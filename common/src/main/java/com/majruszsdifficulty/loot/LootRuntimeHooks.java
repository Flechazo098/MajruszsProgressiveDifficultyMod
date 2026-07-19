package com.majruszsdifficulty.loot;

import cc.sighs.oelib.event.EventBus;
import com.majruszsdifficulty.events.ServerLootGeneratedEvent;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;

public final class LootRuntimeHooks {
    public static ObjectArrayList<ItemStack> modify(ResourceLocation id, ObjectArrayList<ItemStack> loot, LootContext context) {
        EventBus.post(new ServerLootGeneratedEvent(loot, id, context));
        return loot;
    }

    private LootRuntimeHooks() {
    }
}
