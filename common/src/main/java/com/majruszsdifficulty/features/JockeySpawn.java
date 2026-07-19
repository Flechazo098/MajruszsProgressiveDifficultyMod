package com.majruszsdifficulty.features;

import cc.sighs.oelib.event.Subscribe;
import com.majruszsdifficulty.config.FeatureConfig;
import com.majruszsdifficulty.events.ServerEntityJoinEvent;
import com.majruszsdifficulty.gamestage.GameStageHelper;
import com.majruszsdifficulty.internal.entity.EntityHelper;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Skeleton;

public class JockeySpawn {
    private static FeatureConfig.EnabledStageChance settings() {
        return FeatureConfig.get().jockeySpawn();
    }

    @Subscribe
    private static void spawnSkeleton(ServerEntityJoinEvent data) {
        if (data.entity.getType() != EntityType.SPIDER || data.isLoadedFromDisk || !settings().isEnabled()
                || !data.isAtLeast(GameStageHelper.find(settings().requiredGameStage()))
                || !data.passesChance((float) settings().chance(), settings().isScaledByCrd())) {
            return;
        }
        Skeleton skeleton = EntityHelper.createSpawner(() -> EntityType.SKELETON, data.getServerLevel()).position(data.entity.position()).spawn();
        if (skeleton != null) {
            skeleton.startRiding(data.entity);
        }
    }
}
