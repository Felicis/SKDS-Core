package net.skds.core.events;

import net.minecraft.world.level.Level;
import net.minecraftforge.eventbus.api.Event;
import net.skds.core.util.blockupdate.WWSGlobal;

public class OnWWSAttachEvent extends Event {
	private final Level world;
	private final WWSGlobal wwsGlobal;

	public OnWWSAttachEvent(Level w, WWSGlobal wws) {
		this.world = w;
		this.wwsGlobal = wws;
	}

	public Level getWorld() {
		return world;
	}

	public WWSGlobal getWWS() {
		return wwsGlobal;
	}
}