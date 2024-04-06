package net.skds.core.util.blockupdate;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.BiConsumer;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.Entity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.skds.core.Events;
import net.skds.core.SKDSCoreConfig;

public class BlockUpdataer {

    private static final ConcurrentLinkedQueue<Entity> entitiesToAdd = new ConcurrentLinkedQueue<>();

    private static float upus = 0.1F;
    private static int avgTime = 0;

    private static int avgTickTime = 0;
    private static int[] timeTickList = new int[14];
    private static int indexT2 = 0;

    private static float RemMRUC = 0;
    private static int MRUC = 0;
    private static int updateCounter = 0;
    private static int indexUp = 0;
    private static int indexT = 0;
    private static int indexForFill = 0;
    private static float[] upusList = new float[32];
    private static int[] timeList = new int[32];

    public static float getUPus() {
        return upus;
    }

    public static int getAvgTime() {
        return avgTime;
    }

    public static boolean applyTask(float weight) {
        boolean bl = RemMRUC > 0F;
        if (bl) {
            RemMRUC -= weight;
        }
        return bl;
    }

    public static int getMaxRecomendedUpdateCount() {
        return MRUC;
    }

    private static void calcUPus(int time) {
        // long T1 = System.nanoTime();
        int lenUp = upusList.length;
        if (indexUp >= lenUp) {
            indexUp = 0;
            indexForFill = lenUp;
        }
        indexForFill = Math.max(indexUp, indexForFill);

        if (time > 0) {
            upusList[indexUp] = (float) (1F * (float) updateCounter / (float) time);
            ++indexUp;
        }
        if (indexForFill > 0) {
            float sum = 0;
            for (int i = 0; i < indexForFill; ++i) {
                sum += upusList[i];
            }
            upus = sum / indexForFill;
        }

        int lenT = timeList.length;
        ++indexT;
        if (indexT >= lenT) {
            indexT = 0;
        }
        timeList[indexT] = time;
        int sum = 0;
        int c = 0;
        for (int i : timeList) {
            sum += i;
            ++c;
        }
        avgTime = sum / c;
    }

    private static void calcMidTickTime() {

        int lenT = timeTickList.length;
        ++indexT2;
        if (indexT2 >= lenT) {
            indexT2 = 0;
        }
        int t = Events.getLastTickTime() / 1000;
        timeTickList[indexT2] = t;
        int sum = 0;
        int c = 0;
        for (int i : timeTickList) {
            sum += i;
            ++c;
        }
        avgTickTime = sum / c;
    }

    private static void calcMRUC() {
        int RT = 50_000 - avgTickTime;
        int RTavgF = RT;
        int PreMRUC = (int) ((float) RTavgF * upus);
        MRUC = Math.max(SKDSCoreConfig.COMMON.minBlockUpdates.get(), PreMRUC);
        RemMRUC = MRUC;
        // System.out.println(avgTickTime);
    }

    // private static Map<ServerWorld, ConcurrentHashMap<Long, UpdateTask>>
    // BLOCK_UPDATES_WORLDS = new HashMap<>();
    private static Map<ServerLevel, ConcurrentLinkedQueue<UpdateTask>> BLOCK_UPDATES_WORLDS = new HashMap<>();

    public static void onWorldLoad(ServerLevel w) {
        ConcurrentLinkedQueue<UpdateTask> map = new ConcurrentLinkedQueue<>();
        BLOCK_UPDATES_WORLDS.put(w, map);
    }

    public static void onWorldUnload(ServerLevel w) {
        BLOCK_UPDATES_WORLDS.remove(w);
        entitiesToAdd.forEach((e) -> {
            if (e.level() == w) {

                e.level().addFreshEntity(e);
                entitiesToAdd.remove(e);
            }
        });
    }

    public static void addUpdate(ServerLevel w, BlockPos pos, BlockState newState, BlockState oldState, int flags, BiConsumer<UpdateTask, ServerLevel> action) {
        if (newState == oldState) {
            return;
        }
        ConcurrentLinkedQueue<UpdateTask> bul = BLOCK_UPDATES_WORLDS.get(w);
        if (bul == null) {
            return;
        }
        bul.add(new UpdateTask(pos, newState, oldState, flags, action));
    }

    private static int inint = 0;

    public static void tick(boolean in) {
        long initT = System.nanoTime();
        Entity e;

        if (in) {

            while ((e = entitiesToAdd.poll()) != null) {
                e.level().addFreshEntity(e);
            }
            BLOCK_UPDATES_WORLDS.forEach((w, set) -> {
                //w.getPlayers().forEach((p) -> {
                //    PacketHandler.send(PacketDistributor.PLAYER.with(() -> p), new BlocksUpdatePacket(set));
                //});

                while (!set.isEmpty()) {
                    UpdateTask task = set.poll();
                    task.update(w);
                    ++updateCounter;
                }
            });
        }

        int time = (int) (System.nanoTime() - initT) / 1000;
        if (in) {
            inint = time;
        } else {
            calcMidTickTime();
            calcUPus((int) (time + inint /*+ FluidTasksManager.getLastTimeMicros()*/));
            calcMRUC();
            updateCounter = 0;
            // System.out.println(MRUC);
        }
    }

    public static void addEntity(Entity e) {
        entitiesToAdd.add(e);
    }
}