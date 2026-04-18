package com.marfeyx.itemstash.mixin;

import com.marfeyx.itemstash.stash.ItemPickupStasher;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin {
    @Shadow
    public abstract net.minecraft.item.ItemStack getStack();

    @Inject(method = "onPlayerCollision", at = @At("HEAD"), cancellable = true)
    private void itemstash$captureOverflowPickup(PlayerEntity player, CallbackInfo ci) {
        if (!(player instanceof ServerPlayerEntity serverPlayer)) {
            return;
        }

        if (ItemPickupStasher.stashIfInventoryCannotAccept(serverPlayer, getStack())) {
            ((ItemEntity) (Object) this).discard();
            ci.cancel();
        }
    }
}
