package com.majruszsdifficulty.internal.platform;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

public interface IEntityDataPlatform {
    @Nullable CompoundTag get(Entity entity);

    CompoundTag getOrCreate(Entity entity);

    @Nullable CompoundTag set(Entity entity, CompoundTag data);

    @Nullable CompoundTag remove(Entity entity);
}
