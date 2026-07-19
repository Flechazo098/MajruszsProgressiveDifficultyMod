package com.majruszsdifficulty.treasurebag.listeners;

import cc.sighs.oelib.event.Subscribe;
import com.majruszsdifficulty.MajruszsDifficulty;
import com.majruszsdifficulty.treasurebag.events.OnTreasureBagOpened;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerPlayer;

public class AdvancementsController {
    @Subscribe
    private static void trigger(OnTreasureBagOpened data) {
        if (data.player() instanceof ServerPlayer player) {
            MajruszsDifficulty.triggerAdvancement(player, BuiltInRegistries.ITEM.getKey(data.treasureBag()).toString());
        }
    }
}
