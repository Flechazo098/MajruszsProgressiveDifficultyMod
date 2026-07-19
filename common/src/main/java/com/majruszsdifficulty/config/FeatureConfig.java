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

/**
 * Server-synchronized feature configuration backed directly by OELib's typed schema API.
 */
public record FeatureConfig(
        EnabledStage blockIllusionerFromJoiningRaids,
        EnabledStage creeperChainReaction,
        EnabledStageChance creeperExplodeBehindWall,
        EnabledStageChance creeperSpawnCharged,
        EnabledStageChance drownedLightningBolt,
        EnabledStageChance endermanTeleportAttack,
        EnabledStageChance evokerWithTotem,
        EnabledStageChance jockeySpawn,
        EnabledStageChance spawnKillerBunny
) {
    public static final ConfigUnit<FeatureConfig> UNIT = ConfigSchema.defineServer(
            MethodHandles.lookup(),
            ResourceLocation.fromNamespaceAndPath(MajruszsDifficulty.MOD_ID, "features"),
            FeatureConfig.class,
            meta -> meta
                    .directory(MajruszsDifficulty.MOD_ID)
                    .fileName("features")
                    .format(ConfigStorageFormat.JSON),
            schema -> schema.group(
                    ConfigSchema.record("block_illusioner_from_joining_raids", EnabledStage.class, enabledStage(GameStage.NORMAL_ID), FeatureConfig::blockIllusionerFromJoiningRaids),
                    ConfigSchema.record("creeper_chain_reaction", EnabledStage.class, enabledStage(GameStage.EXPERT_ID), FeatureConfig::creeperChainReaction),
                    ConfigSchema.record("creeper_explode_behind_wall", EnabledStageChance.class, enabledStageChance(GameStage.EXPERT_ID, 1.0, false), FeatureConfig::creeperExplodeBehindWall),
                    ConfigSchema.record("creeper_spawn_charged", EnabledStageChance.class, enabledStageChance(GameStage.NORMAL_ID, 0.125, true), FeatureConfig::creeperSpawnCharged),
                    ConfigSchema.record("drowned_lightning_bolt", EnabledStageChance.class, enabledStageChance(GameStage.EXPERT_ID, 1.0, false), FeatureConfig::drownedLightningBolt),
                    ConfigSchema.record("enderman_teleport_attack", EnabledStageChance.class, enabledStageChance(GameStage.MASTER_ID, 0.5, true), FeatureConfig::endermanTeleportAttack),
                    ConfigSchema.record("evoker_with_totem", EnabledStageChance.class, enabledStageChance(GameStage.NORMAL_ID, 1.0, true), FeatureConfig::evokerWithTotem),
                    ConfigSchema.record("jockey_spawn", EnabledStageChance.class, enabledStageChance(GameStage.EXPERT_ID, 0.125, false), FeatureConfig::jockeySpawn),
                    ConfigSchema.record("spawn_killer_bunny", EnabledStageChance.class, enabledStageChance(GameStage.EXPERT_ID, 0.1, true), FeatureConfig::spawnKillerBunny)
            ).apply(schema, FeatureConfig::new)
    );

    public static FeatureConfig get() {
        return UNIT.get();
    }

    private static ConfigMetaCodec<EnabledStage> enabledStage(String requiredGameStage) {
        return ConfigSchema.metaCodec(EnabledStage.class, schema -> schema.group(
                ConfigField.bool("is_enabled").tooltip().defaultValue(true).forGetter(EnabledStage::isEnabled),
                ConfigField.string("required_game_stage").tooltip().defaultValue(requiredGameStage).forGetter(EnabledStage::requiredGameStage)
        ).apply(schema, EnabledStage::new));
    }

    private static ConfigMetaCodec<EnabledStageChance> enabledStageChance(String requiredGameStage, double chance, boolean scaledByCrd) {
        return ConfigSchema.metaCodec(EnabledStageChance.class, schema -> schema.group(
                ConfigField.bool("is_enabled").tooltip().defaultValue(true).forGetter(EnabledStageChance::isEnabled),
                ConfigField.string("required_game_stage").tooltip().defaultValue(requiredGameStage).forGetter(EnabledStageChance::requiredGameStage),
                ConfigField.doubleRange("chance", 0.0, 1.0).tooltip().defaultValue(chance).forGetter(EnabledStageChance::chance),
                ConfigField.bool("is_scaled_by_crd").tooltip().defaultValue(scaledByCrd).forGetter(EnabledStageChance::isScaledByCrd)
        ).apply(schema, EnabledStageChance::new));
    }

    public record EnabledStage(boolean isEnabled, String requiredGameStage) {
    }

    public record EnabledStageChance(boolean isEnabled, String requiredGameStage, double chance,
                                     boolean isScaledByCrd) {
    }
}
