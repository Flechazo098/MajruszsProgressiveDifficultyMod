package com.majruszsdifficulty.mixin.fabric;

import cc.sighs.oelib.event.EventBus;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.majruszsdifficulty.events.ServerItemUseFinishedEvent;
import com.majruszsdifficulty.events.ServerLivingEntityDamagedEvent;
import com.majruszsdifficulty.events.ServerLivingEntityIncomingDamageEvent;
import com.majruszsdifficulty.events.ServerMobEffectApplicableEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity {
    @Unique
    private ItemStack majruszsdifficulty$usedItem = ItemStack.EMPTY;

    @Inject(method = "completeUsingItem", at = @At("HEAD"))
    private void majruszsdifficulty$beforeUseFinished(CallbackInfo callback) {
        this.majruszsdifficulty$usedItem = ((LivingEntity) (Object) this).getUseItem().copy();
    }

    @Inject(method = "completeUsingItem", at = @At("TAIL"))
    private void majruszsdifficulty$afterUseFinished(CallbackInfo callback) {
        LivingEntity entity = (LivingEntity) (Object) this;
        if (entity.level() instanceof ServerLevel && !this.majruszsdifficulty$usedItem.isEmpty()) {
            EventBus.post(new ServerItemUseFinishedEvent(entity, this.majruszsdifficulty$usedItem));
        }
        this.majruszsdifficulty$usedItem = ItemStack.EMPTY;
    }

    @Inject(method = "canBeAffected", at = @At("HEAD"), cancellable = true)
    private void majruszsdifficulty$checkEffect(MobEffectInstance effect, CallbackInfoReturnable<Boolean> callback) {
        LivingEntity entity = (LivingEntity) (Object) this;
        if (entity.level() instanceof ServerLevel && EventBus.post(new ServerMobEffectApplicableEvent(effect, entity))) {
            callback.setReturnValue(false);
        }
    }

    @WrapMethod(method = "actuallyHurt")
    private void majruszsdifficulty$wrapFatalDamage(DamageSource source, float amount, Operation<Void> original) {
        LivingEntity entity = (LivingEntity) (Object) this;
        float healthBefore = entity.getHealth();
        original.call(source, amount);
        if (healthBefore > 0.0f && entity.isDeadOrDying()) {
            EventBus.post(new ServerLivingEntityDamagedEvent(source, entity, Math.max(healthBefore - entity.getHealth(), 0.0f)));
        }
    }

    @WrapMethod(method = "hurt")
    private boolean majruszsdifficulty$wrapHurt(DamageSource source, float amount, Operation<Boolean> original) {
        LivingEntity entity = (LivingEntity) (Object) this;
        if (entity.isInvulnerableTo(source)
                || entity.level().isClientSide
                || entity.isDeadOrDying()
                || (source.is(DamageTypeTags.IS_FIRE) && entity.hasEffect(MobEffects.FIRE_RESISTANCE))) {
            return original.call(source, amount);
        }

        ServerLivingEntityIncomingDamageEvent event = new ServerLivingEntityIncomingDamageEvent(source, entity, amount);
        EventBus.post(event);
        if (event.isDamageCancelled()) {
            entity.invulnerableTime = 20;
            return false;
        }
        if (event.attacker instanceof Player player) {
            if (event.spawnCriticalParticles) {
                player.crit(entity);
            }
            if (event.spawnMagicParticles) {
                player.magicCrit(entity);
            }
        }
        return original.call(source, event.damage);
    }
}
