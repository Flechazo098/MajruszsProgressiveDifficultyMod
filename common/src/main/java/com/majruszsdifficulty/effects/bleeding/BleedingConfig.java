package com.majruszsdifficulty.effects.bleeding;

import cc.sighs.oelib.config.ConfigSchema;
import cc.sighs.oelib.config.ConfigUnit;
import cc.sighs.oelib.config.codecs.ConfigMetaCodec;
import cc.sighs.oelib.config.field.ConfigField;
import cc.sighs.oelib.config.model.ConfigStorageFormat;
import com.majruszsdifficulty.MajruszsDifficulty;
import com.majruszsdifficulty.gamestage.GameStage;
import com.majruszsdifficulty.internal.entity.EffectDef;
import com.mojang.serialization.Codec;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Map;

public record BleedingConfig(
        boolean isEnabled,
        boolean canBeCuredWithGoldenApples,
        boolean isApplicableToAnimals,
        boolean isApplicableToIllagers,
        List<EntityType<?>> otherApplicableMobs,
        List<EntityType<?>> immuneMobs,
        Map<String, EffectDef> effects,
        ArmorProtection armorProtection,
        Sources sources
) {
    private static final Map<String, EffectDef> DEFAULT_EFFECTS = Map.of(
            "__default", new EffectDef(() -> null, 0, 24.0f),
            GameStage.EXPERT_ID, new EffectDef(() -> null, 1, 24.0f),
            GameStage.MASTER_ID, new EffectDef(() -> null, 2, 24.0f)
    );
    private static final List<BleedingSources.Tools.ToolDef> DEFAULT_TOOLS = List.of(
            new BleedingSources.Tools.ToolDef("minecraft:trident", 0.3f, List.of(new BleedingSources.Tools.EnchantmentDef("minecraft:impaling", 0.04f))),
            new BleedingSources.Tools.ToolDef("{regex}.*_sword", 0.3f, List.of(
                    new BleedingSources.Tools.EnchantmentDef("minecraft:sharpness", 0.04f),
                    new BleedingSources.Tools.EnchantmentDef("{regex}(minecraft:smite)|(minecraft:bane_of_arthropods)|(majruszsenchantments:misanthropy)", 0.02f)
            )),
            new BleedingSources.Tools.ToolDef("{regex}.*_axe", 0.26f, List.of(
                    new BleedingSources.Tools.EnchantmentDef("minecraft:sharpness", 0.04f),
                    new BleedingSources.Tools.EnchantmentDef("{regex}(minecraft:smite)|(minecraft:bane_of_arthropods)|(majruszsenchantments:misanthropy)", 0.02f)
            )),
            new BleedingSources.Tools.ToolDef("{regex}(.*_pickaxe)|minecraft:shears", 0.22f),
            new BleedingSources.Tools.ToolDef("{regex}.*_shovel", 0.18f),
            new BleedingSources.Tools.ToolDef("{regex}.*_hoe", 0.14f)
    );
    public static final ConfigUnit<BleedingConfig> UNIT = ConfigSchema.defineServer(
            MethodHandles.lookup(),
            ResourceLocation.fromNamespaceAndPath(MajruszsDifficulty.MOD_ID, "bleeding"),
            BleedingConfig.class,
            meta -> meta.directory(MajruszsDifficulty.MOD_ID).fileName("bleeding").format(ConfigStorageFormat.JSON),
            schema -> schema.group(
                    ConfigField.bool("is_enabled").tooltip().defaultValue(true).forGetter(BleedingConfig::isEnabled),
                    ConfigField.bool("can_be_cured_with_golden_apples").tooltip().defaultValue(true).forGetter(BleedingConfig::canBeCuredWithGoldenApples),
                    ConfigField.bool("is_applicable_to_animals").tooltip().defaultValue(true).forGetter(BleedingConfig::isApplicableToAnimals),
                    ConfigField.bool("is_applicable_to_pillagers").tooltip().defaultValue(true).forGetter(BleedingConfig::isApplicableToIllagers),
                    ConfigField.list("other_applicable_mobs", BuiltInRegistries.ENTITY_TYPE.byNameCodec()).tooltip().defaultValue(List.of(EntityType.PLAYER, EntityType.VILLAGER)).forGetter(BleedingConfig::otherApplicableMobs),
                    ConfigField.list("immune_mobs", BuiltInRegistries.ENTITY_TYPE.byNameCodec()).tooltip().defaultValue(List.of(EntityType.ZOMBIE_HORSE, EntityType.SKELETON_HORSE)).forGetter(BleedingConfig::immuneMobs),
                    ConfigField.map("effect", Codec.STRING, EffectDef.CODEC).tooltip().defaultValue(DEFAULT_EFFECTS).forGetter(BleedingConfig::effects),
                    ConfigSchema.record("armor_protection", ArmorProtection.class, armorCodec(), BleedingConfig::armorProtection),
                    ConfigSchema.record("sources", Sources.class, sourcesCodec(), BleedingConfig::sources)
            ).apply(schema, BleedingConfig::new)
    );

    private static ConfigMetaCodec<ArmorProtection> armorCodec() {
        return ConfigSchema.metaCodec(ArmorProtection.class, schema -> schema.group(
                ConfigField.doubleRange("chance_multiplier_base", 0.0, 1.0).tooltip().defaultValue(0.09).forGetter(ArmorProtection::base),
                ConfigField.doubleRange("chance_multiplier_per_armor", 0.0, 1.0).tooltip().defaultValue(0.03).forGetter(ArmorProtection::perArmor),
                ConfigField.doubleRange("chance_multiplier_per_armor_toughness", 0.0, 1.0).tooltip().defaultValue(0.03).forGetter(ArmorProtection::perToughness)
        ).apply(schema, ArmorProtection::new));
    }

    private static ConfigMetaCodec<Sources> sourcesCodec() {
        return ConfigSchema.metaCodec(Sources.class, schema -> schema.group(
                ConfigSchema.record("arrows", ChanceSource.class, chanceSource(0.333), Sources::arrows),
                ConfigSchema.record("bite", BiteSource.class, biteCodec(), Sources::bite),
                ConfigSchema.record("cactus", ChanceSource.class, chanceSource(0.5), Sources::cactus),
                ConfigSchema.record("tools", ToolsSource.class, toolsCodec(), Sources::tools)
        ).apply(schema, Sources::new));
    }

    private static ConfigMetaCodec<BiteSource> biteCodec() {
        return ConfigSchema.metaCodec(BiteSource.class, schema -> schema.group(
                ConfigField.bool("is_enabled").tooltip().defaultValue(true).forGetter(BiteSource::isEnabled),
                ConfigField.doubleRange("chance", 0.0, 1.0).tooltip().defaultValue(0.5).forGetter(BiteSource::chance),
                ConfigField.list("blacklisted_animals", BuiltInRegistries.ENTITY_TYPE.byNameCodec()).tooltip().defaultValue(List.of(EntityType.LLAMA)).forGetter(BiteSource::blacklistedAnimals)
        ).apply(schema, BiteSource::new));
    }

    private static ConfigMetaCodec<ToolsSource> toolsCodec() {
        return ConfigSchema.metaCodec(ToolsSource.class, schema -> schema.group(
                ConfigField.bool("is_enabled").tooltip().defaultValue(true).forGetter(ToolsSource::isEnabled),
                ConfigField.list("list", BleedingSources.Tools.ToolDef.CODEC).tooltip().defaultValue(DEFAULT_TOOLS).forGetter(ToolsSource::tools)
        ).apply(schema, ToolsSource::new));
    }

    private static ConfigMetaCodec<ChanceSource> chanceSource(double chance) {
        return ConfigSchema.metaCodec(ChanceSource.class, schema -> schema.group(
                ConfigField.bool("is_enabled").tooltip().defaultValue(true).forGetter(ChanceSource::isEnabled),
                ConfigField.doubleRange("chance", 0.0, 1.0).tooltip().defaultValue(chance).forGetter(ChanceSource::chance)
        ).apply(schema, ChanceSource::new));
    }

    public static BleedingConfig get() {
        return UNIT.get();
    }

    public record ArmorProtection(double base, double perArmor, double perToughness) {
    }

    public record ChanceSource(boolean isEnabled, double chance) {
    }

    public record BiteSource(boolean isEnabled, double chance, List<EntityType<?>> blacklistedAnimals) {
    }

    public record ToolsSource(boolean isEnabled, List<BleedingSources.Tools.ToolDef> tools) {
    }

    public record Sources(ChanceSource arrows, BiteSource bite, ChanceSource cactus, ToolsSource tools) {
    }
}
