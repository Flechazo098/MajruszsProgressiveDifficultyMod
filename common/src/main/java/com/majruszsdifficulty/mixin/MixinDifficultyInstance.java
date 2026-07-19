package com.majruszsdifficulty.mixin;

import com.majruszsdifficulty.bloodmoon.listeners.DifficultyIncreaser;
import com.majruszsdifficulty.gamestage.listeners.ClampedRegionalDifficultyIncreaser;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DifficultyInstance.class)
public abstract class MixinDifficultyInstance {
    @Inject(method = "getSpecialMultiplier", at = @At("RETURN"), cancellable = true)
    private void majruszsdifficulty$modifyRegionalDifficulty(CallbackInfoReturnable<Float> callback) {
        float original = callback.getReturnValue();
        callback.setReturnValue(Mth.clamp(ClampedRegionalDifficultyIncreaser.modify(DifficultyIncreaser.modify(original)), 0.0f, 1.0f));
    }
}
