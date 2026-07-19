package com.majruszsdifficulty.features;

import cc.sighs.oelib.event.Subscribe;
import com.majruszsdifficulty.config.FeatureConfig;
import com.majruszsdifficulty.events.ServerEntityJoinEvent;
import com.majruszsdifficulty.gamestage.GameStageHelper;
import com.majruszsdifficulty.internal.entity.EntityHelper;
import com.majruszsdifficulty.mixin.IMixinCreeper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;

public class CreeperExplodeBehindWall {
    private static FeatureConfig.EnabledStageChance settings() {
        return FeatureConfig.get().creeperExplodeBehindWall();
    }

    @Subscribe
    private static void modifyAI(ServerEntityJoinEvent data) {
        if (!(data.entity instanceof Creeper creeper) || !settings().isEnabled()
                || !data.isAtLeast(GameStageHelper.find(settings().requiredGameStage()))
                || !data.passesChance((float) settings().chance(), settings().isScaledByCrd())) {
            return;
        }

        EntityHelper.getGoalSelector(creeper).addGoal(1, new ExplodeBehindWallGoal(creeper));
    }

    public static class ExplodeBehindWallGoal extends Goal {
        private static final double MAXIMUM_START_DISTANCE = 19.0;
        private static final double MAXIMUM_EXPLODE_DISTANCE = 49.0;
        private static final double OFFSET = 16.0;
        private final Creeper creeper;
        private LivingEntity attackTarget;

        public ExplodeBehindWallGoal(Creeper creeper) {
            this.creeper = creeper;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            LivingEntity target = this.getNearestPlayer(this.creeper);

            return this.creeper.getSwellDir() > 0
                    || target != null
                    && this.creeper.distanceToSqr(target) < MAXIMUM_START_DISTANCE * getDistanceMultiplier();
        }

        @Override
        public void start() {
            this.creeper.getNavigation().stop();
            this.attackTarget = this.getNearestPlayer(this.creeper);
        }

        @Override
        public void stop() {
            this.attackTarget = null;
        }

        @Override
        public void tick() {
            if (this.attackTarget == null || this.creeper.distanceToSqr(this.attackTarget) > MAXIMUM_EXPLODE_DISTANCE * this.getDistanceMultiplier()) {
                this.creeper.setSwellDir(-1); // stops creeper's explosion
            } else {
                this.creeper.setSwellDir(1);
            }
        }

        private double getDistanceMultiplier() {
            double sizeMultiplier = ((IMixinCreeper) this.creeper).getExplosionRadius() / 3.0f;
            double chargedMultiplier = this.creeper.isPowered() ? 2.0 : 1.0;

            return sizeMultiplier * chargedMultiplier;
        }

        private @Nullable Player getNearestPlayer(Creeper creeper) {
            if (!(creeper.level() instanceof ServerLevel level)) {
                return null;
            }

            Player nearestPlayer = null;
            for (Player player : EntityHelper.getEntitiesNearby(Player.class, level, creeper.position(), OFFSET)) {
                if (EntityHelper.isOnCreativeMode(player) || EntityHelper.isOnSpectatorMode(player)) {
                    continue;
                }

                if (nearestPlayer == null || creeper.distanceToSqr(player) < creeper.distanceToSqr(nearestPlayer)) {
                    nearestPlayer = player;
                }
            }

            return nearestPlayer;
        }
    }
}
