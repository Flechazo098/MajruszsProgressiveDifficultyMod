package com.majruszsdifficulty.config;

import cc.sighs.oelib.config.ConfigSchema;
import cc.sighs.oelib.config.ConfigUnit;
import cc.sighs.oelib.config.codecs.ConfigMetaCodec;
import cc.sighs.oelib.config.field.ConfigField;
import cc.sighs.oelib.config.model.ConfigStorageFormat;
import com.majruszsdifficulty.MajruszsDifficulty;
import com.majruszsdifficulty.entity.CursedArmor;
import com.majruszsdifficulty.features.MobGroups;
import com.majruszsdifficulty.features.MobsApplyEffects;
import com.majruszsdifficulty.gamestage.GameStage;
import com.majruszsdifficulty.internal.entity.EffectDef;
import com.mojang.serialization.Codec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Map;

public record MobFeatureConfig(
        CreeperSpawnDebuffed creeperSpawnDebuffed,
        List<MobsApplyEffects.ExtraEffectDef> mobsApplyEffects,
        CursedArmorSettings cursedArmor,
        Map<String, MobGroups.GroupDef> mobGroups
) {
    private static final List<EffectDef> CREEPER_EFFECTS = List.of(
            new EffectDef(MobEffects.WEAKNESS::value, 0, 60.0f),
            new EffectDef(MobEffects.MOVEMENT_SLOWDOWN::value, 0, 60.0f),
            new EffectDef(MobEffects.DIG_SLOWDOWN::value, 0, 60.0f),
            new EffectDef(MobEffects.SATURATION::value, 0, 60.0f)
    );
    private static final List<MobsApplyEffects.ExtraEffectDef> EXTRA_EFFECTS = List.of(
            new MobsApplyEffects.ExtraEffectDef(GameStage.EXPERT_ID, 0.25f, true, EntityType.SPIDER, new EffectDef(MobEffects.POISON::value, 0, 7.0f)),
            new MobsApplyEffects.ExtraEffectDef(GameStage.EXPERT_ID, 0.5f, true, EntityType.SLIME, new EffectDef(MobEffects.MOVEMENT_SLOWDOWN::value, 0, 7.0f)),
            new MobsApplyEffects.ExtraEffectDef(GameStage.MASTER_ID, 0.5f, true, EntityType.SHULKER, new EffectDef(MobEffects.BLINDNESS::value, 0, 7.0f)),
            new MobsApplyEffects.ExtraEffectDef(GameStage.MASTER_ID, 0.75f, true, EntityType.PHANTOM, new EffectDef(MobEffects.LEVITATION::value, 0, 7.0f))
    );

    private static ConfigMetaCodec<CreeperSpawnDebuffed> creeperDebuffedCodec() {
        return ConfigSchema.metaCodec(CreeperSpawnDebuffed.class, schema -> schema.group(
                ConfigField.bool("is_enabled").tooltip().defaultValue(true).forGetter(CreeperSpawnDebuffed::isEnabled),
                ConfigField.string("required_game_stage").tooltip().defaultValue(GameStage.NORMAL_ID).forGetter(CreeperSpawnDebuffed::requiredGameStage),
                ConfigField.doubleRange("chance", 0.0, 1.0).tooltip().defaultValue(0.375).forGetter(CreeperSpawnDebuffed::chance),
                ConfigField.bool("is_scaled_by_crd").tooltip().defaultValue(true).forGetter(CreeperSpawnDebuffed::isScaledByCrd),
                ConfigField.list("effects", EffectDef.CODEC).tooltip().defaultValue(CREEPER_EFFECTS).forGetter(CreeperSpawnDebuffed::effects)
        ).apply(schema, CreeperSpawnDebuffed::new));
    }

    private static ConfigMetaCodec<CursedArmorSettings> cursedArmorCodec() {
        return ConfigSchema.metaCodec(CursedArmorSettings.class, schema -> schema.group(
                ConfigField.doubleRange("item_drop_chance", 0.0, 1.0).tooltip().defaultValue(0.2).forGetter(CursedArmorSettings::itemDropChance),
                ConfigField.doubleRange("custom_name_chance", 0.0, 1.0).tooltip().defaultValue(0.025).forGetter(CursedArmorSettings::customNameChance),
                ConfigField.list("custom_names", Codec.STRING).tooltip().defaultValue(List.of("Freshah")).forGetter(CursedArmorSettings::customNames),
                ConfigField.list("locations", CursedArmor.LocationDef.CODEC).tooltip().defaultValue(CursedArmor.defaultLocations()).forGetter(CursedArmorSettings::locations)
        ).apply(schema, CursedArmorSettings::new));
    }

    public static final ConfigUnit<MobFeatureConfig> UNIT = ConfigSchema.defineServer(
            MethodHandles.lookup(),
            ResourceLocation.fromNamespaceAndPath(MajruszsDifficulty.MOD_ID, "mob_features"),
            MobFeatureConfig.class,
            meta -> meta.directory(MajruszsDifficulty.MOD_ID).fileName("mob_features").format(ConfigStorageFormat.JSON),
            schema -> schema.group(
                    ConfigSchema.record("creeper_spawn_debuffed", CreeperSpawnDebuffed.class, creeperDebuffedCodec(), MobFeatureConfig::creeperSpawnDebuffed),
                    ConfigField.list("mobs_apply_effects", MobsApplyEffects.ExtraEffectDef.CODEC).tooltip().defaultValue(EXTRA_EFFECTS).forGetter(MobFeatureConfig::mobsApplyEffects),
                    ConfigSchema.record("cursed_armor", CursedArmorSettings.class, cursedArmorCodec(), MobFeatureConfig::cursedArmor),
                    ConfigField.map("mob_groups", Codec.STRING, MobGroups.GroupDef.CODEC).tooltip().defaultValue(MobGroups.defaultGroups()).forGetter(MobFeatureConfig::mobGroups)
            ).apply(schema, MobFeatureConfig::new)
    );

    public static MobFeatureConfig get() {
        return UNIT.get();
    }

    public record CreeperSpawnDebuffed(boolean isEnabled, String requiredGameStage, double chance,
                                       boolean isScaledByCrd, List<EffectDef> effects) {
    }

    public record CursedArmorSettings(double itemDropChance, double customNameChance, List<String> customNames,
                                      List<CursedArmor.LocationDef> locations) {
    }
}
