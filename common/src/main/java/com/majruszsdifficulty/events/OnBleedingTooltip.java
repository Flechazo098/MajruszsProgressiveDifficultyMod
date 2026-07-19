package com.majruszsdifficulty.events;

import cc.sighs.oelib.event.Event;
import com.majruszsdifficulty.internal.math.Range;
import com.majruszsdifficulty.internal.text.TextHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

public class OnBleedingTooltip implements Event {
    public final ItemStack itemStack;
    public final int amplifier;
    private final ClientItemTooltipEvent data;

    public OnBleedingTooltip(ClientItemTooltipEvent data, int amplifier) {
        this.itemStack = data.itemStack();
        this.amplifier = amplifier;
        this.data = data;
    }

    public void addItem(double chance) {
        MutableComponent component = TextHelper.translatable("effect.majruszsdifficulty.bleeding.item_tooltip", TextHelper.percent(Range.CHANCE.clamp((float) chance)), TextHelper.toRoman(this.amplifier + 1))
                .withStyle(ChatFormatting.DARK_GREEN);

        this.data.add(component);
    }

    public void addArmor(EquipmentSlot slot, double chanceMultiplier) {
        MutableComponent component = TextHelper.translatable("effect.majruszsdifficulty.bleeding.armor_tooltip", TextHelper.minPrecision(chanceMultiplier, 3))
                .withStyle(ChatFormatting.BLUE);

        this.data.add(component);
    }
}
