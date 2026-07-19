package com.majruszsdifficulty.mixin.fabric;

import cc.sighs.oelib.event.EventBus;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.majruszsdifficulty.events.ServerExplosionDetonateEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Mixin(Explosion.class)
public abstract class MixinExplosion {
    @Shadow
    @Final
    private Level level;

    @ModifyExpressionValue(
            method = "explode",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getEntities(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/AABB;)Ljava/util/List;")
    )
    private List<Entity> majruszsdifficulty$onDetonate(List<Entity> entities) {
        Explosion explosion = (Explosion) (Object) this;
        if (this.level instanceof ServerLevel serverLevel) {
            EventBus.post(new ServerExplosionDetonateEvent(serverLevel, explosion, explosion.getToBlow(), entities));
        }
        return entities;
    }
}
