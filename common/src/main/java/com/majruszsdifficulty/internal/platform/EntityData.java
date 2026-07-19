package com.majruszsdifficulty.internal.platform;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

public final class EntityData {
    private static final IEntityDataPlatform PLATFORM = Services.load(IEntityDataPlatform.class);

    public static @Nullable CompoundTag get(Entity entity) {
        return PLATFORM.get(entity);
    }

    public static CompoundTag getOrCreate(Entity entity) {
        return PLATFORM.getOrCreate(entity);
    }

    public static @Nullable CompoundTag set(Entity entity, CompoundTag data) {
        return PLATFORM.set(entity, data);
    }

    public static @Nullable CompoundTag remove(Entity entity) {
        return PLATFORM.remove(entity);
    }

    private EntityData() {
    }
}
