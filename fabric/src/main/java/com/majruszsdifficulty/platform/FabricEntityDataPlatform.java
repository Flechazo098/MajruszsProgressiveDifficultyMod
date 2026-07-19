package com.majruszsdifficulty.platform;

import com.majruszsdifficulty.MajruszsDifficulty;
import com.majruszsdifficulty.internal.platform.IEntityDataPlatform;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentTarget;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

public final class FabricEntityDataPlatform implements IEntityDataPlatform {
    private static AttachmentType<CompoundTag> entityData;

    public static void registerAttachments() {
        if (entityData != null) {
            return;
        }

        entityData = AttachmentRegistry.create(
                ResourceLocation.fromNamespaceAndPath(MajruszsDifficulty.MOD_ID, "entity_data"),
                builder -> builder.initializer(CompoundTag::new).persistent(CompoundTag.CODEC)
        );
    }

    @Override
    public @Nullable CompoundTag get(Entity entity) {
        return target(entity).getAttached(this.type());
    }

    @Override
    public CompoundTag getOrCreate(Entity entity) {
        return target(entity).getAttachedOrCreate(this.type());
    }

    @Override
    public @Nullable CompoundTag set(Entity entity, CompoundTag data) {
        return target(entity).setAttached(this.type(), data);
    }

    @Override
    public @Nullable CompoundTag remove(Entity entity) {
        return target(entity).setAttached(this.type(), null);
    }

    private AttachmentType<CompoundTag> type() {
        if (entityData == null) {
            throw new IllegalStateException("Entity data attachment has not been registered");
        }
        return entityData;
    }

    private static AttachmentTarget target(Entity entity) {
        return entity;
    }
}
