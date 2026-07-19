package com.majruszsdifficulty.effects.bleeding;

import cc.sighs.oelib.event.Subscribe;
import com.majruszsdifficulty.MajruszsDifficulty;
import com.majruszsdifficulty.events.ClientItemTooltipEvent;
import com.majruszsdifficulty.events.ServerItemUseFinishedEvent;
import com.majruszsdifficulty.internal.text.TextHelper;
import com.majruszsdifficulty.registry.ModEffects;
import net.minecraft.ChatFormatting;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import java.util.List;

public class BleedingAppleProtection {
    private static final List<Item> ITEMS = List.of(Items.GOLDEN_APPLE, Items.ENCHANTED_GOLDEN_APPLE);

    @Subscribe
    private static void cancelBleeding(ServerItemUseFinishedEvent data) {
        if (BleedingConfig.get().canBeCuredWithGoldenApples() && ITEMS.contains(data.itemStack().getItem())) {
            data.entity().removeEffect(MajruszsDifficulty.effectHolder(ModEffects.BLEEDING_EFFECT));
        }
    }

    @Subscribe
    private static void addTooltip(ClientItemTooltipEvent data) {
        if (!data.isAdvanced() || !BleedingConfig.get().canBeCuredWithGoldenApples() || !ITEMS.contains(data.itemStack().getItem())) {
            return;
        }
        data.add(TextHelper.empty());
        data.add(TextHelper.translatable("effect.majruszsdifficulty.bleeding.item_consumed").withStyle(ChatFormatting.DARK_PURPLE));
        data.add(TextHelper.translatable("item.majruszsdifficulty.bandage.effect").withStyle(ChatFormatting.BLUE));
    }
}
