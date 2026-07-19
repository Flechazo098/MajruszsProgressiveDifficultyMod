package com.majruszsdifficulty.network;

import cc.sighs.oelib.network.api.INetworkContext;
import cc.sighs.oelib.network.api.INetworkPacket;
import cc.sighs.oelib.network.api.NetworkPacket;
import cc.sighs.oelib.network.api.Side;
import com.majruszsdifficulty.MajruszsDifficulty;
import com.majruszsdifficulty.bloodmoon.BloodMoonHelper;
import com.majruszsdifficulty.gamestage.GameStageHelper;

@NetworkPacket(modId = MajruszsDifficulty.MOD_ID, id = "client_state", side = Side.CLIENT)
public record ClientStateSyncPacket(boolean bloodMoonActive, String globalGameStage, String playerGameStage)
        implements INetworkPacket<ClientStateSyncPacket> {
    @Override
    public void handle(INetworkContext context) {
        context.enqueueWork(() -> {
            BloodMoonHelper.load(this.bloodMoonActive);
            GameStageHelper.applyClientState(this.globalGameStage, this.playerGameStage);
        });
    }
}
