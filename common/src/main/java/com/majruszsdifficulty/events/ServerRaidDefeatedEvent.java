package com.majruszsdifficulty.events;

import cc.sighs.oelib.event.Event;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.raid.Raid;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public record ServerRaidDefeatedEvent(Raid raid, List<ServerPlayer> players) implements Event {
    public static ServerRaidDefeatedEvent create(Raid raid, Set<UUID> heroes) {
        return new ServerRaidDefeatedEvent(raid, heroes.stream()
                .map(raid.getLevel()::getPlayerByUUID)
                .filter(ServerPlayer.class::isInstance)
                .map(ServerPlayer.class::cast)
                .toList());
    }
}
