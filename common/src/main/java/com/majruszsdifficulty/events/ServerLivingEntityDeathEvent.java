package com.majruszsdifficulty.events;

import cc.sighs.oelib.event.CancellableEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

public final class ServerLivingEntityDeathEvent implements CancellableEvent {
    public final DamageSource source;
    public final @Nullable LivingEntity attacker;
    public final LivingEntity target;
    private boolean canceled;

    public ServerLivingEntityDeathEvent(DamageSource source, LivingEntity target) {
        this.source = source;
        this.attacker = source.getEntity() instanceof LivingEntity livingEntity ? livingEntity : null;
        this.target = target;
    }

    public ServerLevel getLevel() {
        return (ServerLevel) this.target.level();
    }

    public ServerLevel getServerLevel() {
        return this.getLevel();
    }

    public boolean isDirect() {
        return this.source.getDirectEntity() == this.attacker;
    }

    public void cancelDeath() {
        this.cancel();
    }

    public boolean isDeathCancelled() {
        return this.isCanceled();
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
