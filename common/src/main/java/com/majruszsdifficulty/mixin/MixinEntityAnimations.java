package com.majruszsdifficulty.mixin;

import com.majruszsdifficulty.internal.animations.IAnimableEntity;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class MixinEntityAnimations {
    @Inject(method = "tick", at = @At("TAIL"))
    private void majruszsdifficulty$tickAnimations(CallbackInfo callback) {
        if (this instanceof IAnimableEntity animable) {
            animable.getAnimations().tick();
        }
    }
}
