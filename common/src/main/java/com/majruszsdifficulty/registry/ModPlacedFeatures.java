package com.majruszsdifficulty.registry;

import com.majruszsdifficulty.MajruszsDifficulty;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public final class ModPlacedFeatures {
    public static final ResourceKey<PlacedFeature> ENDERIUM_ORE_PLACED_FEATURE = key("enderium_ore");
    public static final ResourceKey<PlacedFeature> ENDERIUM_ORE_LARGE_PLACED_FEATURE = key("enderium_ore_large");
    public static final ResourceKey<PlacedFeature> FRAGILE_END_STONE_PLACED_FEATURE = key("fragile_end_stone");
    public static final ResourceKey<PlacedFeature> FRAGILE_END_STONE_LARGE_PLACED_FEATURE = key("fragile_end_stone_large");
    public static final ResourceKey<PlacedFeature> INFESTED_END_STONE_PLACED_FEATURE = key("infested_end_stone");

    private static ResourceKey<PlacedFeature> key(String name) {
        return ResourceKey.create(Registries.PLACED_FEATURE, MajruszsDifficulty.id(name));
    }

    private ModPlacedFeatures() {
    }
}
