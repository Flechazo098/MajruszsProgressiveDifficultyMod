package com.majruszsdifficulty.mixin;

import com.majruszsdifficulty.items.SoulJar;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class MixinLivingEntitySwimSpeed {
    @Inject(method = "getWaterSlowDown", at = @At("RETURN"), cancellable = true)
    private void majruszsdifficulty$soulJarSwimBonus(CallbackInfoReturnable<Float> callback) {
        LivingEntity entity = (LivingEntity) (Object) this;
        callback.setReturnValue(SoulJar.applySwimBonus(entity, callback.getReturnValue()));
    }
}
