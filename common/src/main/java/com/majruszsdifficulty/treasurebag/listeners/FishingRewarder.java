package com.majruszsdifficulty.treasurebag.listeners;

import cc.sighs.oelib.event.Subscribe;
import com.majruszsdifficulty.events.ServerItemFishedEvent;
import com.majruszsdifficulty.internal.entity.EntityHelper;
import com.majruszsdifficulty.internal.item.ItemHelper;
import com.majruszsdifficulty.registry.ModItems;
import net.minecraft.world.item.ItemStack;

public class FishingRewarder {
    public static int FISH_REQUIREMENT = 16;

    @Subscribe
    private static void updateItemsFished(ServerItemFishedEvent data) {
        var tag = EntityHelper.getOrCreateExtraTag(data.player());
        int fishesLeft = tag.getInt("TreasureBagFishesLeft");
        if (fishesLeft <= 0) {
            fishesLeft = FISH_REQUIREMENT;
        }
        --fishesLeft;
        tag.putInt("TreasureBagFishesLeft", fishesLeft);
        if (fishesLeft == 0) {
            ItemHelper.giveToPlayer(new ItemStack(ModItems.ANGLER_TREASURE_BAG_ITEM.get()), data.player());
        }
    }
}
