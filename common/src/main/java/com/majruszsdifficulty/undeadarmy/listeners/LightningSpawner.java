package com.majruszsdifficulty.undeadarmy.listeners;

import cc.sighs.oelib.event.EventPriority;
import cc.sighs.oelib.event.Subscribe;
import com.majruszsdifficulty.internal.math.AnyPos;
import com.majruszsdifficulty.undeadarmy.UndeadArmy;
import com.majruszsdifficulty.undeadarmy.events.OnUndeadArmyStateChanged;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LightningSpawner {
    @Subscribe(priority = EventPriority.LOW)
    private static void spawn(OnUndeadArmyStateChanged data) {
        if (data.undeadArmy.phase.state != UndeadArmy.Phase.State.WAVE_PREPARING) {
            return;
        }
        List<UndeadArmy.MobInfo> mobsLeft = new ArrayList<>(data.undeadArmy.mobsLeft);
        Collections.shuffle(mobsLeft);
        int count = Math.min(mobsLeft.size(), 3);
        for (int idx = 0; idx < count; ++idx) {
            LightningBolt lightningBolt = EntityType.LIGHTNING_BOLT.create(data.getLevel());
            if (lightningBolt != null) {
                lightningBolt.moveTo(AnyPos.from(mobsLeft.get(idx).position).center().vec3());
                lightningBolt.setVisualOnly(true);
                data.getLevel().addFreshEntity(lightningBolt);
            }
        }
    }
}
