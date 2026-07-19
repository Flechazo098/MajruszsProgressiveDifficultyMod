package com.majruszsdifficulty.events;

import cc.sighs.oelib.event.CancellableEvent;
import com.majruszsdifficulty.mixin.IMixinExplosion;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public final class ServerExplosionStartEvent implements CancellableEvent {
    public final Explosion explosion;
    public final ServerLevel level;
    public final Vec3 position;
    public final @Nullable LivingEntity entity;
    public final float originalRadius;
    private boolean canceled;

    public ServerExplosionStartEvent(ServerLevel level, Explosion explosion) {
        this.explosion = explosion;
        this.level = level;
        this.position = explosion.center();
        this.entity = explosion.getIndirectSourceEntity();
        this.originalRadius = explosion.radius();
    }

    public ServerLevel getLevel() {
        return this.level;
    }

    public ServerLevel getServerLevel() {
        return this.level;
    }

    public void setRadius(float radius) {
        ((IMixinExplosion) this.explosion).majruszsdifficulty$setRadius(radius);
    }

    public void setFire(boolean fire) {
        ((IMixinExplosion) this.explosion).majruszsdifficulty$setFire(fire);
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
