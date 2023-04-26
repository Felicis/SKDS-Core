package net.skds.core.api;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;

public interface IBlockExtraStates {
	
	default void customStatesRegister(Block b, StateDefinition.Builder<Block, BlockState> builder) {		
	}
}