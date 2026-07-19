package com.majruszsdifficulty.bloodmoon;

import cc.sighs.oelib.event.EventSide;
import cc.sighs.oelib.event.Subscribe;
import cc.sighs.oelib.event.events.ClientTickEvent;
import com.majruszsdifficulty.internal.annotation.Dist;
import com.majruszsdifficulty.internal.annotation.OnlyIn;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.level.Level;

@OnlyIn(Dist.CLIENT)
public class BloodMoonClient {
    static float COLOR_RATIO = 0.0f;

    @Subscribe(side = EventSide.CLIENT)
    private static void tick(ClientTickEvent.Post event) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) {
            return;
        }

        long relativeTime = level.getDayTime() % Level.TICKS_PER_DAY;
        if (BloodMoonHelper.isActive() && BloodMoonConfig.TIME.within(relativeTime)) {
            COLOR_RATIO = (float) (2.0 * (relativeTime - BloodMoonConfig.TIME.from) / (BloodMoonConfig.TIME.to - BloodMoonConfig.TIME.from) - 1.0);
            COLOR_RATIO = 1.0f - Math.abs(COLOR_RATIO * COLOR_RATIO * COLOR_RATIO);
        } else {
            COLOR_RATIO = 0.0f;
        }
    }
}
