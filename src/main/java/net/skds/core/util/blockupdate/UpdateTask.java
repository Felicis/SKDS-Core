package net.skds.core.util.blockupdate;

import java.util.function.BiConsumer;

import net.minecraft.block.BlockState;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;

public class UpdateTask {
	public final BlockPos pos;
	public final BlockState newState;
	public final BlockState oldState;
	public final int flags;
	public final BiConsumer<UpdateTask, ServerWorld> action;
	//public final boolean drop;

	public UpdateTask(BlockPos pos, BlockState newState, BlockState oldState, int flags, BiConsumer<UpdateTask, ServerWorld> action) {
		this.pos = pos;
		this.flags = flags;
		this.action = action;
		this.newState = newState;
		this.oldState = oldState;
	}

	//@SuppressWarnings("deprecation")
	public void update(ServerWorld w) {
		action.accept(this, w);
		// System.out.println("x");
		/*
		Fluid fluid = newState.getFluidState().getFluid();
		if (fluid != Fluids.EMPTY && !oldState.isAir()
				&& !fluid.isEquivalentTo(oldState.getFluidState().getFluid())
				&& !(oldState.getBlock() instanceof IWaterLoggable)) {
			((IFlowingFluid) fluid).beforeReplacingBlockCustom(w, pos, oldState);
		} else if (drop) {
			TileEntity tileentity = oldState.hasTileEntity() ? w.getTileEntity(pos) : null;
			Block.spawnDrops(oldState, w, pos, tileentity);
		}
		if (newState != oldState) {
			w.markAndNotifyBlock(pos, w.getChunkAt(pos), newState, newState, flags, 512);
		}
		*/
	}

	public void updateClient(ClientWorld w) {
		// System.out.println("x");
		//Fluid fluid = newState.getFluidState().getFluid();
		//if (fluid != Fluids.EMPTY && !oldState.isAir()
		//		&& !FFluidStatic.isSameFluid(fluid, oldState.getFluidState().getFluid())
		//		&& !(oldState.getBlock() instanceof IWaterLoggable)) {
		//	((IFlowingFluid) fluid).beforeReplacingBlockCustom(w, pos, oldState);
		//} else if (drop) {
		//	TileEntity tileentity = oldState.hasTileEntity() ? w.getTileEntity(pos) : null;
		//	Block.spawnDrops(oldState, w, pos, tileentity);
		//}
		//w.markAndNotifyBlock(pos, w.getChunkAt(pos), newState, newState, 3, 512);
		//w.addParticle(ParticleTypes.CLOUD, pos.getX() + 0.5,  pos.getY() + 0.5,  pos.getZ() + 0.5, 0, 0, 0);
		//w.notifyBlockUpdate(pos, oldState, newState, 2);
		w.setBlockAndUpdate(pos, newState);

	}
}