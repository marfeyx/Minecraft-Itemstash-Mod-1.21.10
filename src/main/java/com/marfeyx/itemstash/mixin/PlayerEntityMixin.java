package com.marfeyx.itemstash.mixin;

import com.marfeyx.itemstash.stash.ItemstashHolder;
import com.marfeyx.itemstash.stash.ItemstashInventory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin implements ItemstashHolder {
    @Unique
    private final ItemstashInventory itemstash$stash = new ItemstashInventory();

    @Override
    public ItemstashInventory itemstash$getStash() {
        return itemstash$stash;
    }

    @Inject(method = "readCustomData", at = @At("TAIL"))
    private void itemstash$readStash(ReadView view, CallbackInfo ci) {
        itemstash$stash.readData(view);
    }

    @Inject(method = "writeCustomData", at = @At("TAIL"))
    private void itemstash$writeStash(WriteView view, CallbackInfo ci) {
        itemstash$stash.writeData(view);
    }
}
