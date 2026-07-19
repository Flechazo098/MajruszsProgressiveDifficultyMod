package com.majruszsdifficulty.features;

import cc.sighs.oelib.event.Subscribe;
import com.majruszsdifficulty.config.FeatureConfig;
import com.majruszsdifficulty.events.ServerLivingEntityDamagedEvent;
import com.majruszsdifficulty.gamestage.GameStageHelper;
import com.majruszsdifficulty.internal.entity.EntityHelper;
import com.majruszsdifficulty.internal.level.LevelHelper;
import com.majruszsdifficulty.internal.math.Random;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Drowned;
import net.minecraft.world.entity.projectile.ThrownTrident;

public class DrownedLightningBolt {
    private static FeatureConfig.EnabledStageChance settings() {
        return FeatureConfig.get().drownedLightningBolt();
    }

    @Subscribe
    private static void spawn(ServerLivingEntityDamagedEvent data) {
        FeatureConfig.EnabledStageChance settings = settings();
        float chance = (float) settings.chance();
        if (settings.isScaledByCrd()) {
            chance *= LevelHelper.getClampedRegionalDifficultyAt(data.getLevel(), data.target.blockPosition());
        }
        if (!settings.isEnabled()
                || !(data.attacker instanceof Drowned)
                || !(data.source.getDirectEntity() instanceof ThrownTrident)
                || !LevelHelper.isRainingAt(data.getLevel(), data.target.blockPosition().offset(0, 2, 0))
                || GameStageHelper.determineGameStage(data.getLevel(), data.target.position()).getOrdinal()
                < GameStageHelper.find(settings.requiredGameStage()).getOrdinal()
                || !Random.check(chance)) {
            return;
        }
        EntityHelper.createSpawner(() -> EntityType.LIGHTNING_BOLT, data.getLevel())
                .position(data.target.position())
                .spawn();
    }
}
