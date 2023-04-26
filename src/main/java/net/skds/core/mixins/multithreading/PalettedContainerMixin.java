package net.skds.core.mixins.multithreading;

import java.util.concurrent.Semaphore;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.world.level.chunk.PalettedContainer;

@Mixin(value = { PalettedContainer.class })
public class PalettedContainerMixin<T> {

	@Shadow
	private final Semaphore lock = new Semaphore(1);

	@Inject(method = "acquire", at = @At(value = "HEAD"), cancellable = true)
	public void acquire(CallbackInfo ci) {
		lock.acquireUninterruptibly();
		ci.cancel();
	}

	@Inject(method = "get", at = @At(value = "HEAD"), cancellable = true)
	public synchronized void get(int x, int y, int z, CallbackInfoReturnable<T> ci) {

		ci.setReturnValue(this.get(getIndex(x, y, z)));
	}

	//============================
	@Inject(method = "getAndSet", at = @At(value = "HEAD"), cancellable = true)
	public synchronized void getAndSet(int x, int y, int z, T state, CallbackInfoReturnable<T> ci) {
	
		lock.acquireUninterruptibly();
		T t = this.getAndSet(getIndex(x, y, z), state);
		lock.release();
		ci.setReturnValue(t);
	}
	//=============================
	@Shadow
	protected T get(int index) {
		return null;
	}

	@Shadow
	protected T getAndSet(int index, T state) {
		return null;
	}

	@Shadow
	private static int getIndex(int x, int y, int z) {
		return 0;
	}

	// @Overwrite
	// public void unlock() {
	// this.lock.unlock();
	// }
}