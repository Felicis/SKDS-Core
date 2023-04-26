package net.skds.core.mixins.multithreading;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.server.ChunkHolder;
import net.minecraft.world.server.ServerChunkProvider;
import net.minecraft.world.server.ServerWorld;
import net.skds.core.api.multithreading.ISKDSThread;
import net.skds.core.api.IServerChunkProvider;

@Mixin(value = { ServerChunkProvider.class })
public abstract class ServerChunkProviderMixin implements IServerChunkProvider {

    @Final
    @Shadow
    public ServerWorld level;
    @Final
    @Shadow
    private Thread mainThread;

    public IChunk getCustomChunk(long l) {
        ChunkHolder chunkHolder = this.getVisibleChunkIfPresent(l);
        if (chunkHolder == null) {
            return null;
        }
        return chunkHolder.getLastAvailable();
    }

    @Shadow
    private ChunkHolder getVisibleChunkIfPresent(long l) {
        return null;
    }

	@Redirect(method = "getChunk", at = @At(value = "INVOKE", ordinal = 0, target = "Ljava/lang/Thread;currentThread()Ljava/lang/Thread;"))
	public Thread aaa(int x, int z, ChunkStatus status, boolean b) {
		return mainThread;
	}

    @Inject(method = "storeInCache", at = @At(value = "HEAD"), cancellable = true)
    private void swapp(long l, IChunk ic, ChunkStatus cs, CallbackInfo ci) {
        if (Thread.currentThread() != mainThread) {
            ci.cancel();
        }
    }

	@Inject(method = "blockChanged", at = @At(value = "HEAD", ordinal = 0), cancellable = true)
	public synchronized void blockChanged(BlockPos pos, CallbackInfo ci) {
		if (Thread.currentThread() instanceof ISKDSThread) {
			int i = pos.getX() >> 4;
			int j = pos.getZ() >> 4;
			ChunkHolder chunkholder = this.getVisibleChunkIfPresent(ChunkPos.asLong(i, j));
			if (chunkholder != null) {
				chunkholder.blockChanged(pos);
			}
			ci.cancel();
		}

	}
}