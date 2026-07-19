package com.majruszsdifficulty.mixin.fabric;

import cc.sighs.oelib.event.EventBus;
import com.majruszsdifficulty.events.ServerPlayerChangedDimensionEvent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.portal.DimensionTransition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayer.class)
public abstract class MixinServerPlayer {
    @Unique
    private ResourceKey<Level> majruszsdifficulty$previousDimension;

    @Inject(method = "changeDimension(Lnet/minecraft/world/level/portal/DimensionTransition;)Lnet/minecraft/world/entity/Entity;", at = @At("HEAD"))
    private void majruszsdifficulty$beforeDimensionChange(DimensionTransition transition,
                                                          CallbackInfoReturnable<Entity> callback) {
        this.majruszsdifficulty$previousDimension = ((ServerPlayer) (Object) this).level().dimension();
    }

    @Inject(method = "changeDimension(Lnet/minecraft/world/level/portal/DimensionTransition;)Lnet/minecraft/world/entity/Entity;", at = @At("RETURN"))
    private void majruszsdifficulty$afterDimensionChange(DimensionTransition transition,
                                                         CallbackInfoReturnable<Entity> callback) {
        ServerPlayer player = (ServerPlayer) (Object) this;
        if (callback.getReturnValue() == player
                && this.majruszsdifficulty$previousDimension != player.level().dimension()) {
            EventBus.post(new ServerPlayerChangedDimensionEvent(player, player.serverLevel()));
        }
    }
}
