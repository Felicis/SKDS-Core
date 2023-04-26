package net.skds.core.mixins.custom;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.skds.core.api.IBlockExtended;
import net.skds.core.api.IBlockExtraStates;
import net.skds.core.util.CustomBlockPars;

@Mixin(Block.class)
public class BlockMixin implements IBlockExtended, IBlockExtraStates {

	private CustomBlockPars customBlockPars = new CustomBlockPars();

	@Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/Block;createBlockStateDefinition(Lnet/minecraft/world/level/block/state/StateDefinition$Builder;)V"))
	protected void aaa(Block b, StateDefinition.Builder<Block, BlockState> builder) {
		createBlockStateDefinition(builder);
		customStatesRegister(b, builder);
	}

	@Override
	public void customStatesRegister(Block b, StateDefinition.Builder<Block, BlockState> builder) {		
	}

	@Shadow
	protected final void registerDefaultState(BlockState state) {
	}

	@Shadow
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
	}

	@Override
	public CustomBlockPars getCustomBlockPars() {
		return customBlockPars;
	}

	@Override
	public void setCustomBlockPars(CustomBlockPars param) {
		customBlockPars = param;
	}
}