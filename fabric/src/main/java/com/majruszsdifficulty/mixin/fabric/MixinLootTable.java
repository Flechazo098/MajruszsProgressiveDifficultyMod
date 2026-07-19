package com.majruszsdifficulty.mixin.fabric;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.majruszsdifficulty.loot.LootRuntimeHooks;
import com.majruszsdifficulty.platform.FabricLootTableIds;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LootTable.class)
public abstract class MixinLootTable {
    @ModifyReturnValue(method = "getRandomItems(Lnet/minecraft/world/level/storage/loot/LootContext;)Lit/unimi/dsi/fastutil/objects/ObjectArrayList;", at = @At("RETURN"))
    private ObjectArrayList<ItemStack> majruszsdifficulty$modifyGeneratedLoot(ObjectArrayList<ItemStack> loot, LootContext context) {
        LootTable table = (LootTable) (Object) this;
        return LootRuntimeHooks.modify(FabricLootTableIds.get(table), loot, context);
    }
}
