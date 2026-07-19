package com.majruszsdifficulty.config;

import cc.sighs.oelib.config.ConfigSchema;
import cc.sighs.oelib.config.ConfigUnit;
import cc.sighs.oelib.config.codecs.ConfigMetaCodec;
import cc.sighs.oelib.config.field.ConfigField;
import cc.sighs.oelib.config.model.ConfigStorageFormat;
import com.majruszsdifficulty.MajruszsDifficulty;
import com.majruszsdifficulty.gamestage.GameStage;
import com.mojang.serialization.Codec;
import net.minecraft.resources.ResourceLocation;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Map;

public record GameplayConfig(
        CreeperSplit creeperSplitIntoCreeperlings,
        DoubleLoot doubleLoot,
        ExperienceBonus experienceBonus,
        MobsSpawnStronger mobsSpawnStronger,
        SpawnBlocker spawnBlocker,
        SpawnRateIncreaser spawnRateIncreaser,
        GameStageDifficulty gameStageDifficulty
) {
    private static final Map<String, Integer> CREEPER_COUNTS = Map.of(
            "__default", 2, GameStage.EXPERT_ID, 4, GameStage.MASTER_ID, 6
    );
    private static final Map<String, Float> DOUBLE_LOOT_CHANCES = Map.of(
            "__default", 0.0f, GameStage.EXPERT_ID, 0.05f, GameStage.MASTER_ID, 0.1f
    );
    private static final Map<String, Float> EXPERIENCE_BONUSES = Map.of(
            "__default", 0.0f, GameStage.EXPERT_ID, 0.2f, GameStage.MASTER_ID, 0.4f
    );
    private static final Map<String, Float> HEALTH_BONUSES = Map.of(
            "__default", 0.0f, GameStage.EXPERT_ID, 0.15f, GameStage.MASTER_ID, 0.3f
    );
    private static final Map<String, Float> DAMAGE_BONUSES = Map.of(
            "__default", 0.0f, GameStage.EXPERT_ID, 0.1f, GameStage.MASTER_ID, 0.2f
    );
    private static final Map<String, List<String>> FORBIDDEN_ENTITIES = Map.of(
            "__default", List.of("majruszsdifficulty:illusioner", "majruszsdifficulty:tank", "majruszsdifficulty:cerberus"),
            GameStage.EXPERT_ID, List.of("majruszsdifficulty:cerberus"),
            GameStage.MASTER_ID, List.of()
    );
    private static final Map<String, Float> SPAWN_RATE_MULTIPLIERS = Map.of(
            "__default", 1.0f, GameStage.EXPERT_ID, 1.1f, GameStage.MASTER_ID, 1.2f
    );
    private static final Map<String, Float> CRD_PENALTIES = Map.of(
            "__default", 0.0f, GameStage.EXPERT_ID, 0.15f, GameStage.MASTER_ID, 0.3f
    );

    private static ConfigMetaCodec<CreeperSplit> creeperSplitCodec() {
        return ConfigSchema.metaCodec(CreeperSplit.class, schema -> schema.group(
                ConfigField.bool("is_enabled").tooltip().defaultValue(true).forGetter(CreeperSplit::isEnabled),
                ConfigField.map("count", Codec.STRING, Codec.intRange(1, 20)).tooltip().defaultValue(CREEPER_COUNTS).forGetter(CreeperSplit::count),
                ConfigField.doubleRange("chance", 0.0, 1.0).tooltip().defaultValue(0.666).forGetter(CreeperSplit::chance),
                ConfigField.bool("is_scaled_by_crd").tooltip().defaultValue(true).forGetter(CreeperSplit::isScaledByCrd)
        ).apply(schema, CreeperSplit::new));
    }

    private static ConfigMetaCodec<DoubleLoot> doubleLootCodec() {
        return ConfigSchema.metaCodec(DoubleLoot.class, schema -> schema.group(
                ConfigField.bool("is_enabled").tooltip().defaultValue(true).forGetter(DoubleLoot::isEnabled),
                ConfigField.map("chance", Codec.STRING, Codec.FLOAT).tooltip().defaultValue(DOUBLE_LOOT_CHANCES).forGetter(DoubleLoot::chance),
                ConfigField.list("blacklisted_items", Codec.STRING).tooltip().defaultValue(List.of("minecraft:nether_star", "minecraft:totem_of_undying")).forGetter(DoubleLoot::blacklistedItems)
        ).apply(schema, DoubleLoot::new));
    }

    private static ConfigMetaCodec<ExperienceBonus> experienceBonusCodec() {
        return ConfigSchema.metaCodec(ExperienceBonus.class, schema -> schema.group(
                ConfigField.bool("is_enabled").tooltip().defaultValue(true).forGetter(ExperienceBonus::isEnabled),
                ConfigField.map("extra_multiplier", Codec.STRING, Codec.floatRange(0.0f, 10.0f)).tooltip().defaultValue(EXPERIENCE_BONUSES).forGetter(ExperienceBonus::extraMultiplier)
        ).apply(schema, ExperienceBonus::new));
    }

    private static ConfigMetaCodec<MobsSpawnStronger> mobsSpawnStrongerCodec() {
        return ConfigSchema.metaCodec(MobsSpawnStronger.class, schema -> schema.group(
                ConfigField.bool("is_enabled").tooltip().defaultValue(true).forGetter(MobsSpawnStronger::isEnabled),
                ConfigField.map("health_bonus", Codec.STRING, Codec.FLOAT).tooltip().defaultValue(HEALTH_BONUSES).forGetter(MobsSpawnStronger::healthBonus),
                ConfigField.map("damage_bonus", Codec.STRING, Codec.FLOAT).tooltip().defaultValue(DAMAGE_BONUSES).forGetter(MobsSpawnStronger::damageBonus),
                ConfigField.list("excluded_mobs", Codec.STRING).tooltip().defaultValue(List.of()).forGetter(MobsSpawnStronger::excludedMobs)
        ).apply(schema, MobsSpawnStronger::new));
    }

    private static ConfigMetaCodec<SpawnBlocker> spawnBlockerCodec() {
        return ConfigSchema.metaCodec(SpawnBlocker.class, schema -> schema.group(
                ConfigField.map("forbidden_entities", Codec.STRING, Codec.STRING.listOf()).tooltip().defaultValue(FORBIDDEN_ENTITIES).forGetter(SpawnBlocker::forbiddenEntities)
        ).apply(schema, SpawnBlocker::new));
    }

    private static ConfigMetaCodec<SpawnRateIncreaser> spawnRateCodec() {
        return ConfigSchema.metaCodec(SpawnRateIncreaser.class, schema -> schema.group(
                ConfigField.bool("is_enabled").tooltip().defaultValue(true).forGetter(SpawnRateIncreaser::isEnabled),
                ConfigField.map("multiplier", Codec.STRING, Codec.floatRange(1.0f, 20.0f)).tooltip().defaultValue(SPAWN_RATE_MULTIPLIERS).forGetter(SpawnRateIncreaser::multiplier)
        ).apply(schema, SpawnRateIncreaser::new));
    }

    private static ConfigMetaCodec<GameStageDifficulty> gameStageDifficultyCodec() {
        return ConfigSchema.metaCodec(GameStageDifficulty.class, schema -> schema.group(
                ConfigField.map("crd_penalty", Codec.STRING, Codec.FLOAT).tooltip().defaultValue(CRD_PENALTIES).forGetter(GameStageDifficulty::crdPenalty)
        ).apply(schema, GameStageDifficulty::new));
    }

    public static final ConfigUnit<GameplayConfig> UNIT = ConfigSchema.defineServer(
            MethodHandles.lookup(),
            ResourceLocation.fromNamespaceAndPath(MajruszsDifficulty.MOD_ID, "gameplay"),
            GameplayConfig.class,
            meta -> meta.directory(MajruszsDifficulty.MOD_ID).fileName("gameplay").format(ConfigStorageFormat.JSON),
            schema -> schema.group(
                    ConfigSchema.record("creeper_split_into_creeperlings", CreeperSplit.class, creeperSplitCodec(), GameplayConfig::creeperSplitIntoCreeperlings),
                    ConfigSchema.record("double_loot", DoubleLoot.class, doubleLootCodec(), GameplayConfig::doubleLoot),
                    ConfigSchema.record("experience_bonus", ExperienceBonus.class, experienceBonusCodec(), GameplayConfig::experienceBonus),
                    ConfigSchema.record("mobs_spawn_stronger", MobsSpawnStronger.class, mobsSpawnStrongerCodec(), GameplayConfig::mobsSpawnStronger),
                    ConfigSchema.record("spawn_blocker", SpawnBlocker.class, spawnBlockerCodec(), GameplayConfig::spawnBlocker),
                    ConfigSchema.record("spawn_rate_increaser", SpawnRateIncreaser.class, spawnRateCodec(), GameplayConfig::spawnRateIncreaser),
                    ConfigSchema.record("game_stage_difficulty", GameStageDifficulty.class, gameStageDifficultyCodec(), GameplayConfig::gameStageDifficulty)
            ).apply(schema, GameplayConfig::new)
    );

    public static GameplayConfig get() {
        return UNIT.get();
    }

    public record CreeperSplit(boolean isEnabled, Map<String, Integer> count, double chance, boolean isScaledByCrd) {
    }

    public record DoubleLoot(boolean isEnabled, Map<String, Float> chance, List<String> blacklistedItems) {
    }

    public record ExperienceBonus(boolean isEnabled, Map<String, Float> extraMultiplier) {
    }

    public record MobsSpawnStronger(boolean isEnabled, Map<String, Float> healthBonus, Map<String, Float> damageBonus,
                                    List<String> excludedMobs) {
    }

    public record SpawnBlocker(Map<String, List<String>> forbiddenEntities) {
    }

    public record SpawnRateIncreaser(boolean isEnabled, Map<String, Float> multiplier) {
    }

    public record GameStageDifficulty(Map<String, Float> crdPenalty) {
    }
}
