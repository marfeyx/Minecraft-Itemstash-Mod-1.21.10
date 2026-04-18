package com.marfeyx.itemstash.network;

import com.marfeyx.itemstash.Itemstash;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

public record ItemstashActionPayload(int index, String action) implements CustomPayload {
    public static final CustomPayload.Id<ItemstashActionPayload> ID = new CustomPayload.Id<>(Itemstash.id("action"));
    public static final PacketCodec<RegistryByteBuf, ItemstashActionPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.VAR_INT,
            ItemstashActionPayload::index,
            PacketCodecs.STRING,
            ItemstashActionPayload::action,
            ItemstashActionPayload::new
    );

    public static final String TAKE_STACK = "take_stack";
    public static final String FILL_INVENTORY = "fill_inventory";
    public static final String DROP_ALL = "drop_all";

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
