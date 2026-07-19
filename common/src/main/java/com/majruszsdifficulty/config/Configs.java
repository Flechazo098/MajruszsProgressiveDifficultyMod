package com.majruszsdifficulty.config;

import cc.sighs.oelib.config.ConfigManager;
import com.majruszsdifficulty.bloodmoon.BloodMoonConfig;
import com.majruszsdifficulty.effects.bleeding.BleedingConfig;
import com.majruszsdifficulty.gamestage.GameStageConfig;
import com.majruszsdifficulty.undeadarmy.UndeadArmyConfig;

public final class Configs {
    private static boolean registered;

    public static void register() {
        if (registered) {
            return;
        }

        registered = true;
        ConfigManager.registerServer(FeatureConfig.UNIT, player -> player.hasPermissions(4));
        ConfigManager.registerServer(AdvancedFeatureConfig.UNIT, player -> player.hasPermissions(4));
        ConfigManager.registerServer(BloodMoonConfig.UNIT, player -> player.hasPermissions(4));
        ConfigManager.registerServer(GameplayConfig.UNIT, player -> player.hasPermissions(4));
        ConfigManager.registerServer(GameStageConfig.UNIT, player -> player.hasPermissions(4));
        ConfigManager.registerServer(ItemConfig.UNIT, player -> player.hasPermissions(4));
        ConfigManager.registerServer(MobFeatureConfig.UNIT, player -> player.hasPermissions(4));
        ConfigManager.registerServer(BleedingConfig.UNIT, player -> player.hasPermissions(4));
        ConfigManager.registerServer(UndeadArmyConfig.UNIT, player -> player.hasPermissions(4));
    }

    private Configs() {
    }
}
