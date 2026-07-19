package com.majruszsdifficulty.events;

import cc.sighs.oelib.event.Event;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;

public record ServerLivingEntityTickEvent(LivingEntity entity) implements Event {

	public ServerLevel getLevel() {
		return (ServerLevel) this.entity.level();
	}

	public ServerLevel getServerLevel() {
		return this.getLevel();
	}
}
