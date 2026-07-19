package com.majruszsdifficulty.events;

import cc.sighs.oelib.event.CancellableEvent;
import net.minecraft.server.level.ServerPlayer;

public final class ServerPlayerExperienceChangeEvent implements CancellableEvent {
    public final ServerPlayer player;
    public final int original;
    public int amount;
    private boolean canceled;

    public ServerPlayerExperienceChangeEvent(ServerPlayer player, int amount) {
        this.player = player;
        this.original = amount;
        this.amount = amount;
    }

    @Override
    public boolean isCanceled() {
        return this.canceled;
    }

    @Override
    public void setCanceled(boolean canceled) {
        this.canceled = canceled;
    }
}
