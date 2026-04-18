package com.marfeyx.itemstash.mixin;

import com.marfeyx.itemstash.stash.ItemstashHolder;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin {
    @Inject(method = "copyFrom", at = @At("TAIL"))
    private void itemstash$copyStashOnRespawn(ServerPlayerEntity oldPlayer, boolean alive, CallbackInfo ci) {
        ServerPlayerEntity newPlayer = (ServerPlayerEntity) (Object) this;

        ((ItemstashHolder) newPlayer).itemstash$getStash().copyFrom(
                ((ItemstashHolder) oldPlayer).itemstash$getStash()
        );
    }
}
