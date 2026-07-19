package com.majruszsdifficulty.bloodmoon.listeners;

import com.majruszsdifficulty.bloodmoon.BloodMoonConfig;
import com.majruszsdifficulty.bloodmoon.BloodMoonHelper;

public class DifficultyIncreaser {
    public static float modify(float crd) {
        return BloodMoonHelper.isActive() ? crd + (float) BloodMoonConfig.get().crdPenalty() : crd;
    }
}
