package com.majruszsdifficulty.gamestage.listeners;

import cc.sighs.oelib.event.Subscribe;
import com.majruszsdifficulty.gamestage.GameStage;
import com.majruszsdifficulty.gamestage.GameStageHelper;
import com.majruszsdifficulty.gamestage.contexts.OnGlobalGameStageChanged;
import com.majruszsdifficulty.gamestage.contexts.OnPlayerGameStageChanged;
import com.majruszsdifficulty.internal.platform.Side;
import net.minecraft.world.entity.player.Player;

public class Notifier {
    @Subscribe
    private static void notify(OnGlobalGameStageChanged data) {
        if (GameStageHelper.isPerPlayerDifficultyEnabled()
                || data.current().getOrdinal() <= data.previous().getOrdinal()
                || Side.getServer() == null) {
            return;
        }
        Side.getServer()
                .getPlayerList()
                .getPlayers()
                .forEach(player -> Notifier.send(player, data.current()));
    }

    @Subscribe
    private static void notify(OnPlayerGameStageChanged data) {
        if (data.current().getOrdinal() > data.previous().getOrdinal()) {
            Notifier.send(data.player(), data.current());
        }
    }

    private static void send(Player player, GameStage gameStage) {
        gameStage.getMessages().forEach(message -> player.displayClientMessage(message, false));
    }
}
