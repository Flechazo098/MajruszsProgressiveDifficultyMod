package com.majruszsdifficulty.features;

import cc.sighs.oelib.event.Subscribe;
import com.majruszsdifficulty.config.FeatureConfig;
import com.majruszsdifficulty.events.ServerLivingEntityDamagedEvent;
import com.majruszsdifficulty.gamestage.GameStageHelper;
import net.minecraft.world.entity.monster.Creeper;

public class CreeperChainReaction {
    private static FeatureConfig.EnabledStage settings() {
        return FeatureConfig.get().creeperChainReaction();
    }

    @Subscribe
    private static void igniteCreeper(ServerLivingEntityDamagedEvent data) {
        FeatureConfig.EnabledStage settings = settings();
        if (settings.isEnabled()
                && data.target instanceof Creeper creeper
                && data.attacker instanceof Creeper
                && GameStageHelper.determineGameStage(data.getLevel(), data.target.position()).getOrdinal()
                >= GameStageHelper.find(settings.requiredGameStage()).getOrdinal()) {
            creeper.ignite();
        }
    }
}
