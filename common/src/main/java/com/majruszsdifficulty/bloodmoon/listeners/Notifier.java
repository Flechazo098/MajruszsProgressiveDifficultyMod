package com.majruszsdifficulty.bloodmoon.listeners;

import cc.sighs.oelib.event.Subscribe;
import com.majruszsdifficulty.bloodmoon.events.OnBloodMoonFinished;
import com.majruszsdifficulty.bloodmoon.events.OnBloodMoonStarted;
import com.majruszsdifficulty.internal.platform.Side;
import com.majruszsdifficulty.internal.text.TextHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

public class Notifier {
    @Subscribe
    private static void notifyStarted(OnBloodMoonStarted event) {
        sendMessage("majruszsdifficulty.blood_moon.started");
    }

    @Subscribe
    private static void notifyFinished(OnBloodMoonFinished event) {
        sendMessage("majruszsdifficulty.blood_moon.finished");
    }

    private static void sendMessage(String id) {
        if (Side.getServer() == null) {
            return;
        }
        Component message = TextHelper.translatable(id).withStyle(ChatFormatting.RED);

        Side.getServer()
                .getPlayerList()
                .getPlayers()
                .forEach(player -> player.sendSystemMessage(message));
    }
}
