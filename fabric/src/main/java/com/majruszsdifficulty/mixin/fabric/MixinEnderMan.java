package com.majruszsdifficulty.mixin.fabric;

import cc.sighs.oelib.event.EventBus;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.majruszsdifficulty.events.ServerEndermanAngerEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EnderMan.class)
public abstract class MixinEnderMan {
    @WrapOperation(
            method = "isLookingAtMe(Lnet/minecraft/world/entity/player/Player;)Z",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/item/ItemStack;is(Lnet/minecraft/world/item/Item;)Z"
            )
    )
    private boolean majruszsdifficulty$shouldSuppressAnger(ItemStack mask, Item item,
                                                           Operation<Boolean> original, Player player) {
        if (original.call(mask, item)) {
            return true;
        }
        EnderMan enderMan = (EnderMan) (Object) this;
        return enderMan.level() instanceof ServerLevel
                && EventBus.post(new ServerEndermanAngerEvent(enderMan, player));
    }
}
