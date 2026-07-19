package com.majruszsdifficulty.resource;

import com.google.gson.JsonParser;
import com.majruszsdifficulty.MajruszsDifficulty;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;

import java.util.function.Supplier;

public final class ClientResource<Type> implements Supplier<Type> {
    private final ResourceLocation file;
    private final Codec<Type> codec;
    private volatile Type value;

    public ClientResource(ResourceLocation file, Codec<Type> codec) {
        this.file = file;
        this.codec = codec;
    }

    @Override
    public Type get() {
        Type current = this.value;
        if (current == null) {
            throw new IllegalStateException("Client resource has not been loaded: " + this.file);
        }
        return current;
    }

    public void reload(ResourceManager manager) {
        try (var reader = manager.getResourceOrThrow(this.file).openAsReader()) {
            this.value = this.codec.parse(JsonOps.INSTANCE, JsonParser.parseReader(reader))
                    .getOrThrow(message -> new IllegalArgumentException("Failed to decode client resource " + this.file + ": " + message));
        } catch (Exception exception) {
            MajruszsDifficulty.LOGGER.error("Failed to reload client resource {}", this.file, exception);
        }
    }
}
