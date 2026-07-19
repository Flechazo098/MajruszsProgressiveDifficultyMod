package com.majruszsdifficulty.bloodmoon;

import com.majruszsdifficulty.internal.annotation.Dist;
import com.majruszsdifficulty.internal.annotation.OnlyIn;
import com.majruszsdifficulty.internal.platform.Side;
import com.majruszsdifficulty.world.DifficultySavedData;
import net.minecraft.world.level.Level;

import java.util.Optional;

public class BloodMoonHelper {
    private static BloodMoon BLOOD_MOON = new BloodMoon();

    public static boolean start() {
        if (BLOOD_MOON.start()) {
            DifficultySavedData.markDirty();
            DifficultySavedData.syncAll();
            return true;
        }

        return false;
    }

    public static boolean stop() {
        if (BLOOD_MOON.finish()) {
            DifficultySavedData.markDirty();
            DifficultySavedData.syncAll();
            return true;
        }

        return false;
    }

    @OnlyIn(Dist.CLIENT)
    public static float getColorRatio() {
        return BloodMoonClient.COLOR_RATIO;
    }

    public static long getRelativeDayTime() {
        return Optional.ofNullable(Side.getServer()).map(server -> server.overworld().getDayTime() % Level.TICKS_PER_DAY).orElse(0L);
    }

    public static boolean isActive() {
        return BLOOD_MOON.isActive();
    }

    public static void load(boolean active) {
        BLOOD_MOON = new BloodMoon();
        BLOOD_MOON.isActive = active;
    }

    public static boolean isValidDayTime() {
        return BloodMoonConfig.TIME.within(BloodMoonHelper.getRelativeDayTime());
    }
}
