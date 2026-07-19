package com.majruszsdifficulty.mixin.fabric;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.majruszsdifficulty.features.SpawnBlocker;
import net.minecraft.util.SpawnUtil;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.LevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Mirrors NeoForge's PositionCheck hook in SpawnUtil for callers using NATURAL spawn type.
 */
@Mixin(SpawnUtil.class)
public abstract class MixinSpawnUtil {
    @WrapOperation(
            method = "trySpawnMob",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/Mob;checkSpawnRules(Lnet/minecraft/world/level/LevelAccessor;Lnet/minecraft/world/entity/MobSpawnType;)Z"
            )
    )
    private static boolean majruszsdifficulty$checkSpawnPosition(Mob mob, LevelAccessor level,
                                                                 MobSpawnType spawnType, Operation<Boolean> original
    ) {
        if (spawnType == MobSpawnType.NATURAL && SpawnBlocker.isForbidden(mob)) {
            return false;
        }
        return original.call(mob, level, spawnType);
    }
}
