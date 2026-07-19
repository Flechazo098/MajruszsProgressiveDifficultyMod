package com.majruszsdifficulty.events;

import cc.sighs.oelib.event.CancellableEvent;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.player.Player;

public final class ServerEndermanAngerEvent implements CancellableEvent {
    public final EnderMan enderMan;
    public final Player player;
    private boolean canceled;

    public ServerEndermanAngerEvent(EnderMan enderMan, Player player) {
        this.enderMan = enderMan;
        this.player = player;
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
