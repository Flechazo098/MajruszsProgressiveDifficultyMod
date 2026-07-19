package com.majruszsdifficulty.events;

import cc.sighs.oelib.event.CancellableEvent;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

public final class ServerMobEffectApplicableEvent implements CancellableEvent {
    public final MobEffectInstance effectInstance;
    public final Holder<MobEffect> effect;
    public final LivingEntity entity;
    private boolean canceled;

    public ServerMobEffectApplicableEvent(MobEffectInstance effectInstance, LivingEntity entity) {
        this.effectInstance = effectInstance;
        this.effect = effectInstance.getEffect();
        this.entity = entity;
    }

    public void cancelEffect() {
        this.cancel();
    }

    public boolean isEffectCancelled() {
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
