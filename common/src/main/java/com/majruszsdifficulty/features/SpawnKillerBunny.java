package com.majruszsdifficulty.features;

import cc.sighs.oelib.event.Subscribe;
import com.majruszsdifficulty.config.FeatureConfig;
import com.majruszsdifficulty.events.ServerEntityJoinEvent;
import com.majruszsdifficulty.gamestage.GameStageHelper;
import net.minecraft.world.entity.animal.Rabbit;

public class SpawnKillerBunny {
    private static FeatureConfig.EnabledStageChance settings() {
        return FeatureConfig.get().spawnKillerBunny();
    }

    @Subscribe
    private static void transformToKiller(ServerEntityJoinEvent data) {
        if (!(data.entity instanceof Rabbit rabbit) || rabbit.isBaby() || data.isLoadedFromDisk || !settings().isEnabled()
                || !data.isAtLeast(GameStageHelper.find(settings().requiredGameStage()))
                || !data.passesChance((float) settings().chance(), settings().isScaledByCrd())) {
            return;
        }
        rabbit.setVariant(Rabbit.Variant.EVIL);
    }
}
