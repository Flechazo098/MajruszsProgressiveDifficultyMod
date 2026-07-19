package com.majruszsdifficulty.undeadarmy.listeners;

import cc.sighs.oelib.event.Subscribe;
import com.majruszsdifficulty.MajruszsDifficulty;
import com.majruszsdifficulty.undeadarmy.events.OnUndeadArmyDefeated;

public class AdvancementsController {
    @Subscribe
    private static void trigger(OnUndeadArmyDefeated data) {
        data.undeadArmy.participants.forEach(participant -> MajruszsDifficulty.triggerAdvancement(participant, "army_defeated"));
    }
}
