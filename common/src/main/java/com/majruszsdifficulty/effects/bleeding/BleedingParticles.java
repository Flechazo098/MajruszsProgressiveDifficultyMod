package com.majruszsdifficulty.effects.bleeding;

import cc.sighs.oelib.event.EventPriority;
import cc.sighs.oelib.event.Subscribe;
import com.majruszsdifficulty.events.ServerLivingEntityDeathEvent;
import com.majruszsdifficulty.events.ServerLivingEntityIncomingDamageEvent;
import com.majruszsdifficulty.events.ServerLivingEntityTickEvent;
import com.majruszsdifficulty.internal.emitter.ParticleEmitter;
import com.majruszsdifficulty.internal.entity.EffectHelper;
import com.majruszsdifficulty.internal.entity.EntityHelper;
import com.majruszsdifficulty.internal.math.AnyPos;
import com.majruszsdifficulty.internal.math.Random;
import com.majruszsdifficulty.internal.time.TimeHelper;
import com.majruszsdifficulty.network.BleedingGuiPacket;
import com.majruszsdifficulty.registry.ModDamageTypes;
import com.majruszsdifficulty.registry.ModEffects;
import com.majruszsdifficulty.registry.ModParticles;
import net.minecraft.server.level.ServerPlayer;

public class BleedingParticles {
    @Subscribe
    private static void emit(ServerLivingEntityTickEvent data) {
        if (!TimeHelper.haveSecondsPassed(0.15f)
                || !EffectHelper.has(ModEffects.BLEEDING_EFFECT, data.entity())) {
            return;
        }
        int amplifier = EffectHelper.getAmplifier(ModEffects.BLEEDING_EFFECT, data.entity()).orElse(0);
        float walkDistanceDelta = EntityHelper.getWalkDistanceDelta(data.entity());

        ParticleEmitter.of(ModParticles.BLOOD_PARTICLE)
                .count(Random.round(0.5 + 0.5 * (15.0 + amplifier) * walkDistanceDelta))
                .sizeBased(data.entity())
                .offset(AnyPos.from(data.entity().getBbWidth(), data.entity().getBbHeight(), data.entity().getBbWidth()).mul(0.25, 0.25, 0.25).vec3())
                .emit(data.getServerLevel());
    }

    @Subscribe
    private static void emit(ServerLivingEntityDeathEvent data) {
        if (!EffectHelper.has(ModEffects.BLEEDING_EFFECT, data.target)) {
            return;
        }
        ParticleEmitter.of(ModParticles.BLOOD_PARTICLE)
                .count(50)
                .sizeBased(data.target)
                .offset(AnyPos.from(data.target.getBbWidth(), data.target.getBbHeight(), data.target.getBbWidth()).mul(0.25, 0.25, 0.25).vec3())
                .emit(data.getServerLevel());
    }

    @Subscribe(priority = EventPriority.LOWEST)
    private static void addGuiOverlay(ServerLivingEntityIncomingDamageEvent data) {
        if (data.source.is(ModDamageTypes.BLEEDING_DAMAGE_SOURCE) && data.target instanceof ServerPlayer player) {
            new BleedingGuiPacket(3).sendTo(player);
        }
    }
}
