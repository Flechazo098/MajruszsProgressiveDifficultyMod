package com.majruszsdifficulty.bloodmoon.listeners;

import cc.sighs.oelib.event.Subscribe;
import cc.sighs.oelib.event.events.ServerTickEvent;
import com.majruszsdifficulty.bloodmoon.BloodMoonConfig;
import com.majruszsdifficulty.bloodmoon.BloodMoonHelper;
import com.majruszsdifficulty.internal.math.Random;

public class Trigger {
    @Subscribe
    private static void start(ServerTickEvent.Post event) {
        if (!BloodMoonConfig.get().isEnabled()
                || BloodMoonHelper.getRelativeDayTime() != BloodMoonConfig.TIME.from
                || !Random.check((float) BloodMoonConfig.get().nightTriggerChance())) {
            return;
        }
        BloodMoonHelper.start();
    }

    @Subscribe
    private static void finish(ServerTickEvent.Post event) {
        if (!BloodMoonConfig.get().isEnabled() || BloodMoonHelper.isValidDayTime() || !BloodMoonHelper.isActive()) {
            return;
        }
        BloodMoonHelper.stop();
    }
}
