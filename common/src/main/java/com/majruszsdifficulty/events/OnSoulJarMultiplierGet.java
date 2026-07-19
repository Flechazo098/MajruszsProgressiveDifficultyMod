package com.majruszsdifficulty.events;

import cc.sighs.oelib.event.Event;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class OnSoulJarMultiplierGet implements Event {
    public final LivingEntity entity;
    public final ItemStack itemStack;
    public float multiplier = 1.0f;

    public OnSoulJarMultiplierGet(LivingEntity entity, ItemStack itemStack) {
        this.entity = entity;
        this.itemStack = itemStack;
    }

    public float getMultiplier() {
        return Math.max(this.multiplier, 0.0f);
    }
}
