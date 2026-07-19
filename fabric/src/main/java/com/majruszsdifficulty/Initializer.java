package com.majruszsdifficulty;

import cc.sighs.oelib.event.EventAutoRegistration;
import cc.sighs.oelib.event.EventBus;
import com.majruszsdifficulty.events.*;
import com.majruszsdifficulty.platform.FabricEntityDataPlatform;
import com.majruszsdifficulty.platform.FabricLootTableIds;
import com.majruszsdifficulty.registration.CommonRegistration;
import com.majruszsdifficulty.registry.ModEntities;
import com.majruszsdifficulty.registry.ModItems;
import com.majruszsdifficulty.registry.ModPlacedFeatures;
import com.majruszsdifficulty.registry.ModPotions;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectionContext;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.registry.FabricBrewingRecipeRegistryBuilder;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.GenerationStep;

import java.util.function.Predicate;

public class Initializer implements ModInitializer {
    private static final Predicate<BiomeSelectionContext> IS_OVERWORLD = context -> context.canGenerateIn(LevelStem.OVERWORLD)
            && context.getBiomeKey() != Biomes.DEEP_DARK
            && context.getBiomeKey() != Biomes.MUSHROOM_FIELDS;
    private static final Predicate<BiomeSelectionContext> IS_NETHER = context -> context.canGenerateIn(LevelStem.NETHER);
    private static final Predicate<BiomeSelectionContext> IS_END = context -> context.canGenerateIn(LevelStem.END);

    @Override
    public void onInitialize() {
        FabricEntityDataPlatform.registerAttachments();
        CommonRegistration.register();
        EventAutoRegistration.registerAllListeners();
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) ->
                EventBus.post(new ServerPlayerJoinedEvent(handler.getPlayer()))
        );
        ServerLifecycleEvents.SERVER_STARTED.register(server ->
                EventBus.post(new ServerStartedEvent(server))
        );
        ServerEntityEvents.EQUIPMENT_CHANGE.register((entity, slot, from, to) ->
                EventBus.post(new ServerEquipmentChangedEvent(entity, slot, from, to))
        );
        LootTableEvents.ALL_LOADED.register((manager, registry) -> {
            FabricLootTableIds.clear();
            registry.entrySet().forEach(entry -> FabricLootTableIds.register(entry.getValue(), entry.getKey().location()));
        });
        FabricBrewingRecipeRegistryBuilder.BUILD.register(builder -> {
            builder.addMix(Potions.WATER, ModItems.CERBERUS_FANG_ITEM.get(), Potions.MUNDANE);
            builder.addMix(Potions.AWKWARD, ModItems.CERBERUS_FANG_ITEM.get(), MajruszsDifficulty.potionHolder(ModPotions.WITHER_POTION));
            builder.addMix(MajruszsDifficulty.potionHolder(ModPotions.WITHER_POTION), Items.REDSTONE, MajruszsDifficulty.potionHolder(ModPotions.WITHER_LONG_POTION));
            builder.addMix(MajruszsDifficulty.potionHolder(ModPotions.WITHER_POTION), Items.GLOWSTONE_DUST, MajruszsDifficulty.potionHolder(ModPotions.WITHER_STRONG_POTION));
        });

        BiomeModifications.addFeature(IS_END, GenerationStep.Decoration.UNDERGROUND_ORES, ModPlacedFeatures.ENDERIUM_ORE_PLACED_FEATURE);
        BiomeModifications.addFeature(IS_END, GenerationStep.Decoration.UNDERGROUND_ORES, ModPlacedFeatures.ENDERIUM_ORE_LARGE_PLACED_FEATURE);
        BiomeModifications.addFeature(IS_END, GenerationStep.Decoration.UNDERGROUND_ORES, ModPlacedFeatures.FRAGILE_END_STONE_PLACED_FEATURE);
        BiomeModifications.addFeature(IS_END, GenerationStep.Decoration.UNDERGROUND_ORES, ModPlacedFeatures.FRAGILE_END_STONE_LARGE_PLACED_FEATURE);
        BiomeModifications.addFeature(IS_END, GenerationStep.Decoration.UNDERGROUND_ORES, ModPlacedFeatures.INFESTED_END_STONE_PLACED_FEATURE);

        BiomeModifications.addSpawn(IS_OVERWORLD, MobCategory.MONSTER, ModEntities.CURSED_ARMOR_ENTITY.get(), 30, 1, 3);
        BiomeModifications.addSpawn(IS_OVERWORLD, MobCategory.MONSTER, ModEntities.ILLUSIONER_ENTITY.get(), 8, 1, 1);
        BiomeModifications.addSpawn(IS_OVERWORLD, MobCategory.MONSTER, ModEntities.TANK_ENTITY.get(), 3, 1, 1);
        BiomeModifications.addSpawn(IS_NETHER, MobCategory.MONSTER, ModEntities.CERBERUS_ENTITY.get(), 1, 1, 1);
    }
}
