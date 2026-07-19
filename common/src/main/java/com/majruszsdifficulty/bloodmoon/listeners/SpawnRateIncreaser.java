package com.majruszsdifficulty.bloodmoon.listeners;

import com.majruszsdifficulty.bloodmoon.BloodMoonConfig;
import com.majruszsdifficulty.bloodmoon.BloodMoonHelper;
import net.minecraft.world.entity.MobCategory;

public class SpawnRateIncreaser {
    public static float modify(MobCategory category, float value) {
        return BloodMoonHelper.isActive() && category == MobCategory.MONSTER
                ? value * (float) BloodMoonConfig.get().spawnRateMultiplier()
                : value;
    }
}
