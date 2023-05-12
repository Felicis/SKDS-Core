package net.skds.core.util.blockupdate;

import java.util.HashSet;
import java.util.Set;
import java.util.function.BiConsumer;

import net.minecraft.core.SectionPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.network.PacketDistributor;
import net.skds.core.network.DebugPacket;
import net.skds.core.network.PacketHandler;
import net.skds.core.api.IServerChunkProvider;
import net.skds.core.api.IWWS;

public abstract class BasicExecutor implements Runnable {

	protected final BlockState nullreturnstate = Blocks.BARRIER.defaultBlockState();
	protected final ServerLevel w;
	protected final IWWS owner;
	protected final BiConsumer<UpdateTask, ServerLevel> action;
	protected Set<BlockPos> banPoses = new HashSet<>();
	protected boolean cancel = false;

	protected BasicExecutor(ServerLevel w, BiConsumer<UpdateTask, ServerLevel> action, IWWS owner) {
		this.owner = owner;
		this.w = w;
		this.action = action;
	}

	protected abstract void applyAction(BlockPos pos, BlockState newState, BlockState oldState, ServerLevel w);

	protected BlockState setState(BlockPos pos, BlockState newState) {
		/*
		 * Sets a block state into this world.Flags are as follows: 1 will cause a block
		 * update. 2 will send the change to clients. 4 will prevent the block from
		 * being re-rendered. 8 will force any re-renders to run on the main thread
		 * instead 16 will prevent neighbor reactions (e.g. fences connecting, observers
		 * pulsing). 32 will prevent neighbor reactions from spawning drops. 64 will
		 * signify the block is being moved. Flags can be OR-ed
		 */
		// BlockState oldState = w.getBlockState(pos);
		// BlockState oldState = getBlockState(pos);
		// System.out.println(oldState);
		// System.out.println(
		// w.setBlockState(pos, newState, 3));
		// BlockState oldState = setFinBlockState(pos, newState);
		// w.markAndNotifyBlock(pos, (Chunk) getChunk(pos), newState, newState, 3, 512);

		// *
		BlockState oldState = setFinBlockState(pos, newState);
		if (oldState != null) {
			applyAction(pos, newState, oldState, w);
			//if ((newState.getFluidState().isEmpty() ^ oldState.getFluidState().isEmpty())
			//		&& (newState.getOpacity(w, pos) != oldState.getOpacity(w, pos)
			//				|| newState.getLightValue(w, pos) != oldState.getLightValue(w, pos)
			//				|| newState.isTransparent() || oldState.isTransparent())) {
			//	w.getChunkProvider().getLightManager().checkBlock(pos);
			//}

			// BlockUpdataer.addUpdate(w, pos, newState, oldState, 3, action);
			//action.accept(new UpdateTask(pos, newState, oldState, 3, action), w);
			//synchronized (w) {
			//	action.accept(new UpdateTask(pos, newState, oldState, 3, action), w);
			//}
			// w.markAndNotifyBlock(pos, (Chunk) getChunk(pos), newState, newState, 3, 512);
			// w.notifyBlockUpdate(pos, newState, newState, 3);
			// w.notifyNeighborsOfStateChange(pos, newState.getBlock());
		}
		// */
		return oldState;
	}

	private ChunkAccess chunkCash = null;
	private long chunkPosCash = 0;
	private boolean newChunkCash = true;

	protected ChunkAccess getChunk(int blockX, int blockZ) {
		long lpos = ChunkPos.asLong(blockX >> 4, blockZ >> 4);
		if (newChunkCash || lpos != chunkPosCash) {
			newChunkCash = false;
			ServerChunkCache prov = (ServerChunkCache) w.getChunkSource();
			chunkCash = ((IServerChunkProvider) prov).getCustomChunk(lpos);
			chunkPosCash = lpos;
		}
		return chunkCash;
	}

	protected ChunkAccess getChunk(BlockPos cPos) {
		// return w.getChunk(cPos);
		return getChunk(cPos.getX(), cPos.getZ());
	}

	protected BlockState getBlockState(BlockPos pos) {
		ChunkAccess chunk = getChunk(pos);
		if (chunk == null) {
			cancel = true;
			return nullreturnstate;
		}
		BlockState ssss = chunk.getBlockState(pos);
		if (ssss == null) {
			return nullreturnstate;
		}

		return ssss;
	}

	protected BlockState setFinBlockState(BlockPos pos, BlockState state) {
		ChunkAccess chunk = getChunk(pos);
		if (!(chunk instanceof LevelChunk)) {
			return null;
		}
		LevelChunkSection levelChunkSection = chunk.getSection(chunk.getSectionIndex(pos.getY()));
		BlockState setted = levelChunkSection.setBlockState(pos.getX() & 15, pos.getY() & 15,
				pos.getZ() & 15, state);

		return setted;
	}

	protected boolean isAir(BlockState statex) {
		return statex.getMaterial() == Material.AIR;
	}

	// ============== ENTITY ================= //

	protected BlockEntity getTileEntity(BlockPos pos) {
		ChunkAccess ich = getChunk(pos);
		BlockEntity tileentity = null;

		if (ich instanceof LevelChunk) {
			tileentity = ((LevelChunk) getChunk(pos)).getBlockEntity(pos, LevelChunk.EntityCreationType.IMMEDIATE);
		}

		// if (tileentity == null) {
		// tileentity = this.getPendingTileEntityAt(pos);
		// }

		return tileentity;
	}

	protected void addEntity(Entity e) {
		BlockUpdataer.addEntity(e);
		/*
		 * if (((ServerWorldAccessor) w).hasDuplicateEntityInv(entityIn)) { return
		 * false; } //else { // if
		 * (net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new //
		 * net.minecraftforge.event.entity.EntityJoinWorldEvent(entityIn, this))) return
		 * // false; IChunk ichunk = this.getChunk((int) Math.floor(entityIn.getPosX()),
		 * (int) Math.floor(entityIn.getPosZ()));
		 * 
		 * if (!(ichunk instanceof Chunk)) { return false; } else {
		 * ichunk.addEntity(entityIn); ((ServerWorldAccessor)
		 * w).onEntityAddedInv(entityIn); return true; } // }
		 */
	}

	// ==============================
	public static Direction dirFromVec(BlockPos pos1, BlockPos pos2) {
		return Direction.getNearest(pos2.getX() - pos1.getX(), pos2.getY() - pos1.getY(),
				pos2.getZ() - pos1.getZ());
	}

	protected void debug(BlockPos pos) {
		w.players().forEach((p) -> {
			PacketHandler.send(PacketDistributor.PLAYER.with(() -> p), new DebugPacket(pos));
		});
	}
}