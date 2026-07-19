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

@Mixin(SpawnUtil.class)
public abstract class MixinSpawnUtil {
    @WrapOperation(
            method = "trySpawnMob(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/world/entity/MobSpawnType;Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;IIILnet/minecraft/util/SpawnUtil$Strategy;)Ljava/util/Optional;",
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
