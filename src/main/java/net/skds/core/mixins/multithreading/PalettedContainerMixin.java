package net.skds.core.mixins.multithreading;

import net.minecraft.world.level.chunk.PalettedContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = { PalettedContainer.class })
public abstract class PalettedContainerMixin<T> {

//	@Inject(method = "acquire", at = @At(value = "HEAD"), cancellable = true)
//	public void acquire(CallbackInfo ci) {
//		lock.acquireUninterruptibly();  // TODO have not found equivalent - therefore just locking normally, no inject required
//		ci.cancel();
//	}
	@Mixin(value = {PalettedContainer.Strategy.class})
	public abstract static class StrategyMixin{
		@Shadow
		public int getIndex(int x, int y, int z) {
			return 0;
		}
	}

//	@Shadow public abstract void release();
//
//	@Shadow public abstract void acquire();

	// TODO inject was probably already redundant: static getIndex was already overridden and otherwise no different
//	@Inject(method = "get(III)Ljava/lang/Object;", at = @At(value = "HEAD"), cancellable = true)
//	public synchronized void get(int x, int y, int z, CallbackInfoReturnable<T> ci) {
//
//		ci.setReturnValue(this.get(this.strategy.getIndex(x, y, z)));
//	}

	//============================
	// TODO inject was probably already redundant: static getIndex was already overridden and otherwise no different
//	@Inject(method = "getAndSet(IIILjava/lang/Object;)Ljava/lang/Object;", at = @At(value = "HEAD"), cancellable = true)
//	public synchronized void getAndSet(int x, int y, int z, T state, CallbackInfoReturnable<T> ci) {
//		this.acquire();
//		T t = this.getAndSet(this.strategy.getIndex(x, y, z), state);
//		this.release();
//		ci.setReturnValue(t);
//	}
	//=============================
	@Shadow
	protected T get(int index) {
		return null;
	}

	@Shadow
	private T getAndSet(int index, T state) {
		return null;
	}

	// @Overwrite
	// public void unlock() {
	// this.lock.unlock();
	// }
}