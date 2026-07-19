package com.majruszsdifficulty.bloodmoon;

import cc.sighs.oelib.event.EventBus;
import com.majruszsdifficulty.bloodmoon.events.OnBloodMoonFinished;
import com.majruszsdifficulty.bloodmoon.events.OnBloodMoonStarted;

public class BloodMoon {
    boolean isActive = false;

    public boolean start() {
        if (!this.isActive && BloodMoonHelper.isValidDayTime() && !EventBus.post(new OnBloodMoonStarted())) {
            this.isActive = true;

            return true;
        }

        return false;
    }

    public boolean finish() {
        if (this.isActive) {
            EventBus.post(new OnBloodMoonFinished());
            this.isActive = false;

            return true;
        }

        return false;
    }

    public boolean isActive() {
        return this.isActive;
    }
}
