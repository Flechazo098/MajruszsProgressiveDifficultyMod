package com.majruszsdifficulty.gamestage.contexts;

import cc.sighs.oelib.event.Event;
import com.majruszsdifficulty.gamestage.GameStage;
import net.minecraft.world.entity.player.Player;


public record OnPlayerGameStageChanged(GameStage previous, GameStage current, Player player) implements Event {
}
