package com.majruszsdifficulty.mixin.fabric;

import cc.sighs.oelib.event.EventBus;
import com.llamalad7.mixinextras.sugar.Local;
import com.majruszsdifficulty.events.ServerExplosionStartEvent;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Level.class)
public abstract class MixinLevel {
    @Inject(
            method = "explode(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/damagesource/DamageSource;Lnet/minecraft/world/level/ExplosionDamageCalculator;DDDFZLnet/minecraft/world/level/Level$ExplosionInteraction;ZLnet/minecraft/core/particles/ParticleOptions;Lnet/minecraft/core/particles/ParticleOptions;Lnet/minecraft/core/Holder;)Lnet/minecraft/world/level/Explosion;",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/Explosion;explode()V"
            ),
            cancellable = true
    )
    private void majruszsdifficulty$beforeExplosion(Entity source, DamageSource damageSource,
                                                     ExplosionDamageCalculator damageCalculator,
                                                     double x, double y, double z, float radius, boolean fire,
                                                     Level.ExplosionInteraction interaction, boolean spawnParticles,
                                                     ParticleOptions smallParticles, ParticleOptions largeParticles,
                                                     Holder<SoundEvent> sound,
                                                     CallbackInfoReturnable<Explosion> callback,
                                                     @Local Explosion explosion) {
        if ((Object) this instanceof ServerLevel level
                && EventBus.post(new ServerExplosionStartEvent(level, explosion))) {
            callback.setReturnValue(explosion);
        }
    }
}
