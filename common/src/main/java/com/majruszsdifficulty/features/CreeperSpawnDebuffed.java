package com.majruszsdifficulty.features;

import cc.sighs.oelib.event.Subscribe;
import com.majruszsdifficulty.config.MobFeatureConfig;
import com.majruszsdifficulty.events.ServerEntityJoinEvent;
import com.majruszsdifficulty.gamestage.GameStageHelper;
import com.majruszsdifficulty.internal.math.Random;
import net.minecraft.world.entity.monster.Creeper;

public class CreeperSpawnDebuffed {
    @Subscribe
    private static void applyRandomEffect(ServerEntityJoinEvent data) {
        MobFeatureConfig.CreeperSpawnDebuffed settings = MobFeatureConfig.get().creeperSpawnDebuffed();
        if (!(data.entity instanceof Creeper) || data.isLoadedFromDisk || !settings.isEnabled()
                || !data.isAtLeast(GameStageHelper.find(settings.requiredGameStage()))
                || !data.passesChance((float) settings.chance(), settings.isScaledByCrd())) {
            return;
        }
        ((Creeper) data.entity).addEffect(Random.next(settings.effects()).toEffectInstance());
    }
}
