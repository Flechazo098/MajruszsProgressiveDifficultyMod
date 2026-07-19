package com.majruszsdifficulty.undeadarmy.listeners;

import cc.sighs.oelib.event.Subscribe;
import com.majruszsdifficulty.undeadarmy.UndeadArmy;
import com.majruszsdifficulty.undeadarmy.events.OnUndeadArmyTicked;

public class BossUpdater {
    @Subscribe
    private static void update(OnUndeadArmyTicked data) {
        for (UndeadArmy.MobInfo mobInfo : data.undeadArmy.mobsLeft) {
            if (mobInfo.isBoss && mobInfo.uuid != null) {
                data.undeadArmy.boss = mobInfo.toEntity(data.getServerLevel());
                return;
            }
        }

        data.undeadArmy.boss = null;
    }
}
