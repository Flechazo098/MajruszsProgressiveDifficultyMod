package com.majruszsdifficulty.platform;

import com.majruszsdifficulty.MajruszsDifficulty;
import com.majruszsdifficulty.internal.platform.IEntityDataPlatform;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public final class NeoForgeEntityDataPlatform implements IEntityDataPlatform {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENTS = DeferredRegister.create(
            NeoForgeRegistries.Keys.ATTACHMENT_TYPES,
            MajruszsDifficulty.MOD_ID
    );
    private static final Supplier<AttachmentType<CompoundTag>> ENTITY_DATA = ATTACHMENTS.register(
            "entity_data",
            () -> AttachmentType.builder(() -> new CompoundTag())
                    .serialize(CompoundTag.CODEC, tag -> !tag.isEmpty())
                    .build()
    );

    @Override
    public @Nullable CompoundTag get(Entity entity) {
        return entity.getExistingDataOrNull(ENTITY_DATA);
    }

    @Override
    public CompoundTag getOrCreate(Entity entity) {
        return entity.getData(ENTITY_DATA);
    }

    @Override
    public @Nullable CompoundTag set(Entity entity, CompoundTag data) {
        return entity.setData(ENTITY_DATA, data);
    }

    @Override
    public @Nullable CompoundTag remove(Entity entity) {
        return entity.removeData(ENTITY_DATA);
    }
}
