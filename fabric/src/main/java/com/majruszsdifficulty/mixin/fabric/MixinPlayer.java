package com.majruszsdifficulty.mixin.fabric;

import cc.sighs.oelib.event.EventBus;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.majruszsdifficulty.events.ServerPlayerExperienceChangeEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Player.class)
public abstract class MixinPlayer {
    @WrapMethod(method = "giveExperiencePoints")
    private void majruszsdifficulty$changeExperience(int amount, Operation<Void> original) {
        if ((Object) this instanceof ServerPlayer player) {
            ServerPlayerExperienceChangeEvent event = new ServerPlayerExperienceChangeEvent(player, amount);
            if (!EventBus.post(event)) {
                original.call(event.amount);
            }
        } else {
            original.call(amount);
        }
    }
}
