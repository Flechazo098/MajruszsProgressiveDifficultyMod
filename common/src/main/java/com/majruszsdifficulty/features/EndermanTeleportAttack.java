package com.majruszsdifficulty.features;

import cc.sighs.oelib.event.Subscribe;
import com.majruszsdifficulty.MajruszsDifficulty;
import com.majruszsdifficulty.config.FeatureConfig;
import com.majruszsdifficulty.events.ServerLivingEntityDamagedEvent;
import com.majruszsdifficulty.gamestage.GameStageHelper;
import com.majruszsdifficulty.internal.level.LevelHelper;
import com.majruszsdifficulty.internal.math.Random;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.monster.EnderMan;

public class EndermanTeleportAttack {
    private static FeatureConfig.EnabledStageChance settings() {
        return FeatureConfig.get().endermanTeleportAttack();
    }

    @Subscribe
    private static void teleportRandomly(ServerLivingEntityDamagedEvent data) {
        FeatureConfig.EnabledStageChance settings = settings();
        float chance = (float) settings.chance();
        if (settings.isScaledByCrd()) {
            chance *= LevelHelper.getClampedRegionalDifficultyAt(data.getLevel(), data.target.blockPosition());
        }
        if (!settings.isEnabled()
                || !(data.attacker instanceof EnderMan)
                || !data.source.isDirect()
                || GameStageHelper.determineGameStage(data.getLevel(), data.target.position()).getOrdinal()
                < GameStageHelper.find(settings.requiredGameStage()).getOrdinal()
                || !Random.check(chance)) {
            return;
        }
        if (LevelHelper.teleportNearby(data.target, data.getServerLevel(), 6.0) && data.target instanceof ServerPlayer player) {
            MajruszsDifficulty.triggerAdvancement(player, "enderman_teleport_attack");
        }
    }
}
