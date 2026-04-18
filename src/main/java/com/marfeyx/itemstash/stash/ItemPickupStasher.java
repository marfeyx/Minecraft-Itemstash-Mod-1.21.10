package com.marfeyx.itemstash.stash;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;

public final class ItemPickupStasher {
    private ItemPickupStasher() {
    }

    public static boolean stashIfInventoryCannotAccept(ServerPlayerEntity player, ItemStack stack) {
        if (stack.isEmpty() || canAcceptAny(player.getInventory(), stack)) {
            return false;
        }

        ((ItemstashHolder) player).itemstash$getStash().add(stack);
        return true;
    }

    private static boolean canAcceptAny(PlayerInventory inventory, ItemStack incoming) {
        for (int slot = 0; slot < PlayerInventory.MAIN_SIZE; slot++) {
            ItemStack current = inventory.getStack(slot);

            if (current.isEmpty()) {
                return true;
            }

            if (ItemStack.areItemsAndComponentsEqual(current, incoming) && current.getCount() < current.getMaxCount()) {
                return true;
            }
        }

        return false;
    }
}
