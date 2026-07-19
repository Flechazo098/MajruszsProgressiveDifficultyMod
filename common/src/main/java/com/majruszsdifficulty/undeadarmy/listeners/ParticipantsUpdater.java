package com.majruszsdifficulty.undeadarmy.listeners;

import cc.sighs.oelib.event.EventPriority;
import cc.sighs.oelib.event.Subscribe;
import com.majruszsdifficulty.undeadarmy.events.OnUndeadArmyTicked;

public class ParticipantsUpdater {
    @Subscribe(priority = EventPriority.HIGH)
    private static void update(OnUndeadArmyTicked data) {
        data.undeadArmy.participants.clear();
        data.undeadArmy.participants.addAll(data.getServerLevel().getPlayers(player -> {
            return player.isAlive() && data.undeadArmy.isInRange(player.blockPosition());
        }));
    }
}
