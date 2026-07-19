package com.majruszsdifficulty.gamestage.listeners;

import com.majruszsdifficulty.config.GameplayConfig;
import com.majruszsdifficulty.gamestage.GameStageHelper;
import com.majruszsdifficulty.gamestage.GameStageValue;

public class ClampedRegionalDifficultyIncreaser {
    public static float modify(float crd) {
        return crd + GameStageValue.of(GameplayConfig.get().gameStageDifficulty().crdPenalty()).get(GameStageHelper.getGlobalGameStage());
    }
}
