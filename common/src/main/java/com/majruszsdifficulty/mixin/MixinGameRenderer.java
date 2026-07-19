package com.majruszsdifficulty.mixin;

import com.majruszsdifficulty.effects.bleeding.BleedingPostProcessor;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public abstract class MixinGameRenderer {
    @Inject(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/Minecraft;getMainRenderTarget()Lcom/mojang/blaze3d/pipeline/RenderTarget;",
                    ordinal = 0
            )
    )
    private void majruszsdifficulty$processBloodPostEffect(DeltaTracker deltaTracker, boolean renderLevel, CallbackInfo callback) {
        BleedingPostProcessor.process(
                deltaTracker.getGameTimeDeltaTicks(),
                deltaTracker.getGameTimeDeltaPartialTick(true)
        );
    }

    @Inject(method = "close", at = @At("HEAD"))
    private void majruszsdifficulty$closeBloodPostEffect(CallbackInfo callback) {
        BleedingPostProcessor.close();
    }
}
