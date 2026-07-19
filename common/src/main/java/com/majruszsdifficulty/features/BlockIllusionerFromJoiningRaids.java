package com.majruszsdifficulty.features;

import cc.sighs.oelib.event.Subscribe;
import com.majruszsdifficulty.config.FeatureConfig;
import com.majruszsdifficulty.events.ServerEntityJoinEvent;
import com.majruszsdifficulty.gamestage.GameStageHelper;
import net.minecraft.world.entity.monster.Illusioner;

public class BlockIllusionerFromJoiningRaids {
    private static FeatureConfig.EnabledStage settings() {
        return FeatureConfig.get().blockIllusionerFromJoiningRaids();
    }

    @Subscribe
    private static void blockJoiningRaids(ServerEntityJoinEvent data) {
        if (data.entity instanceof Illusioner illusioner && settings().isEnabled()
                && data.isAtLeast(GameStageHelper.find(settings().requiredGameStage()))) {
            illusioner.setCanJoinRaid(false);
        }
    }
}
