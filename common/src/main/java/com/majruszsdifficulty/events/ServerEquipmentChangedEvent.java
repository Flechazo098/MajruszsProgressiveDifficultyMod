package com.majruszsdifficulty.events;

import cc.sighs.oelib.event.Event;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public record ServerEquipmentChangedEvent(
        LivingEntity entity,
        EquipmentSlot slot,
        ItemStack from,
        ItemStack to
) implements Event {
}
