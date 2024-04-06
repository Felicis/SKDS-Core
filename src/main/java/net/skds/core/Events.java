package net.skds.core;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.TickEvent.LevelTickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.ServerTickEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.skds.core.api.IWorldExtended;
import net.skds.core.util.blockupdate.BlockUpdataer;
import net.skds.core.util.blockupdate.WWSGlobal;

public class Events {

    private static long inTickTime = System.nanoTime();
    private static int lastTickTime = 0;

    @SubscribeEvent
    public void onWorldUnload(LevelEvent.Unload e) {

        Level w = (Level) e.getLevel();
        WWSGlobal wwsg = ((IWorldExtended) w).getWWS();
        wwsg.unloadWorld(w);
        if (!w.isClientSide) {
            BlockUpdataer.onWorldUnload((ServerLevel) e.getLevel());
        }
    }

    @SubscribeEvent
    public void onWorldLoad(LevelEvent.Load e) {

        Level w = (Level) e.getLevel();
        ((IWorldExtended) w).addWWS();
        // WWSGlobal.loadWorld(w);
        if (!w.isClientSide) {
            BlockUpdataer.onWorldLoad((ServerLevel) w);
        }
    }

    @SubscribeEvent
    public void tick(LevelTickEvent event) {

        boolean in = event.phase == Phase.START;
        Level w = event.level;
        if (in) {
            //System.out.println("W I ========================");
            WWSGlobal wwsg = ((IWorldExtended) w).getWWS();
            wwsg.tickIn();

        }
        //BlockUpdataer.tick(in);
        if (!in) {
            //System.out.println("W O ========================");
            WWSGlobal wwsg = ((IWorldExtended) w).getWWS();
            wwsg.tickOut();

            lastTickTime = (int) (System.nanoTime() - inTickTime);
        }
    }

    @SubscribeEvent
    public void tick(ServerTickEvent event) {
        boolean in = event.phase == Phase.START;
        if (in) {
            inTickTime = System.nanoTime();
            //System.out.println("IN========================");
        }
        BlockUpdataer.tick(in);
        if (!in) {
            lastTickTime = (int) (System.nanoTime() - inTickTime);
            ///System.out.println("OUT========================");
        }
    }

    public static int getLastTickTime() {
        return lastTickTime;
    }

    public static int getRemainingTickTimeNanos() {
        return 50_000_000 - (int) (System.nanoTime() - inTickTime);
    }

    public static int getRemainingTickTimeMicros() {
        return getRemainingTickTimeNanos() / 1000;
    }

    public static int getRemainingTickTimeMilis() {
        return getRemainingTickTimeNanos() / 1000_000;
    }
}