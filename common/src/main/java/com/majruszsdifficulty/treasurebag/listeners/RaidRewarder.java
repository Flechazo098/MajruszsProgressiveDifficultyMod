package com.majruszsdifficulty.treasurebag.listeners;

import cc.sighs.oelib.event.Subscribe;
import com.majruszsdifficulty.events.ServerRaidDefeatedEvent;
import com.majruszsdifficulty.internal.item.ItemHelper;
import com.majruszsdifficulty.registry.ModItems;
import com.majruszsdifficulty.undeadarmy.events.OnUndeadArmyDefeated;
import net.minecraft.world.item.ItemStack;

public class RaidRewarder {
    @Subscribe
    private static void giveTreasureBags(ServerRaidDefeatedEvent data) {
        data.players().forEach(player -> ItemHelper.giveToPlayer(new ItemStack(ModItems.PILLAGER_TREASURE_BAG_ITEM.get()), player));
    }

    @Subscribe
    private static void giveTreasureBags(OnUndeadArmyDefeated data) {
        data.undeadArmy.participants.forEach(player -> ItemHelper.giveToPlayer(new ItemStack(ModItems.UNDEAD_ARMY_TREASURE_BAG_ITEM.get()), player));
    }
}
