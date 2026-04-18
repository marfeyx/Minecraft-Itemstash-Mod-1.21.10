package com.marfeyx.itemstash.network;

import com.marfeyx.itemstash.Itemstash;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

public record OpenStashPayload() implements CustomPayload {
    public static final CustomPayload.Id<OpenStashPayload> ID = new CustomPayload.Id<>(Itemstash.id("open"));
    public static final PacketCodec<RegistryByteBuf, OpenStashPayload> CODEC = PacketCodec.unit(new OpenStashPayload());

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
