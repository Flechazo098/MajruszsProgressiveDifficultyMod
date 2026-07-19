package com.majruszsdifficulty.undeadarmy.listeners;

import cc.sighs.oelib.event.Subscribe;
import com.majruszsdifficulty.internal.entity.EntityHelper;
import com.majruszsdifficulty.undeadarmy.UndeadArmy;
import com.majruszsdifficulty.undeadarmy.UndeadArmyConfig;
import com.majruszsdifficulty.undeadarmy.events.OnUndeadArmyWaveFinished;

public class RewardsController {
    @Subscribe
    private static void update(OnUndeadArmyWaveFinished data) {
        RewardsController.giveExperienceReward(data.undeadArmy);
        if (data.undeadArmy.isLastWave()) {
            // TODO: treasure bag
            if (UndeadArmyConfig.get().resetAllParticipantsKills()) {
                RewardsController.resetAllKillRequirements(data.undeadArmy);
            }
        }
    }

    private static void giveExperienceReward(UndeadArmy undeadArmy) {
        UndeadArmyConfig.WaveDef waveDef = UndeadArmyConfig.get().waves().get(undeadArmy.currentWave);
        undeadArmy.participants.forEach(participant -> {
            for (int i = 0; i < waveDef.experience / 4; ++i) {
                EntityHelper.spawnExperience(undeadArmy.getLevel(), participant.position(), 4);
            }
        });
    }

    private static void resetAllKillRequirements(UndeadArmy undeadArmy) {
        undeadArmy.participants.forEach(participant -> {
            // TODO: update
            // undeadArmy.config.modifyUndeadArmyInfo( participant.getPersistentData(), info->info.killedUndead = 0 );
        });
    }
}
