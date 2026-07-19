package com.majruszsdifficulty.gamestage.contexts;

import cc.sighs.oelib.event.Event;
import com.majruszsdifficulty.gamestage.GameStage;


public record OnGlobalGameStageChanged(GameStage previous, GameStage current) implements Event {
}
