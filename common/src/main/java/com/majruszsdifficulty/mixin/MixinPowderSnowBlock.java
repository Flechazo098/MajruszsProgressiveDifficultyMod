package com.majruszsdifficulty.mixin;

import com.majruszsdifficulty.registry.ModItems;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.PowderSnowBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PowderSnowBlock.class)
public abstract class MixinPowderSnowBlock {
    @Inject(method = "canEntityWalkOnPowderSnow", at = @At("HEAD"), cancellable = true)
    private static void majruszsdifficulty$tatteredBootsWalkOnPowderSnow(Entity entity, CallbackInfoReturnable<Boolean> callback) {
        if (entity instanceof LivingEntity living && living.getItemBySlot(EquipmentSlot.FEET).is(ModItems.TATTERED_BOOTS_ITEM.get())) {
            callback.setReturnValue(true);
        }
    }
}
