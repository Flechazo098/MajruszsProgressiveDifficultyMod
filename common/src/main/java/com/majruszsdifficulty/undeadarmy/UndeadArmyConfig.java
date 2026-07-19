package com.majruszsdifficulty.undeadarmy;

import cc.sighs.oelib.config.ConfigSchema;
import cc.sighs.oelib.config.ConfigUnit;
import cc.sighs.oelib.config.field.ConfigField;
import cc.sighs.oelib.config.model.ConfigStorageFormat;
import com.majruszsdifficulty.MajruszsDifficulty;
import com.majruszsdifficulty.gamestage.GameStage;
import com.majruszsdifficulty.gamestage.GameStageHelper;
import com.majruszsdifficulty.registry.ModEntities;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public record UndeadArmyConfig(
        boolean isEnabled,
        boolean resetAllParticipantsKills,
        double waveDuration,
        double preparingDuration,
        double highlightDelay,
        double extraSizeRatioPerPlayer,
        int areaRadius,
        int killRequirement,
        int killRequirementFirst,
        int killRequirementWarning,
        List<WaveDef> waves
) {
    private static final List<WaveDef> DEFAULT_WAVES = List.of(
            new WaveDef(
                    List.of(
                            new MobDef(EntityType.ZOMBIE, "majruszsdifficulty:undead_army/wave_1_mob", 2),
                            new MobDef(EntityType.SKELETON, "majruszsdifficulty:undead_army/wave_1_mob", 2),
                            new MobDef(EntityType.ZOMBIE, "majruszsdifficulty:undead_army/wave_1_mob", 2),
                            new MobDef(EntityType.SKELETON, "majruszsdifficulty:undead_army/wave_1_mob", 2)
                    ),
                    null,
                    GameStage.NORMAL_ID,
                    8
            ),
            new WaveDef(
                    List.of(
                            new MobDef(EntityType.ZOMBIE, "majruszsdifficulty:undead_army/wave_2_mob", 2),
                            new MobDef(EntityType.SKELETON, "majruszsdifficulty:undead_army/wave_2_mob", 3),
                            new MobDef(EntityType.ZOMBIE, "majruszsdifficulty:undead_army/wave_2_mob", 3),
                            new MobDef(EntityType.SKELETON, "majruszsdifficulty:undead_army/wave_2_mob", 2)
                    ),
                    null,
                    GameStage.NORMAL_ID,
                    16
            ),
            new WaveDef(
                    List.of(
                            new MobDef(EntityType.ZOMBIE, "majruszsdifficulty:undead_army/wave_3_mob", 3),
                            new MobDef(EntityType.SKELETON, "majruszsdifficulty:undead_army/wave_3_skeleton", 3),
                            new MobDef(EntityType.HUSK, "majruszsdifficulty:undead_army/wave_3_mob", 3),
                            new MobDef(EntityType.STRAY, "majruszsdifficulty:undead_army/wave_3_skeleton", 3)
                    ),
                    new MobDef(ModEntities.TANK_ENTITY.id()),
                    GameStage.NORMAL_ID,
                    24
            ),
            new WaveDef(
                    List.of(
                            new MobDef(EntityType.ZOMBIE, "majruszsdifficulty:undead_army/wave_4_mob", 2),
                            new MobDef(EntityType.WITHER_SKELETON, "majruszsdifficulty:undead_army/wave_4_mob", 1),
                            new MobDef(EntityType.SKELETON, "majruszsdifficulty:undead_army/wave_4_skeleton", 2),
                            new MobDef(EntityType.WITHER_SKELETON, "majruszsdifficulty:undead_army/wave_4_mob", 1),
                            new MobDef(ModEntities.TANK_ENTITY.id()),
                            new MobDef(EntityType.HUSK, "majruszsdifficulty:undead_army/wave_4_mob", 3),
                            new MobDef(EntityType.WITHER_SKELETON, "majruszsdifficulty:undead_army/wave_4_mob", 1),
                            new MobDef(EntityType.STRAY, "majruszsdifficulty:undead_army/wave_4_skeleton", 3),
                            new MobDef(EntityType.WITHER_SKELETON, "majruszsdifficulty:undead_army/wave_4_mob", 1)
                    ),
                    null,
                    GameStage.EXPERT_ID,
                    32
            ),
            new WaveDef(
                    List.of(
                            new MobDef(EntityType.ZOMBIE, "majruszsdifficulty:undead_army/wave_5_mob", 1),
                            new MobDef(EntityType.SKELETON, "majruszsdifficulty:undead_army/wave_5_skeleton", 1),
                            new MobDef(EntityType.STRAY, "majruszsdifficulty:undead_army/wave_5_skeleton", 3),
                            new MobDef(EntityType.WITHER_SKELETON, "majruszsdifficulty:undead_army/wave_5_mob", 2),
                            new MobDef(ModEntities.TANK_ENTITY.id()),
                            new MobDef(EntityType.HUSK, "majruszsdifficulty:undead_army/wave_5_mob", 3),
                            new MobDef(EntityType.WITHER_SKELETON, "majruszsdifficulty:undead_army/wave_5_mob", 2)
                    ),
                    new MobDef(ModEntities.CERBERUS_ENTITY.id()),
                    GameStage.EXPERT_ID,
                    40
            ),
            new WaveDef(
                    List.of(
                            new MobDef(EntityType.ZOMBIE, "majruszsdifficulty:undead_army/wave_6_mob", 1),
                            new MobDef(EntityType.SKELETON, "majruszsdifficulty:undead_army/wave_6_skeleton", 1),
                            new MobDef(ModEntities.TANK_ENTITY.id()),
                            new MobDef(EntityType.STRAY, "majruszsdifficulty:undead_army/wave_6_skeleton", 1),
                            new MobDef(EntityType.HUSK, "majruszsdifficulty:undead_army/wave_6_mob", 1),
                            new MobDef(EntityType.WITHER_SKELETON, "majruszsdifficulty:undead_army/wave_6_wither_skeleton", 1),
                            new MobDef(ModEntities.CERBERUS_ENTITY.id())
                    ),
                    new MobDef(ModEntities.GIANT_ENTITY.id(), "majruszsdifficulty:undead_army/wave_6_mob", 1),
                    GameStage.MASTER_ID,
                    48
            )
    );

    public static final ConfigUnit<UndeadArmyConfig> UNIT = ConfigSchema.defineServer(
            MethodHandles.lookup(),
            MajruszsDifficulty.id("undead_army"),
            UndeadArmyConfig.class,
            meta -> meta.directory(MajruszsDifficulty.MOD_ID).fileName("undead_army").format(ConfigStorageFormat.JSON),
            schema -> schema.group(
                    ConfigField.bool("is_enabled").tooltip().defaultValue(true).forGetter(UndeadArmyConfig::isEnabled),
                    ConfigField.bool("reset_all_participants_kills").tooltip().defaultValue(true).forGetter(UndeadArmyConfig::resetAllParticipantsKills),
                    ConfigField.doubleRange("wave_duration", 300.0, 3600.0).tooltip().defaultValue(1200.0).forGetter(UndeadArmyConfig::waveDuration),
                    ConfigField.doubleRange("preparing_duration", 1.0, 60.0).tooltip().defaultValue(10.0).forGetter(UndeadArmyConfig::preparingDuration),
                    ConfigField.doubleRange("highlight_delay", 30.0, 3600.0).tooltip().defaultValue(300.0).forGetter(UndeadArmyConfig::highlightDelay),
                    ConfigField.doubleRange("extra_size_ratio_per_player", 0.0, 1.0).tooltip().defaultValue(0.5).forGetter(UndeadArmyConfig::extraSizeRatioPerPlayer),
                    ConfigField.intRange("area_radius", 35, 140).tooltip().defaultValue(70).forGetter(UndeadArmyConfig::areaRadius),
                    ConfigField.intRange("kill_requirement", 0, 1000).tooltip().defaultValue(100).forGetter(UndeadArmyConfig::killRequirement),
                    ConfigField.intRange("kill_requirement_first", 1, 1000).tooltip().defaultValue(25).forGetter(UndeadArmyConfig::killRequirementFirst),
                    ConfigField.intRange("kill_requirement_warning", 1, 1000).tooltip().defaultValue(3).forGetter(UndeadArmyConfig::killRequirementWarning),
                    ConfigField.list("waves", WaveDef.CODEC).tooltip().defaultValue(DEFAULT_WAVES).forGetter(UndeadArmyConfig::waves)
            ).apply(schema, UndeadArmyConfig::new)
    );

    public static UndeadArmyConfig get() {
        return UNIT.get();
    }

    public static class WaveDef {
        public static final Codec<WaveDef> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                MobDef.CODEC.listOf().fieldOf("mobs").forGetter(value -> value.mobDefs),
                MobDef.CODEC.optionalFieldOf("boss").forGetter(value -> Optional.ofNullable(value.bossDef)),
                Codec.STRING.fieldOf("game_stage").forGetter(value -> value.gameStage.getId()),
                Codec.intRange(0, 1000).fieldOf("exp").forGetter(value -> value.experience)
        ).apply(instance, (mobs, boss, stage, experience) -> new WaveDef(mobs, boss.orElse(null), stage, experience)));
        public List<MobDef> mobDefs = new ArrayList<>();
        public MobDef bossDef;
        public GameStage gameStage;
        public int experience = 0;

        public WaveDef(List<MobDef> mobDefs, MobDef bossDef, String gameStageId, int experience) {
            this.mobDefs = mobDefs;
            this.bossDef = bossDef;
            this.gameStage = GameStageHelper.find(gameStageId);
            this.experience = experience;
        }

        public WaveDef() {
        }
    }

    public static class MobDef {
        public static final Codec<MobDef> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ResourceLocation.CODEC.fieldOf("type").forGetter(value -> value.typeId),
                ResourceLocation.CODEC.optionalFieldOf("equipment").forGetter(value -> Optional.ofNullable(value.equipment)),
                Codec.intRange(1, 20).fieldOf("count").orElse(1).forGetter(value -> value.count)
        ).apply(instance, (type, equipment, count) -> new MobDef(type, equipment.map(ResourceLocation::toString).orElse(null), count)));
        public ResourceLocation typeId;
        public ResourceLocation equipment;
        public int count = 1;

        public MobDef(ResourceLocation typeId, String equipment, int count) {
            this.typeId = typeId;
            this.equipment = equipment != null ? ResourceLocation.parse(equipment) : null;
            this.count = count;
        }

        public MobDef(EntityType<?> type, String equipment, int count) {
            this(BuiltInRegistries.ENTITY_TYPE.getKey(type), equipment, count);
        }

        public MobDef(ResourceLocation typeId) {
            this(typeId, null, 1);
        }

        public MobDef() {
        }
    }
}
