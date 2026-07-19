package com.majruszsdifficulty.mixin;

import net.minecraft.world.entity.MobCategory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MobCategory.class)
public abstract class MixinMobCategory {
    @Inject(method = "getMaxInstancesPerChunk", at = @At("RETURN"), cancellable = true)
    private void majruszsdifficulty$modifySpawnLimit(CallbackInfoReturnable<Integer> callback) {
        MobCategory category = (MobCategory) (Object) this;
        int original = callback.getReturnValue();
        float value = com.majruszsdifficulty.bloodmoon.listeners.SpawnRateIncreaser.modify(category, original);
        value = com.majruszsdifficulty.features.SpawnRateIncreaser.modify(category, value);
        callback.setReturnValue(Math.max(1, Math.round(value)));
    }
}
