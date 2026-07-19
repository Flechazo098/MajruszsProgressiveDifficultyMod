package com.majruszsdifficulty.events;

import cc.sighs.oelib.event.CancellableEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

public final class ServerLivingEntityIncomingDamageEvent implements CancellableEvent {
    public final DamageSource source;
    public final @Nullable LivingEntity attacker;
    public final LivingEntity target;
    public final float original;
    public float damage;
    public boolean spawnCriticalParticles;
    public boolean spawnMagicParticles;
    private boolean canceled;

    public ServerLivingEntityIncomingDamageEvent(DamageSource source, LivingEntity target, float damage) {
        this.source = source;
        this.attacker = source.getEntity() instanceof LivingEntity livingEntity ? livingEntity : null;
        this.target = target;
        this.original = damage;
        this.damage = damage;
    }

    public ServerLevel getLevel() {
        return (ServerLevel) this.target.level();
    }

    public ServerLevel getServerLevel() {
        return this.getLevel();
    }

    public void cancelDamage() {
        this.cancel();
    }

    public boolean isDamageCancelled() {
        return this.isCanceled() || this.damage <= 0.0f;
    }

    public boolean isDirect() {
        return this.source.isDirect();
    }

    public boolean willTakeFullDamage() {
        return this.target.invulnerableTime <= 10;
    }

    @Override
    public boolean isCanceled() {
        return this.canceled;
    }

    @Override
    public void setCanceled(boolean canceled) {
        this.canceled = canceled;
    }
}
