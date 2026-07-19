package com.majruszsdifficulty.mixin;

import com.majruszsdifficulty.itemsets.EnderiumSet;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnchantmentHelper.class)
public abstract class MixinEnchantmentHelper {
    @Inject(method = "getEnchantmentLevel(Lnet/minecraft/core/Holder;Lnet/minecraft/world/entity/LivingEntity;)I", at = @At("RETURN"), cancellable = true)
    private static void majruszsdifficulty$enderiumLooting(Holder<Enchantment> enchantment, LivingEntity entity,
                                                           CallbackInfoReturnable<Integer> callback) {
        callback.setReturnValue(EnderiumSet.modifyLootingLevel(enchantment, entity, callback.getReturnValue()));
    }
}
