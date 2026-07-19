package com.majruszsdifficulty.events;

import cc.sighs.oelib.event.Event;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public record ServerItemUseFinishedEvent(LivingEntity entity, ItemStack itemStack) implements Event {
}
