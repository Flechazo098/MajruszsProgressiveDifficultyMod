package com.majruszsdifficulty.registry;

import cc.sighs.oelib.registry.DeferredRegister;
import cc.sighs.oelib.registry.RegisterSupplier;
import com.majruszsdifficulty.MajruszsDifficulty;
import com.majruszsdifficulty.internal.emitter.ParticleEmitter;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;

public final class ModParticles {
    private static final DeferredRegister<ParticleType<?>> REGISTER = DeferredRegister.create(Registries.PARTICLE_TYPE, MajruszsDifficulty.MOD_ID);

    public static final RegisterSupplier<SimpleParticleType> BLOOD_PARTICLE = REGISTER.register("blood", ModParticles::createBloodParticle);

    public static void register() {
        REGISTER.register();
    }

    private static SimpleParticleType createBloodParticle() {
        SimpleParticleType particle = new SimpleParticleType(true) {
        };
        ParticleEmitter.setDefault(particle, new ParticleEmitter.Properties(ParticleEmitter.offset(0.5f), ParticleEmitter.speed(0.025f, 0.075f)));
        return particle;
    }

    private ModParticles() {
    }
}
