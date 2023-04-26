package net.skds.core.mixins.custom;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.StateContainer;
import net.minecraft.state.StateContainer.Builder;
import net.skds.core.api.IBlockExtended;
import net.skds.core.api.IBlockExtraStates;
import net.skds.core.util.CustomBlockPars;

@Mixin(Block.class)
public class BlockMixin implements IBlockExtended, IBlockExtraStates {

	private CustomBlockPars customBlockPars = new CustomBlockPars();

	@Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/Block;createBlockStateDefinition(Lnet/minecraft/state/StateContainer$Builder;)V"))
	protected void aaa(Block b, StateContainer.Builder<Block, BlockState> builder) {
		createBlockStateDefinition(builder);
		customStatesRegister(b, builder);
	}

	@Override
	public void customStatesRegister(Block b, StateContainer.Builder<Block, BlockState> builder) {		
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