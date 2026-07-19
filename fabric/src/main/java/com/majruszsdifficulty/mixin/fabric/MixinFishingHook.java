package com.majruszsdifficulty.mixin.fabric;

import cc.sighs.oelib.event.EventBus;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.majruszsdifficulty.events.ServerItemFishedEvent;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(FishingHook.class)
public abstract class MixinFishingHook {
    @ModifyExpressionValue(
            method = "retrieve",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/storage/loot/LootTable;getRandomItems(Lnet/minecraft/world/level/storage/loot/LootParams;)Lit/unimi/dsi/fastutil/objects/ObjectArrayList;")
    )
    private ObjectArrayList<ItemStack> majruszsdifficulty$itemFished(
            ObjectArrayList<ItemStack> items, ItemStack rod
    ) {
        FishingHook hook = (FishingHook) (Object) this;
        if (hook.getPlayerOwner() instanceof ServerPlayer player) {
            EventBus.post(new ServerItemFishedEvent(player, hook, rod, items));
        }
        return items;
    }
}
