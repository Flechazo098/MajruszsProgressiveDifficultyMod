package com.majruszsdifficulty.mixin.fabric;

import cc.sighs.oelib.event.EventBus;
import com.majruszsdifficulty.events.ServerEndermanAngerEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnderMan.class)
public abstract class MixinEnderMan {
    @Inject(method = "isLookingAtMe", at = @At("HEAD"), cancellable = true)
    private void majruszsdifficulty$beforeAnger(Player player, CallbackInfoReturnable<Boolean> callback) {
        EnderMan enderMan = (EnderMan) (Object) this;
        if (enderMan.level() instanceof ServerLevel && EventBus.post(new ServerEndermanAngerEvent(enderMan, player))) {
            callback.setReturnValue(false);
        }
    }
}
