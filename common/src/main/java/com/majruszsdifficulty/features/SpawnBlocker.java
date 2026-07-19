package com.majruszsdifficulty.features;

import com.majruszsdifficulty.config.GameplayConfig;
import com.majruszsdifficulty.gamestage.GameStageHelper;
import com.majruszsdifficulty.gamestage.GameStageValue;
import com.majruszsdifficulty.internal.text.RegexString;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;

import java.util.List;

public class SpawnBlocker {
    public static boolean isForbidden(Entity entity) {
        if (!(entity.level() instanceof ServerLevel level)) {
            return false;
        }
        String id = BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType()).toString();
        List<RegexString> forbiddenIds = GameStageValue.of(GameplayConfig.get().spawnBlocker().forbiddenEntities())
                .get(GameStageHelper.determineGameStage(level, entity.position()))
                .stream().map(RegexString::new).toList();
        return forbiddenIds.stream().anyMatch(pattern -> pattern.matches(id));
    }

}
