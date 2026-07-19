package com.majruszsdifficulty;

import cc.sighs.oelib.registry.RegisterSupplier;
import com.majruszsdifficulty.registry.ModAdvancements;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.alchemy.Potion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class MajruszsDifficulty {
    public static final String MOD_ID = "majruszsdifficulty";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }

    public static ModelLayerLocation layer(String path) {
        return layer(path, "main");
    }

    public static ModelLayerLocation layer(String path, String layer) {
        return new ModelLayerLocation(id(path), layer);
    }

    public static void triggerAdvancement(ServerPlayer player, String id) {
        ModAdvancements.BASIC_ADVANCEMENT.trigger(player, id);
    }

    public static Holder<MobEffect> effectHolder(RegisterSupplier<? extends MobEffect> effect) {
        return BuiltInRegistries.MOB_EFFECT.wrapAsHolder(effect.get());
    }

    public static Holder<ArmorMaterial> armorMaterialHolder(RegisterSupplier<? extends ArmorMaterial> material) {
        return BuiltInRegistries.ARMOR_MATERIAL.wrapAsHolder(material.get());
    }

    public static Holder<Potion> potionHolder(RegisterSupplier<? extends Potion> potion) {
        return BuiltInRegistries.POTION.wrapAsHolder(potion.get());
    }

    private MajruszsDifficulty() {
    }
}
