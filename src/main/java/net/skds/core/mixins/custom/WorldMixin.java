package net.skds.core.mixins.custom;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.world.level.Level;
import net.skds.core.api.IWorldExtended;
import net.skds.core.util.blockupdate.WWSGlobal;

@Mixin(Level.class)
public class WorldMixin implements IWorldExtended {

	private WWSGlobal wwsg = null;;

	@Override
	public WWSGlobal getWWS() {
		return wwsg;
	}

	@Override
	public void addWWS() {
		wwsg = new WWSGlobal((Level) (Object) this);
	}

}