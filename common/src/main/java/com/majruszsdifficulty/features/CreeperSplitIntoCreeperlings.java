package com.majruszsdifficulty.features;

import cc.sighs.oelib.event.Subscribe;
import com.majruszsdifficulty.MajruszsDifficulty;
import com.majruszsdifficulty.config.GameplayConfig;
import com.majruszsdifficulty.entity.Creeperling;
import com.majruszsdifficulty.events.ServerExplosionDetonateEvent;
import com.majruszsdifficulty.events.ServerLivingEntityDeathEvent;
import com.majruszsdifficulty.gamestage.GameStage;
import com.majruszsdifficulty.gamestage.GameStageHelper;
import com.majruszsdifficulty.gamestage.GameStageValue;
import com.majruszsdifficulty.internal.entity.EntityHelper;
import com.majruszsdifficulty.internal.level.LevelHelper;
import com.majruszsdifficulty.internal.math.AnyPos;
import com.majruszsdifficulty.internal.math.Random;
import com.majruszsdifficulty.registry.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.phys.AABB;

public class CreeperSplitIntoCreeperlings {
    @Subscribe
    private static void spawnCreeperlings(ServerExplosionDetonateEvent data) {
        GameplayConfig.CreeperSplit settings = GameplayConfig.get().creeperSplitIntoCreeperlings();
        if (!settings.isEnabled() || !(data.explosion.getDirectSourceEntity() instanceof Creeper creeper)
                || creeper.getType() != EntityType.CREEPER) {
            return;
        }
        float chance = settings.isScaledByCrd()
                ? (float) settings.chance() * (float) LevelHelper.getClampedRegionalDifficultyAt(data.level, BlockPos.containing(data.position))
                : (float) settings.chance();
        if (!Random.check(chance)) {
            return;
        }
        GameStage gameStage = GameStageHelper.determineGameStage(data.level, data.position);
        int count = Random.nextInt(1, GameStageValue.of(settings.count()).get(gameStage) + 1);
        for (int i = 0; i < count; ++i) {
            Creeperling creeperling = EntityHelper.createSpawner(ModEntities.CREEPERLING_ENTITY, data.getLevel())
                    .position(AnyPos.from(creeper.blockPosition()).add(Random.nextVector(-2, 2, -1, 1, -2, 2)).center().vec3())
                    .mobSpawnType(MobSpawnType.EVENT)
                    .spawn();

            data.skipEntityIf(entity -> entity.equals(creeperling));
        }
    }

    @Subscribe
    private static void giveAdvancement(ServerExplosionDetonateEvent data) {
        if (!(data.explosion.getDirectSourceEntity() instanceof Creeperling)) {
            return;
        }
        data.getServerLevel()
                .getEntitiesOfClass(ServerPlayer.class, new AABB(AnyPos.from(data.position).block()).inflate(10.0, 6.0, 10.0))
                .forEach(CreeperSplitIntoCreeperlings::giveAdvancement);
    }

    @Subscribe
    private static void giveAdvancement(ServerLivingEntityDeathEvent data) {
        if (data.attacker instanceof ServerPlayer player && data.target instanceof Creeperling) {
            CreeperSplitIntoCreeperlings.giveAdvancement(player);
        }
    }

    private static void giveAdvancement(ServerPlayer player) {
        MajruszsDifficulty.triggerAdvancement(player, "encountered_creeperling");
    }
}
