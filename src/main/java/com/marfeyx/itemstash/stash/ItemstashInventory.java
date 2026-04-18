package com.marfeyx.itemstash.stash;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;

public final class ItemstashInventory {
    private static final String NBT_KEY = "ItemstashItems";

    private final List<ItemStack> stacks = new ArrayList<>();

    public boolean isEmpty() {
        return stacks.isEmpty();
    }

    public List<ItemstashEntry> entries() {
        List<ItemstashEntry> entries = new ArrayList<>();

        for (int index = 0; index < stacks.size(); index++) {
            ItemStack stack = stacks.get(index);
            entries.add(new ItemstashEntry(index, stack.copyWithCount(1), stack.getCount()));
        }

        return Collections.unmodifiableList(entries);
    }

    public void add(ItemStack incoming) {
        if (incoming.isEmpty()) {
            return;
        }

        for (ItemStack stack : stacks) {
            if (ItemStack.areItemsAndComponentsEqual(stack, incoming)) {
                stack.increment(incoming.getCount());
                return;
            }
        }

        stacks.add(incoming.copy());
    }

    public boolean takeStack(ServerPlayerEntity player, int index) {
        return moveToInventory(player, index, false);
    }

    public boolean fillInventory(ServerPlayerEntity player, int index) {
        return moveToInventory(player, index, true);
    }

    public boolean dropAll(ServerPlayerEntity player, int index) {
        ItemStack stack = get(index);

        if (stack.isEmpty()) {
            return false;
        }

        ItemStack dropped = stack.copy();
        stacks.remove(index);
        ItemEntity entity = player.dropItem(dropped, false, true);

        if (entity != null) {
            entity.setOwner(player.getUuid());
        }

        return true;
    }

    public void readData(ReadView view) {
        stacks.clear();

        for (ItemStack stack : view.getTypedListView(NBT_KEY, ItemStack.CODEC)) {
            if (!stack.isEmpty()) {
                stacks.add(stack);
            }
        }
    }

    public void writeData(WriteView view) {
        WriteView.ListAppender<ItemStack> list = view.getListAppender(NBT_KEY, ItemStack.CODEC);

        for (ItemStack stack : stacks) {
            if (!stack.isEmpty()) {
                list.add(stack);
            }
        }
    }

    private boolean moveToInventory(ServerPlayerEntity player, int index, boolean fillAllSpace) {
        ItemStack stored = get(index);

        if (stored.isEmpty()) {
            return false;
        }

        PlayerInventory inventory = player.getInventory();
        int targetCount = fillAllSpace ? stored.getCount() : Math.min(stored.getMaxCount(), stored.getCount());
        ItemStack moving = stored.copyWithCount(targetCount);
        int before = moving.getCount();

        inventory.insertStack(moving);

        int moved = before - moving.getCount();
        if (moved <= 0) {
            return false;
        }

        stored.decrement(moved);
        cleanup();
        player.playerScreenHandler.sendContentUpdates();
        return true;
    }

    private ItemStack get(int index) {
        if (index < 0 || index >= stacks.size()) {
            return ItemStack.EMPTY;
        }

        return stacks.get(index);
    }

    private void cleanup() {
        Iterator<ItemStack> iterator = stacks.iterator();

        while (iterator.hasNext()) {
            if (iterator.next().isEmpty()) {
                iterator.remove();
            }
        }
    }
}
