package com.majruszsdifficulty.features;

import com.majruszsdifficulty.config.GameplayConfig;
import com.majruszsdifficulty.gamestage.GameStageHelper;
import com.majruszsdifficulty.gamestage.GameStageValue;
import net.minecraft.world.entity.MobCategory;

public class SpawnRateIncreaser {
    public static float modify(MobCategory category, float value) {
        GameplayConfig.SpawnRateIncreaser settings = GameplayConfig.get().spawnRateIncreaser();
        return settings.isEnabled() && category == MobCategory.MONSTER
                ? value * GameStageValue.of(settings.multiplier()).get(GameStageHelper.getGlobalGameStage())
                : value;
    }

}
