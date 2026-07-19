package com.majruszsdifficulty.features;

import cc.sighs.oelib.event.Subscribe;
import com.majruszsdifficulty.config.AdvancedFeatureConfig;
import com.majruszsdifficulty.events.ServerExplosionStartEvent;
import com.majruszsdifficulty.gamestage.GameStageHelper;
import com.majruszsdifficulty.internal.level.LevelHelper;
import com.majruszsdifficulty.internal.math.AnyPos;
import com.majruszsdifficulty.internal.math.Random;

public class DeadlierExplosions {
    private static AdvancedFeatureConfig.DeadlierExplosions settings() {
        return AdvancedFeatureConfig.get().deadlierExplosions();
    }

    @Subscribe
    private static void makeDeadlier(ServerExplosionStartEvent data) {
        AdvancedFeatureConfig.DeadlierExplosions settings = settings();
        if (!settings.isEnabled() || GameStageHelper.determineGameStage(data.level, data.position).getOrdinal()
                < GameStageHelper.find(settings.requiredGameStage()).getOrdinal()) {
            return;
        }
        float crd = settings.isScaledByCrd() ? (float) LevelHelper.getClampedRegionalDifficultyAt(data.getLevel(), AnyPos.from(data.position).block()) : 1.0f;
        data.setRadius(data.originalRadius + data.originalRadius * crd * ((float) settings.radiusMultiplier() - 1.0f));
        if (Random.check((float) settings.fireChance() * crd)) {
            data.setFire(true);
        }
    }
}
