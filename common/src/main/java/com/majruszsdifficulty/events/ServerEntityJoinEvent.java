package com.majruszsdifficulty.events;

import cc.sighs.oelib.event.CancellableEvent;
import com.majruszsdifficulty.gamestage.GameStage;
import com.majruszsdifficulty.gamestage.GameStageHelper;
import com.majruszsdifficulty.internal.level.LevelHelper;
import com.majruszsdifficulty.internal.math.Random;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

/**
 * Fired at NeoForge's EntityJoinLevelEvent patch point and its Fabric-equivalent Mixin point.
 */
public final class ServerEntityJoinEvent implements CancellableEvent {
    public final Entity entity;
    public final boolean isLoadedFromDisk;
    private boolean canceled;

    public ServerEntityJoinEvent(Entity entity, boolean loadedFromDisk) {
        this.entity = entity;
        this.isLoadedFromDisk = loadedFromDisk;
    }

    public ServerLevel getLevel() {
        return (ServerLevel) this.entity.level();
    }

    public ServerLevel getServerLevel() {
        return this.getLevel();
    }

    public Vec3 getPosition() {
        return this.entity.position();
    }

    public boolean isAtLeast(GameStage stage) {
        return GameStageHelper.determineGameStage(this.getLevel(), this.getPosition()).getOrdinal() >= stage.getOrdinal();
    }

    public boolean passesChance(float chance, boolean scaledByCrd) {
        float actual = scaledByCrd ? chance * (float) LevelHelper.getClampedRegionalDifficultyAt(this.getLevel(), this.entity.blockPosition()) : chance;
        return Random.check(actual);
    }

    public void cancelSpawn() {
        if (!(this.entity instanceof Player)) {
            this.cancel();
        }
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
