package com.majruszsdifficulty.bloodmoon.events;

import cc.sighs.oelib.event.CancellableEvent;

public class OnBloodMoonStarted implements CancellableEvent {
    private boolean isCancelled = false;

    public OnBloodMoonStarted() {
    }

    @Override
    public boolean isCanceled() {
        return this.isCancelled;
    }

    @Override
    public void setCanceled(boolean canceled) {
        this.isCancelled = canceled;
    }
}
