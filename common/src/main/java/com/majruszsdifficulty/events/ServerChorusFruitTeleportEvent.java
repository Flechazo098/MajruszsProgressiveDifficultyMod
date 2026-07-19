package com.majruszsdifficulty.events;

import cc.sighs.oelib.event.CancellableEvent;
import net.minecraft.world.entity.LivingEntity;

public final class ServerChorusFruitTeleportEvent implements CancellableEvent {
    public final LivingEntity entity;
    private boolean canceled;

    public ServerChorusFruitTeleportEvent(LivingEntity entity) {
        this.entity = entity;
    }

    public void cancelTeleport() {
        this.cancel();
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
