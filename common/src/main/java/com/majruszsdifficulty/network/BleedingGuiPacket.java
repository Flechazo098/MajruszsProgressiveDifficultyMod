package com.majruszsdifficulty.network;

import cc.sighs.oelib.network.api.INetworkContext;
import cc.sighs.oelib.network.api.INetworkPacket;
import cc.sighs.oelib.network.api.NetworkPacket;
import cc.sighs.oelib.network.api.Side;
import com.majruszsdifficulty.MajruszsDifficulty;
import com.majruszsdifficulty.effects.bleeding.BleedingGui;

@NetworkPacket(modId = MajruszsDifficulty.MOD_ID, id = "bleeding_gui", side = Side.CLIENT)
public record BleedingGuiPacket(int count) implements INetworkPacket<BleedingGuiPacket> {
    @Override
    public void handle(INetworkContext context) {
        context.enqueueWork(() -> BleedingGui.addBloodOnScreen(this.count));
    }
}
