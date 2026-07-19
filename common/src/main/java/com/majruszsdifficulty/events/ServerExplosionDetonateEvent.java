package com.majruszsdifficulty.events;

import cc.sighs.oelib.event.Event;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Predicate;

public final class ServerExplosionDetonateEvent implements Event {
    public final Explosion explosion;
    public final ServerLevel level;
    public final Vec3 position;
    public final @Nullable LivingEntity entity;
    public final List<BlockPos> affectedBlocks;
    public final List<Entity> affectedEntities;

    public ServerExplosionDetonateEvent(ServerLevel level, Explosion explosion, List<BlockPos> blocks, List<Entity> entities) {
        this.level = level;
        this.explosion = explosion;
        this.position = explosion.center();
        this.entity = explosion.getIndirectSourceEntity();
        this.affectedBlocks = blocks;
        this.affectedEntities = entities;
    }

    public ServerLevel getLevel() {
        return this.level;
    }

    public ServerLevel getServerLevel() {
        return this.level;
    }

    public void skipBlockIf(Predicate<BlockPos> predicate) {
        this.affectedBlocks.removeIf(predicate);
    }

    public void skipEntityIf(Predicate<Entity> predicate) {
        this.affectedEntities.removeIf(predicate);
    }
}
