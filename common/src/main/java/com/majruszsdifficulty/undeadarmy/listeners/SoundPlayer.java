package com.majruszsdifficulty.undeadarmy.listeners;

import cc.sighs.oelib.event.Subscribe;
import com.majruszsdifficulty.internal.emitter.SoundEmitter;
import com.majruszsdifficulty.internal.math.AnyPos;
import com.majruszsdifficulty.internal.math.Random;
import com.majruszsdifficulty.registry.ModSounds;
import com.majruszsdifficulty.undeadarmy.UndeadArmy;
import com.majruszsdifficulty.undeadarmy.UndeadArmyConfig;
import com.majruszsdifficulty.undeadarmy.events.OnUndeadArmyStarted;
import com.majruszsdifficulty.undeadarmy.events.OnUndeadArmyStateChanged;
import net.minecraft.world.phys.Vec3;

public class SoundPlayer {
    @Subscribe
    private static void playStart(OnUndeadArmyStarted data) {
        SoundEmitter.of(ModSounds.UNDEAD_ARMY_APPROACHING_SOUND)
                .position(AnyPos.from(data.undeadArmy.position).vec3())
                .volume(Random.nextFloat(0.2f, 0.3f))
                .pitch(Random.nextFloat(0.9f, 1.1f))
                .emit(data.getLevel());
    }

    @Subscribe
    private static void playWaveStart(OnUndeadArmyStateChanged data) {
        if (data.undeadArmy.phase.state != UndeadArmy.Phase.State.WAVE_ONGOING) {
            return;
        }
        Vec3 position = AnyPos.from(data.undeadArmy.position)
                .add(data.undeadArmy.direction.x, 0, data.undeadArmy.direction.z)
                .mul(UndeadArmyConfig.get().areaRadius() - 15)
                .mul(1, 0, 1)
                .vec3();

        data.undeadArmy.participants.forEach(player -> {
            SoundEmitter.of(ModSounds.UNDEAD_ARMY_WAVE_STARTED_SOUND)
                    .position(new Vec3(position.x, player.getY(), position.z))
                    .volume(Random.nextFloat(50.0f, 80.0f))
                    .pitch(Random.nextFloat(0.9f, 1.1f))
                    .send(player);
        });
    }
}
