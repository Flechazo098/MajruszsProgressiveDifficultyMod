package com.majruszsdifficulty.registration;

import cc.sighs.oelib.registry.extra.SpawnPlacementsRegister;
import com.majruszsdifficulty.entity.CursedArmor;
import com.majruszsdifficulty.registry.ModEntities;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.levelgen.Heightmap;

public final class SpawnPlacementRegistration {
    public static void register() {
        SpawnPlacementsRegister.register(ModEntities.ILLUSIONER_ENTITY, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules);
        SpawnPlacementsRegister.register(ModEntities.TANK_ENTITY, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules);
        SpawnPlacementsRegister.register(ModEntities.CURSED_ARMOR_ENTITY, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, CursedArmor::checkMonsterSpawnRules);
        SpawnPlacementsRegister.register(ModEntities.CERBERUS_ENTITY, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules);
    }

    private SpawnPlacementRegistration() {
    }
}
