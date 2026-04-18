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
    private static final int PLAYER_DROPPED_STASH_DELAY_TICKS = 15 * 20;

    @Shadow
    public abstract net.minecraft.item.ItemStack getStack();

    @Inject(method = "onPlayerCollision", at = @At("HEAD"), cancellable = true)
    private void itemstash$captureOverflowPickup(PlayerEntity player, CallbackInfo ci) {
        if (!(player instanceof ServerPlayerEntity serverPlayer)) {
            return;
        }

        ItemEntity itemEntity = (ItemEntity) (Object) this;
        if (itemEntity.getOwner() instanceof PlayerEntity && itemEntity.getItemAge() < PLAYER_DROPPED_STASH_DELAY_TICKS) {
            return;
        }

        if (ItemPickupStasher.stashIfInventoryCannotAccept(serverPlayer, getStack())) {
            itemEntity.discard();
            ci.cancel();
        }
    }
}
