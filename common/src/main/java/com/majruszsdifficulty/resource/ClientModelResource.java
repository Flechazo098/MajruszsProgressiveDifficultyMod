package com.majruszsdifficulty.resource;

import com.google.gson.JsonParser;
import com.majruszsdifficulty.internal.animations.ModelDef;
import com.mojang.serialization.JsonOps;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Supplier;

public final class ClientModelResource implements Supplier<ModelDef> {
    private final ResourceLocation file;
    private volatile ModelDef value;

    public ClientModelResource(ResourceLocation file) {
        this.file = file;
    }

    @Override
    public ModelDef get() {
        ModelDef current = this.value;
        if (current != null) {
            return current;
        }
        synchronized (this) {
            if (this.value == null) {
                this.value = this.load();
            }
            return this.value;
        }
    }

    private ModelDef load() {
        try (var reader = Minecraft.getInstance().getResourceManager().getResourceOrThrow(this.file).openAsReader()) {
            return ModelDef.CODEC.parse(JsonOps.INSTANCE, JsonParser.parseReader(reader))
                    .getOrThrow(message -> new IllegalArgumentException("Failed to decode client model " + this.file + ": " + message));
        } catch (Exception exception) {
            throw new IllegalStateException("Failed to load client model " + this.file, exception);
        }
    }
}
