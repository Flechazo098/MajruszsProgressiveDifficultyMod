package com.majruszsdifficulty.events;

import cc.sighs.oelib.event.Event;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Platform-neutral view of Fabric's ItemTooltipCallback and NeoForge's ItemTooltipEvent.
 */
public record ClientItemTooltipEvent(
        ItemStack itemStack,
        Item.TooltipContext context,
        TooltipFlag flags,
        List<Component> components,
        @Nullable Player player
) implements Event {
    public boolean isAdvanced() {
        return this.flags.isAdvanced();
    }

    public void add(Component component) {
        this.components.add(component);
    }
}
