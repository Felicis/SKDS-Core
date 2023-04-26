package net.skds.core.api;

import net.minecraft.world.level.chunk.ChunkAccess;

public interface IServerChunkProvider {
    public ChunkAccess getCustomChunk(long l);
}