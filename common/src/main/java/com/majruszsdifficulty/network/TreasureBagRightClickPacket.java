package com.majruszsdifficulty.network;

import cc.sighs.oelib.network.api.INetworkContext;
import cc.sighs.oelib.network.api.INetworkPacket;
import cc.sighs.oelib.network.api.NetworkPacket;
import cc.sighs.oelib.network.api.Side;
import com.majruszsdifficulty.MajruszsDifficulty;
import com.majruszsdifficulty.items.TreasureBag;
import net.minecraft.server.level.ServerPlayer;

@NetworkPacket(modId = MajruszsDifficulty.MOD_ID, id = "treasure_bag_right_click", side = Side.SERVER)
public record TreasureBagRightClickPacket(int containerIndex) implements INetworkPacket<TreasureBagRightClickPacket> {
    @Override
    public void handle(INetworkContext context) {
        context.enqueueWork(() -> {
            ServerPlayer player = context.sender();
            if (player != null) {
                TreasureBag.openInInventory(this.containerIndex, player);
            }
        });
    }
}
