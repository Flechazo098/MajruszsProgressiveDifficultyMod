package com.majruszsdifficulty.features;

import cc.sighs.oelib.event.Subscribe;
import com.majruszsdifficulty.config.FeatureConfig;
import com.majruszsdifficulty.events.ServerEntityJoinEvent;
import com.majruszsdifficulty.gamestage.GameStageHelper;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.monster.Creeper;

public class CreeperSpawnCharged {
    private static FeatureConfig.EnabledStageChance settings() {
        return FeatureConfig.get().creeperSpawnCharged();
    }

    @Subscribe
    private static void charge(ServerEntityJoinEvent data) {
        if (!(data.entity instanceof Creeper creeper) || data.isLoadedFromDisk || !settings().isEnabled()
                || !data.isAtLeast(GameStageHelper.find(settings().requiredGameStage()))
                || !data.passesChance((float) settings().chance(), settings().isScaledByCrd())) {
            return;
        }
        LightningBolt lightningBolt = EntityType.LIGHTNING_BOLT.create(data.getServerLevel());
        if (lightningBolt != null) {
            creeper.thunderHit(data.getServerLevel(), lightningBolt);
            creeper.clearFire();
        }
    }
}
