package com.majruszsdifficulty.mixin.fabric;

import com.majruszsdifficulty.features.SpawnBlocker;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.NaturalSpawner;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(NaturalSpawner.class)
public abstract class MixinNaturalSpawner {
    @Inject(
            method = "isValidPositionForMob(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/Mob;D)Z",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/Mob;checkSpawnRules(Lnet/minecraft/world/level/LevelAccessor;Lnet/minecraft/world/entity/MobSpawnType;)Z"
            ),
            cancellable = true
    )
    private static void majruszsdifficulty$checkNaturalSpawnPosition(ServerLevel level, Mob mob, double distance,
                                                                     CallbackInfoReturnable<Boolean> callback) {
        if (SpawnBlocker.isForbidden(mob)) {
            callback.setReturnValue(false);
        }
    }
}
