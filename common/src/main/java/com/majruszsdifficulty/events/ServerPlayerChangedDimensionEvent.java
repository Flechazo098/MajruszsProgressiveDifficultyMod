package com.majruszsdifficulty.events;

import cc.sighs.oelib.event.Event;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

public record ServerPlayerChangedDimensionEvent(ServerPlayer player, ServerLevel current) implements Event {

    public boolean is(ResourceKey<Level> level) {
        return this.current.dimension().equals(level);
    }
}
