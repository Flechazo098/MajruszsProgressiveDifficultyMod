package com.majruszsdifficulty.events;

import cc.sighs.oelib.event.Event;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

public final class ServerLivingEntityDamagedEvent implements Event {
    public final DamageSource source;
    public final @Nullable LivingEntity attacker;
    public final LivingEntity target;
    public final float damage;

    public ServerLivingEntityDamagedEvent(DamageSource source, LivingEntity target, float damage) {
        this.source = source;
        this.attacker = source.getEntity() instanceof LivingEntity livingEntity ? livingEntity : null;
        this.target = target;
        this.damage = damage;
    }

    public ServerLevel getLevel() {
        return (ServerLevel) this.target.level();
    }

    public ServerLevel getServerLevel() {
        return this.getLevel();
    }

    public boolean isDirect() {
        return this.source.isDirect();
    }
}
