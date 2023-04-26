package net.skds.core.api.multithreading;

import net.minecraft.world.level.Level;

public interface ITaskRunnable extends Runnable {
	
	public boolean revoke(Level w);
	public double getPriority();
	public int getSubPriority();
}