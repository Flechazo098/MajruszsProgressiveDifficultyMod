package com.majruszsdifficulty.mixin.fabric;

import cc.sighs.oelib.event.EventBus;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.majruszsdifficulty.events.ServerLivingEntityDeathEvent;
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

@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity {
    @Unique
    private ItemStack majruszsdifficulty$usedItem = ItemStack.EMPTY;

    @Inject(method = "completeUsingItem", at = @At("HEAD"))
    private void majruszsdifficulty$beforeUseFinished(CallbackInfo callback) {
        this.majruszsdifficulty$usedItem = ((LivingEntity) (Object) this).getUseItem().copy();
    }

    @ModifyExpressionValue(
            method = "completeUsingItem",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/item/ItemStack;finishUsingItem(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/LivingEntity;)Lnet/minecraft/world/item/ItemStack;"
            )
    )
    private ItemStack majruszsdifficulty$onUseFinished(ItemStack result) {
        LivingEntity entity = (LivingEntity) (Object) this;
        if (entity.level() instanceof ServerLevel && !this.majruszsdifficulty$usedItem.isEmpty()) {
            EventBus.post(new ServerItemUseFinishedEvent(entity, this.majruszsdifficulty$usedItem));
        }
        this.majruszsdifficulty$usedItem = ItemStack.EMPTY;
        return result;
    }

    @WrapOperation(
            method = {
                    "addEffect(Lnet/minecraft/world/effect/MobEffectInstance;Lnet/minecraft/world/entity/Entity;)Z",
                    "forceAddEffect(Lnet/minecraft/world/effect/MobEffectInstance;Lnet/minecraft/world/entity/Entity;)V"
            },
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/LivingEntity;canBeAffected(Lnet/minecraft/world/effect/MobEffectInstance;)Z"
            )
    )
    private boolean majruszsdifficulty$checkEffect(LivingEntity entity, MobEffectInstance effect,
                                                    Operation<Boolean> original) {
        if (entity.level() instanceof ServerLevel
                && EventBus.post(new ServerMobEffectApplicableEvent(effect, entity))) {
            return false;
        }
        return original.call(entity, effect);
    }

    @WrapMethod(method = "actuallyHurt")
    private void majruszsdifficulty$afterDamage(DamageSource source, float amount, Operation<Void> original) {
        LivingEntity entity = (LivingEntity) (Object) this;
        float healthBefore = entity.getHealth();
        boolean shouldPost = entity.level() instanceof ServerLevel && !entity.isInvulnerableTo(source);
        original.call(source, amount);
        if (shouldPost) {
            EventBus.post(new ServerLivingEntityDamagedEvent(source, entity, Math.max(healthBefore - entity.getHealth(), 0.0f)));
        }
    }

    @Inject(method = "die(Lnet/minecraft/world/damagesource/DamageSource;)V", at = @At("HEAD"), cancellable = true)
    private void majruszsdifficulty$beforeDeath(DamageSource source, CallbackInfo callback) {
        LivingEntity entity = (LivingEntity) (Object) this;
        if (entity.level() instanceof ServerLevel
                && EventBus.post(new ServerLivingEntityDeathEvent(source, entity))) {
            callback.cancel();
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
        if (event.attacker instanceof Player player) {
            if (event.spawnCriticalParticles) {
                player.crit(entity);
            }
            if (event.spawnMagicParticles) {
                player.magicCrit(entity);
            }
        }
        if (event.isDamageCancelled()) {
            return false;
        }
        return original.call(source, event.damage);
    }
}
