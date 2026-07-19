package com.majruszsdifficulty.undeadarmy.listeners;

import cc.sighs.oelib.event.Subscribe;
import com.majruszsdifficulty.internal.time.TimeHelper;
import com.majruszsdifficulty.undeadarmy.UndeadArmy;
import com.majruszsdifficulty.undeadarmy.UndeadArmyConfig;
import com.majruszsdifficulty.undeadarmy.events.OnUndeadArmyTicked;

public class MobHighlighter {
    @Subscribe
    private static void highlight(OnUndeadArmyTicked data) {
        if (!TimeHelper.haveSecondsPassed(4.0)
                || data.undeadArmy.phase.state != UndeadArmy.Phase.State.WAVE_ONGOING
                || data.undeadArmy.phase.getTicksActive() < TimeHelper.toTicks(UndeadArmyConfig.get().highlightDelay())) {
            return;
        }
        data.undeadArmy.highlight();
    }
}
