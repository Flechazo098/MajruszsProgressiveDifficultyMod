package com.majruszsdifficulty.mixin.fabric;

import cc.sighs.oelib.event.EventBus;
import com.majruszsdifficulty.events.ServerLivingEntityTickEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerLevel.class)
public abstract class MixinServerLevel {
    @Inject(
            method = "tickNonPassenger",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/Entity;tick()V",
                    shift = At.Shift.AFTER
            )
    )
    private void majruszsdifficulty$afterEntityTick(Entity entity, CallbackInfo callback) {
        if (entity instanceof LivingEntity livingEntity) {
            EventBus.post(new ServerLivingEntityTickEvent(livingEntity));
        }
    }
}
