package com.majruszsdifficulty.events;

import cc.sighs.oelib.event.Event;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public record ServerItemFishedEvent(ServerPlayer player, FishingHook hook, ItemStack fishingRod,
                                    List<ItemStack> items) implements Event {
}
