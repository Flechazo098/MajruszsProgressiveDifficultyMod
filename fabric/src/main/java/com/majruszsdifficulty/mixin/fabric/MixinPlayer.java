package com.majruszsdifficulty.mixin.fabric;

import cc.sighs.oelib.event.EventBus;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.majruszsdifficulty.events.ServerPlayerExperienceChangeEvent;
import com.majruszsdifficulty.events.ServerLivingEntityDamagedEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Player.class)
public abstract class MixinPlayer {
    @WrapMethod(method = "actuallyHurt")
    private void majruszsdifficulty$afterDamage(DamageSource source, float amount, Operation<Void> original) {
        Player player = (Player) (Object) this;
        float healthBefore = player.getHealth();
        boolean shouldPost = player.level() instanceof ServerLevel && !player.isInvulnerableTo(source);
        original.call(source, amount);
        if (shouldPost) {
            EventBus.post(new ServerLivingEntityDamagedEvent(source, player, Math.max(healthBefore - player.getHealth(), 0.0f)));
        }
    }

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
