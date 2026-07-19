package com.majruszsdifficulty.features;

import cc.sighs.oelib.event.Subscribe;
import com.majruszsdifficulty.config.GameplayConfig;
import com.majruszsdifficulty.events.ServerPlayerExperienceChangeEvent;
import com.majruszsdifficulty.gamestage.GameStageHelper;
import com.majruszsdifficulty.gamestage.GameStageValue;
import com.majruszsdifficulty.internal.math.Random;

public class ExperienceBonus {
    @Subscribe
    private static void increase(ServerPlayerExperienceChangeEvent data) {
        GameplayConfig.ExperienceBonus settings = GameplayConfig.get().experienceBonus();
        if (settings.isEnabled() && data.original > 0) {
            data.amount += Random.round(data.original * GameStageValue.of(settings.extraMultiplier()).get(GameStageHelper.determineGameStage(data.player)));
        }
    }
}
