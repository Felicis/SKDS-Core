package net.skds.core.mixins.multithreading;

import java.util.concurrent.locks.ReentrantLock;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.util.palette.PalettedContainer;

@Mixin(value = { PalettedContainer.class })
public class PalettedContainerMixin<T> {

	@Shadow
	private final ReentrantLock lock = new ReentrantLock();

	@Inject(method = "acquire", at = @At(value = "HEAD"), cancellable = true)
	public void acquire(CallbackInfo ci) {
		lock.lock();
		ci.cancel();
	}

	@Inject(method = "get", at = @At(value = "HEAD"), cancellable = true)
	public synchronized void get(int x, int y, int z, CallbackInfoReturnable<T> ci) {

		ci.setReturnValue(this.get(getIndex(x, y, z)));
	}

	//============================
	@Inject(method = "getAndSet", at = @At(value = "HEAD"), cancellable = true)
	public synchronized void getAndSet(int x, int y, int z, T state, CallbackInfoReturnable<T> ci) {
	
		lock.lock();
		T t = this.getAndSet(getIndex(x, y, z), state);
		lock.unlock();
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