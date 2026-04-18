package com.marfeyx.itemstash;

import com.marfeyx.itemstash.network.ItemstashNetworking;
import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Itemstash implements ModInitializer {
    public static final String MOD_ID = "itemstash";
    public static final Logger LOGGER = LoggerFactory.getLogger("Itemstash");

    @Override
    public void onInitialize() {
        ItemstashNetworking.registerServerReceivers();
    }

    public static Identifier id(String path) {
        return Identifier.of(MOD_ID, path);
    }
}
