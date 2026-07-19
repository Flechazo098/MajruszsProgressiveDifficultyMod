package com.majruszsdifficulty.mixin.fabric;

import cc.sighs.oelib.event.EventBus;
import com.majruszsdifficulty.events.ServerChorusFruitTeleportEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ChorusFruitItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChorusFruitItem.class)
public abstract class MixinChorusFruitItem {
    @Inject(
            method = "finishUsingItem",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/LivingEntity;randomTeleport(DDDZ)Z"
            ),
            cancellable = true
    )
    private void majruszsdifficulty$beforeTeleport(ItemStack stack, Level level, LivingEntity entity, CallbackInfoReturnable<ItemStack> callback) {
        if (EventBus.post(new ServerChorusFruitTeleportEvent(entity))) {
            callback.setReturnValue(stack);
        }
    }
}
