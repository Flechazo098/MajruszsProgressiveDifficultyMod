package com.majruszsdifficulty.gamestage.listeners;

import cc.sighs.oelib.event.Subscribe;
import com.majruszsdifficulty.events.ServerLivingEntityDeathEvent;
import com.majruszsdifficulty.events.ServerPlayerChangedDimensionEvent;
import com.majruszsdifficulty.gamestage.GameStage;
import com.majruszsdifficulty.gamestage.GameStageHelper;
import com.majruszsdifficulty.internal.math.AnyPos;
import net.minecraft.core.registries.BuiltInRegistries;

public class Updater {
    @Subscribe
    private static void tryToChangeGameStage(ServerPlayerChangedDimensionEvent data) {
        for (GameStage gameStage : GameStageHelper.getGameStages()) {
            if (gameStage.checkDimension(data.current().dimension().location().toString())) {
                GameStageHelper.increaseGlobalGameStage(gameStage);
                if (GameStageHelper.isPerPlayerDifficultyEnabled()) {
                    GameStageHelper.increaseGameStage(gameStage, data.player());
                }
            }
        }
    }

    @Subscribe
    private static void tryToChangeGameStage(ServerLivingEntityDeathEvent data) {
        for (GameStage gameStage : GameStageHelper.getGameStages()) {
            if (gameStage.checkEntity(BuiltInRegistries.ENTITY_TYPE.getKey(data.target.getType()).toString())) {
                GameStageHelper.increaseGlobalGameStage(gameStage);
                if (GameStageHelper.isPerPlayerDifficultyEnabled()) {
                    data.getLevel()
                            .players()
                            .stream()
                            .filter(player -> AnyPos.from(player.position()).dist(data.target.position()).floatValue() < 128.0f)
                            .forEach(player -> GameStageHelper.increaseGameStage(gameStage, player));
                }
            }
        }
    }
}
