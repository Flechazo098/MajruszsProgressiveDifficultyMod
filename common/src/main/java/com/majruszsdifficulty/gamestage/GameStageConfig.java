package com.majruszsdifficulty.gamestage;

import cc.sighs.oelib.config.ConfigSchema;
import cc.sighs.oelib.config.ConfigUnit;
import cc.sighs.oelib.config.field.ConfigField;
import cc.sighs.oelib.config.model.ConfigStorageFormat;
import com.majruszsdifficulty.MajruszsDifficulty;
import net.minecraft.ChatFormatting;
import net.minecraft.resources.ResourceLocation;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

public record GameStageConfig(boolean perPlayerDifficulty, List<GameStage> stages) {
    private static final List<GameStage> DEFAULT_STAGES = List.of(
            GameStage.named(GameStage.NORMAL_ID)
                    .format(ChatFormatting.WHITE)
                    .create(),
            GameStage.named(GameStage.EXPERT_ID)
                    .format(ChatFormatting.RED, ChatFormatting.BOLD)
                    .triggersIn("{regex}.*")
                    .message("majruszsdifficulty.stages.expert.started", ChatFormatting.RED, ChatFormatting.BOLD)
                    .message("majruszsdifficulty.undead_army.on_expert", ChatFormatting.DARK_PURPLE)
                    .create(),
            GameStage.named(GameStage.MASTER_ID)
                    .format(ChatFormatting.DARK_PURPLE, ChatFormatting.BOLD)
                    .triggersByKilling("minecraft:ender_dragon")
                    .message("majruszsdifficulty.stages.master.started", ChatFormatting.DARK_PURPLE, ChatFormatting.BOLD)
                    .message("majruszsdifficulty.undead_army.on_master", ChatFormatting.DARK_PURPLE)
                    .create()
    );

    public static final ConfigUnit<GameStageConfig> UNIT = ConfigSchema.defineServer(
            MethodHandles.lookup(),
            ResourceLocation.fromNamespaceAndPath(MajruszsDifficulty.MOD_ID, "game_stages"),
            GameStageConfig.class,
            meta -> meta.directory(MajruszsDifficulty.MOD_ID).fileName("game_stages").format(ConfigStorageFormat.JSON),
            schema -> schema.group(
                    ConfigField.bool("is_per_player_difficulty_enabled").tooltip().defaultValue(false).forGetter(GameStageConfig::perPlayerDifficulty),
                    ConfigField.list("list", GameStage.CODEC).tooltip().defaultValue(DEFAULT_STAGES).forGetter(GameStageConfig::stages)
            ).apply(schema, GameStageConfig::new)
    );

    private static List<GameStage> currentStages = updateOrdinals(new ArrayList<>(DEFAULT_STAGES));

    public static boolean isPerPlayerDifficultyEnabled() {
        return UNIT.get().perPlayerDifficulty();
    }

    public static List<GameStage> getStages() {
        return validate(new ArrayList<>(UNIT.get().stages()));
    }

    private static List<GameStage> validate(List<GameStage> stages) {
        boolean hasDefaultGameStages = stages.stream()
                .filter(stage -> stage.is(GameStage.NORMAL_ID) || stage.is(GameStage.EXPERT_ID) || stage.is(GameStage.MASTER_ID))
                .count() == 3;
        if (!hasDefaultGameStages) {
            throw new IllegalArgumentException("Default game stages cannot be removed");
        }
        for (GameStage stage : stages) {
            long count = stages.stream().filter(value -> value.equals(stage)).count();
            if (count > 1) {
                throw new IllegalArgumentException("Found %d game stages with identical id (%s)".formatted(count, stage.getId()));
            }
        }
        keepOldReferencesValid(stages, currentStages);
        currentStages = updateOrdinals(stages);
        return currentStages;
    }

    private static void keepOldReferencesValid(List<GameStage> newStages, List<GameStage> oldStages) {
        for (int index = 0; index < newStages.size(); ++index) {
            GameStage replacement = newStages.get(index);
            for (GameStage current : oldStages) {
                if (current.is(replacement.getId())) {
                    replacement = current.copy(replacement);
                    break;
                }
            }
            newStages.set(index, replacement);
        }
    }

    private static List<GameStage> updateOrdinals(List<GameStage> stages) {
        for (int index = 0; index < stages.size(); ++index) {
            stages.get(index).ordinal = index;
        }
        return stages;
    }
}
