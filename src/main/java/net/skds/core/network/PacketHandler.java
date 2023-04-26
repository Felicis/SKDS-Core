package net.skds.core.network;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fmllegacy.network.NetworkRegistry;
import net.minecraftforge.fmllegacy.network.PacketDistributor;
import net.minecraftforge.fmllegacy.network.simple.SimpleChannel;
import net.skds.core.SKDSCore;

public class PacketHandler {
	private static final String PROTOCOL_VERSION = "1";
	private static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(new ResourceLocation(SKDSCore.MOD_ID, "network"), () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);

	public static void send(PacketDistributor.PacketTarget target, Object message) {
		CHANNEL.send(target, message);
	}
	
	public static SimpleChannel get() {
		return CHANNEL;
	}

	public static void init() {
		int id = 0;
		CHANNEL.registerMessage(id++, DebugPacket.class, DebugPacket::encoder, DebugPacket::decoder, DebugPacket::handle);
	}
}