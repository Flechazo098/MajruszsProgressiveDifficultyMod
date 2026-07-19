package com.majruszsdifficulty.config;

import cc.sighs.oelib.config.ConfigSchema;
import cc.sighs.oelib.config.ConfigUnit;
import cc.sighs.oelib.config.codecs.ConfigMetaCodec;
import cc.sighs.oelib.config.field.ConfigField;
import cc.sighs.oelib.config.model.ConfigStorageFormat;
import com.majruszsdifficulty.MajruszsDifficulty;
import com.majruszsdifficulty.gamestage.GameStage;
import net.minecraft.resources.ResourceLocation;

import java.lang.invoke.MethodHandles;

public record AdvancedFeatureConfig(
        CreeperExplosionImmunity creeperExplosionImmunity,
        DeadlierExplosions deadlierExplosions,
        PillagerWithFireworks pillagerWithFireworks,
        SpawnPlayerZombie spawnPlayerZombie
) {
    private static ConfigMetaCodec<CreeperExplosionImmunity> creeperExplosionImmunityCodec() {
        return ConfigSchema.metaCodec(
                CreeperExplosionImmunity.class,
                schema -> schema.group(
                        ConfigField.bool("is_enabled").tooltip().defaultValue(true).forGetter(CreeperExplosionImmunity::isEnabled),
                        ConfigField.string("required_game_stage").tooltip().defaultValue(GameStage.EXPERT_ID).forGetter(CreeperExplosionImmunity::requiredGameStage),
                        ConfigField.doubleRange("damage_multiplier", 0.0, 1.0).tooltip().defaultValue(0.2).forGetter(CreeperExplosionImmunity::damageMultiplier)
                ).apply(schema, CreeperExplosionImmunity::new)
        );
    }

    private static ConfigMetaCodec<DeadlierExplosions> deadlierExplosionsCodec() {
        return ConfigSchema.metaCodec(
                DeadlierExplosions.class,
                schema -> schema.group(
                        ConfigField.bool("is_enabled").tooltip().defaultValue(true).forGetter(DeadlierExplosions::isEnabled),
                        ConfigField.string("required_game_stage").tooltip().defaultValue(GameStage.NORMAL_ID).forGetter(DeadlierExplosions::requiredGameStage),
                        ConfigField.doubleRange("radius_multiplier", 1.0, 10.0).tooltip().defaultValue(1.2599).forGetter(DeadlierExplosions::radiusMultiplier),
                        ConfigField.doubleRange("fire_chance", 0.0, 1.0).tooltip().defaultValue(0.75).forGetter(DeadlierExplosions::fireChance),
                        ConfigField.bool("is_scaled_by_crd").tooltip().defaultValue(true).forGetter(DeadlierExplosions::isScaledByCrd)
                ).apply(schema, DeadlierExplosions::new)
        );
    }

    private static ConfigMetaCodec<PillagerWithFireworks> pillagerWithFireworksCodec() {
        return ConfigSchema.metaCodec(
                PillagerWithFireworks.class,
                schema -> schema.group(
                        ConfigField.bool("is_enabled").tooltip().defaultValue(true).forGetter(PillagerWithFireworks::isEnabled),
                        ConfigField.string("required_game_stage").tooltip().defaultValue(GameStage.MASTER_ID).forGetter(PillagerWithFireworks::requiredGameStage),
                        ConfigField.doubleRange("chance", 0.0, 1.0).tooltip().defaultValue(0.15).forGetter(PillagerWithFireworks::chance),
                        ConfigField.bool("is_scaled_by_crd").tooltip().defaultValue(true).forGetter(PillagerWithFireworks::isScaledByCrd),
                        ConfigField.doubleRange("crossbow_multishot_chance", 0.0, 1.0).tooltip().defaultValue(0.25).forGetter(PillagerWithFireworks::crossbowMultishotChance),
                        ConfigField.doubleRange("firework_drop_chance", 0.0, 1.0).tooltip().defaultValue(0.25).forGetter(PillagerWithFireworks::fireworkDropChance),
                        ConfigField.intRange("firework_count", 1, 64).tooltip().defaultValue(8).forGetter(PillagerWithFireworks::fireworkCount)
                ).apply(schema, PillagerWithFireworks::new)
        );
    }

    private static ConfigMetaCodec<SpawnPlayerZombie> spawnPlayerZombieCodec() {
        return ConfigSchema.metaCodec(
                SpawnPlayerZombie.class,
                schema -> schema.group(
                        ConfigField.bool("is_enabled").tooltip().defaultValue(true).forGetter(SpawnPlayerZombie::isEnabled),
                        ConfigField.string("required_game_stage").tooltip().defaultValue(GameStage.EXPERT_ID).forGetter(SpawnPlayerZombie::requiredGameStage),
                        ConfigField.doubleRange("chance", 0.0, 1.0).tooltip().defaultValue(1.0).forGetter(SpawnPlayerZombie::chance),
                        ConfigField.bool("is_scaled_by_crd").tooltip().defaultValue(false).forGetter(SpawnPlayerZombie::isScaledByCrd),
                        ConfigField.doubleRange("head_chance", 0.0, 1.0).tooltip().defaultValue(1.0).forGetter(SpawnPlayerZombie::headChance),
                        ConfigField.doubleRange("head_drop_chance", 0.0, 1.0).tooltip().defaultValue(0.1).forGetter(SpawnPlayerZombie::headDropChance)
                ).apply(schema, SpawnPlayerZombie::new)
        );
    }

    public static final ConfigUnit<AdvancedFeatureConfig> UNIT = ConfigSchema.defineServer(
            MethodHandles.lookup(),
            ResourceLocation.fromNamespaceAndPath(MajruszsDifficulty.MOD_ID, "advanced_features"),
            AdvancedFeatureConfig.class,
            meta -> meta.directory(MajruszsDifficulty.MOD_ID).fileName("advanced_features").format(ConfigStorageFormat.JSON),
            schema -> schema.group(
                    ConfigSchema.record("creeper_explosion_immunity", CreeperExplosionImmunity.class, creeperExplosionImmunityCodec(), AdvancedFeatureConfig::creeperExplosionImmunity),
                    ConfigSchema.record("deadlier_explosions", DeadlierExplosions.class, deadlierExplosionsCodec(), AdvancedFeatureConfig::deadlierExplosions),
                    ConfigSchema.record("pillager_with_fireworks", PillagerWithFireworks.class, pillagerWithFireworksCodec(), AdvancedFeatureConfig::pillagerWithFireworks),
                    ConfigSchema.record("spawn_player_zombie", SpawnPlayerZombie.class, spawnPlayerZombieCodec(), AdvancedFeatureConfig::spawnPlayerZombie)
            ).apply(schema, AdvancedFeatureConfig::new)
    );

    public static AdvancedFeatureConfig get() {
        return UNIT.get();
    }

    public record CreeperExplosionImmunity(boolean isEnabled, String requiredGameStage, double damageMultiplier) {
    }

    public record DeadlierExplosions(boolean isEnabled, String requiredGameStage, double radiusMultiplier,
                                     double fireChance, boolean isScaledByCrd) {
    }

    public record PillagerWithFireworks(boolean isEnabled, String requiredGameStage, double chance,
                                        boolean isScaledByCrd, double crossbowMultishotChance,
                                        double fireworkDropChance, int fireworkCount) {
    }

    public record SpawnPlayerZombie(boolean isEnabled, String requiredGameStage, double chance, boolean isScaledByCrd,
                                    double headChance, double headDropChance) {
    }
}
