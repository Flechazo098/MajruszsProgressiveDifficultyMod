package com.majruszsdifficulty.undeadarmy.events;

import cc.sighs.oelib.event.Event;
import com.majruszsdifficulty.undeadarmy.UndeadArmy;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

public class UndeadArmyEvent implements Event {
    public final UndeadArmy undeadArmy;

    public UndeadArmyEvent(UndeadArmy undeadArmy) {
        this.undeadArmy = undeadArmy;
    }

    public Level getLevel() {
        return this.undeadArmy.getLevel();
    }

    public ServerLevel getServerLevel() {
        return this.undeadArmy.getLevel();
    }
}
