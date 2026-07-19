package com.majruszsdifficulty.registry;

import com.majruszsdifficulty.MajruszsDifficulty;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageType;

public final class ModDamageTypes {
    public static final ResourceKey<DamageType> BLEEDING_DAMAGE_SOURCE = ResourceKey.create(Registries.DAMAGE_TYPE, MajruszsDifficulty.id("bleeding"));

    private ModDamageTypes() {
    }
}
