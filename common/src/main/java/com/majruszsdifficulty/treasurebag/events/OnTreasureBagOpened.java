package com.majruszsdifficulty.treasurebag.events;

import cc.sighs.oelib.event.Event;
import com.majruszsdifficulty.items.TreasureBag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public record OnTreasureBagOpened(Player player, TreasureBag treasureBag, List<ItemStack> loot) implements Event {
}
