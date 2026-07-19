package com.majruszsdifficulty.events;

import cc.sighs.oelib.event.Event;
import net.minecraft.server.level.ServerPlayer;

public record ServerPlayerJoinedEvent(ServerPlayer player) implements Event {
}
