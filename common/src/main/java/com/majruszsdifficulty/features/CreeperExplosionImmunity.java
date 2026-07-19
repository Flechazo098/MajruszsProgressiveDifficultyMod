package com.majruszsdifficulty.features;

import cc.sighs.oelib.event.Subscribe;
import com.majruszsdifficulty.config.AdvancedFeatureConfig;
import com.majruszsdifficulty.events.ServerLivingEntityIncomingDamageEvent;
import com.majruszsdifficulty.gamestage.GameStageHelper;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.entity.monster.Creeper;

public class CreeperExplosionImmunity {
    private static AdvancedFeatureConfig.CreeperExplosionImmunity settings() {
        return AdvancedFeatureConfig.get().creeperExplosionImmunity();
    }

    @Subscribe
    private static void reduceDamage(ServerLivingEntityIncomingDamageEvent data) {
        AdvancedFeatureConfig.CreeperExplosionImmunity settings = settings();
        if (settings.isEnabled()
                && data.target instanceof Creeper
                && data.source.is(DamageTypeTags.IS_EXPLOSION)
                && GameStageHelper.determineGameStage(data.getLevel(), data.target.position()).getOrdinal()
                >= GameStageHelper.find(settings.requiredGameStage()).getOrdinal()) {
            data.damage *= (float) settings.damageMultiplier();
        }
    }
}
