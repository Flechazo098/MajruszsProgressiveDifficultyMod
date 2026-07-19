package com.majruszsdifficulty.events;

import cc.sighs.oelib.event.CancellableEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

public class OnBleedingCheck implements CancellableEvent {
    public final DamageSource source;
    public final @Nullable LivingEntity attacker;
    public final LivingEntity target;
    private boolean isBleedingTriggered = false;

    public OnBleedingCheck(ServerLivingEntityDamagedEvent data) {
        this.source = data.source;
        this.attacker = data.attacker;
        this.target = data.target;
    }

    @Override
    public boolean isCanceled() {
        return this.isBleedingTriggered;
    }

    @Override
    public void setCanceled(boolean canceled) {
        this.isBleedingTriggered = canceled;
    }

    public void trigger() {
        this.cancel();
    }

    public boolean isBleedingTriggered() {
        return this.isBleedingTriggered;
    }
}
