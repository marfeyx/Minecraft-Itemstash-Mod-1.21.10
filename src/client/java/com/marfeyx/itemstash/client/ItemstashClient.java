package com.marfeyx.itemstash.client;

import com.marfeyx.itemstash.Itemstash;
import com.marfeyx.itemstash.network.OpenStashPayload;
import com.marfeyx.itemstash.network.StashSyncPayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class ItemstashClient implements ClientModInitializer {
    private static KeyBinding openStashKey;

    @Override
    public void onInitializeClient() {
        openStashKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.itemstash.open",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_B,
                KeyBinding.Category.create(Itemstash.id("itemstash"))
        ));

        ClientPlayNetworking.registerGlobalReceiver(StashSyncPayload.ID, (payload, context) ->
                context.client().execute(() -> MinecraftClient.getInstance().setScreen(new ItemstashScreen(payload.entries()))));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openStashKey.wasPressed()) {
                if (client.player != null && client.getNetworkHandler() != null) {
                    ClientPlayNetworking.send(new OpenStashPayload());
                }
            }
        });
    }
}
