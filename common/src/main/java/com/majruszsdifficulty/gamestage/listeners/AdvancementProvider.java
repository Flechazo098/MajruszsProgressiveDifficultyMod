package com.majruszsdifficulty.gamestage.listeners;

import cc.sighs.oelib.event.Subscribe;
import com.majruszsdifficulty.events.ServerPlayerJoinedEvent;
import com.majruszsdifficulty.gamestage.GameStageHelper;
import com.majruszsdifficulty.gamestage.contexts.OnGlobalGameStageChanged;
import com.majruszsdifficulty.gamestage.contexts.OnPlayerGameStageChanged;
import com.majruszsdifficulty.internal.platform.Side;
import com.majruszsdifficulty.registry.ModAdvancements;
import net.minecraft.server.level.ServerPlayer;

public class AdvancementProvider {
    @Subscribe
    private static void giveAdvancement(OnGlobalGameStageChanged data) {
        if (GameStageHelper.isPerPlayerDifficultyEnabled()
                || data.current().getOrdinal() <= data.previous().getOrdinal()
                || Side.getServer() == null) {
            return;
        }
        Side.getServer()
                .getPlayerList()
                .getPlayers()
                .forEach(player -> ModAdvancements.GAME_STAGE_ADVANCEMENT.trigger(player, data.current()));
    }

    @Subscribe
    private static void giveAdvancement(OnPlayerGameStageChanged data) {
        if (data.current().getOrdinal() > data.previous().getOrdinal() && data.player() instanceof ServerPlayer player) {
            ModAdvancements.GAME_STAGE_ADVANCEMENT.trigger(player, data.current());
        }
    }

    @Subscribe
    private static void giveAdvancement(ServerPlayerJoinedEvent data) {
        ModAdvancements.GAME_STAGE_ADVANCEMENT.trigger(data.player(), GameStageHelper.determineGameStage(data.player()));
    }
}
