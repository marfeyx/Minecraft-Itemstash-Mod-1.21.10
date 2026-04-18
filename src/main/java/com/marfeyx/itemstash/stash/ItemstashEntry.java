package com.marfeyx.itemstash.stash;

import net.minecraft.item.ItemStack;

public record ItemstashEntry(int index, ItemStack stack, int count) {
}
