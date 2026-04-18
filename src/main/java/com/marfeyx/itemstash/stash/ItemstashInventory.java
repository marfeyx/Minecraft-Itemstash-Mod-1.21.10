package com.marfeyx.itemstash.stash;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.math.Vec3d;

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

    public void copyFrom(ItemstashInventory other) {
        stacks.clear();

        for (ItemStack stack : other.stacks) {
            if (!stack.isEmpty()) {
                stacks.add(stack.copy());
            }
        }
    }

    public boolean takeStack(ServerPlayerEntity player, int index) {
        return moveToInventory(player, index, false);
    }

    public boolean fillInventory(ServerPlayerEntity player, int index) {
        return moveToInventory(player, index, true);
    }

    public boolean dropAll(ServerPlayerEntity player, int index) {
        ItemStack stack = get(index);

        if (stack.isEmpty() || !(player.getEntityWorld() instanceof ServerWorld world)) {
            return false;
        }

        int remaining = stack.getCount();
        boolean droppedAny = false;

        while (remaining > 0) {
            int amount = Math.min(stack.getMaxCount(), remaining);
            ItemStack dropped = stack.copyWithCount(amount);
            ItemEntity entity = createDroppedItem(player, world, dropped);

            entity.setOwner(player.getUuid());
            entity.setThrower(player);
            entity.setToDefaultPickupDelay();

            if (!world.spawnEntity(entity)) {
                break;
            }

            remaining -= amount;
            droppedAny = true;
        }

        if (!droppedAny) {
            return false;
        }

        if (remaining <= 0) {
            stacks.remove(index);
        } else {
            stack.setCount(remaining);
        }

        return true;
    }

    private ItemEntity createDroppedItem(ServerPlayerEntity player, ServerWorld world, ItemStack stack) {
        Vec3d look = player.getRotationVec(1.0F).normalize();
        Vec3d pos = player.getEyePos().add(look.multiply(0.9D)).add(0.0D, -0.25D, 0.0D);
        Vec3d velocity = look.multiply(0.35D).add(0.0D, 0.12D, 0.0D);

        return new ItemEntity(world, pos.x, pos.y, pos.z, stack, velocity.x, velocity.y, velocity.z);
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
