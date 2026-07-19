package com.majruszsdifficulty.events;

import cc.sighs.oelib.event.Event;
import net.minecraft.server.MinecraftServer;

public record ServerStartedEvent(MinecraftServer server) implements Event {
}
