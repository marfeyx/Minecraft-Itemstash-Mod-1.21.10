package com.marfeyx.itemstash.network;

import com.marfeyx.itemstash.stash.ItemstashHolder;
import com.marfeyx.itemstash.stash.ItemstashInventory;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;

public final class ItemstashNetworking {
    private ItemstashNetworking() {
    }

    public static void registerServerReceivers() {
        PayloadTypeRegistry.playC2S().register(OpenStashPayload.ID, OpenStashPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(ItemstashActionPayload.ID, ItemstashActionPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(StashSyncPayload.ID, StashSyncPayload.CODEC);

        ServerPlayNetworking.registerGlobalReceiver(OpenStashPayload.ID, (payload, context) ->
                context.server().execute(() -> sync(context.player())));

        ServerPlayNetworking.registerGlobalReceiver(ItemstashActionPayload.ID, (payload, context) ->
                context.server().execute(() -> handleAction(context.player(), payload)));
    }

    public static void sync(ServerPlayerEntity player) {
        ItemstashInventory stash = ((ItemstashHolder) player).itemstash$getStash();
        ServerPlayNetworking.send(player, new StashSyncPayload(stash.entries()));
    }

    private static void handleAction(ServerPlayerEntity player, ItemstashActionPayload payload) {
        ItemstashInventory stash = ((ItemstashHolder) player).itemstash$getStash();

        switch (payload.action()) {
            case ItemstashActionPayload.TAKE_STACK -> stash.takeStack(player, payload.index());
            case ItemstashActionPayload.FILL_INVENTORY -> stash.fillInventory(player, payload.index());
            case ItemstashActionPayload.DROP_ALL -> stash.dropAll(player, payload.index());
            default -> {
            }
        }

        sync(player);
    }
}
