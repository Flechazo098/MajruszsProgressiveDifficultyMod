package com.majruszsdifficulty.platform;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootTable;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;

public final class FabricLootTableIds {
    private static final Map<LootTable, ResourceLocation> IDS = Collections.synchronizedMap(new IdentityHashMap<>());

    public static void register(LootTable table, ResourceLocation id) {
        IDS.put(table, id);
    }

    public static ResourceLocation get(LootTable table) {
        return IDS.getOrDefault(table, ResourceLocation.withDefaultNamespace("empty"));
    }

    public static void clear() {
        IDS.clear();
    }

    private FabricLootTableIds() {
    }
}
