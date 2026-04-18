package com.marfeyx.itemstash.network;

import com.marfeyx.itemstash.Itemstash;
import com.marfeyx.itemstash.stash.ItemstashEntry;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

public record StashSyncPayload(List<ItemstashEntry> entries) implements CustomPayload {
    public static final CustomPayload.Id<StashSyncPayload> ID = new CustomPayload.Id<>(Itemstash.id("sync"));
    public static final PacketCodec<RegistryByteBuf, StashSyncPayload> CODEC = new PacketCodec<>() {
        @Override
        public StashSyncPayload decode(RegistryByteBuf buf) {
            int size = buf.readVarInt();
            List<ItemstashEntry> entries = new ArrayList<>(size);

            for (int i = 0; i < size; i++) {
                int index = buf.readVarInt();
                ItemStack stack = ItemStack.PACKET_CODEC.decode(buf);
                int count = buf.readVarInt();
                entries.add(new ItemstashEntry(index, stack, count));
            }

            return new StashSyncPayload(entries);
        }

        @Override
        public void encode(RegistryByteBuf buf, StashSyncPayload payload) {
            buf.writeVarInt(payload.entries().size());

            for (ItemstashEntry entry : payload.entries()) {
                buf.writeVarInt(entry.index());
                ItemStack.PACKET_CODEC.encode(buf, entry.stack());
                buf.writeVarInt(entry.count());
            }
        }
    };

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
