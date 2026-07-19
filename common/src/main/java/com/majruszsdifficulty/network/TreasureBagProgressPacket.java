package com.majruszsdifficulty.network;

import cc.sighs.oelib.network.api.INetworkContext;
import cc.sighs.oelib.network.api.INetworkPacket;
import cc.sighs.oelib.network.api.NetworkPacket;
import cc.sighs.oelib.network.api.Side;
import com.majruszsdifficulty.MajruszsDifficulty;
import com.majruszsdifficulty.treasurebag.TreasureBagClient;
import com.majruszsdifficulty.treasurebag.TreasureBagHelper;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

@NetworkPacket(modId = MajruszsDifficulty.MOD_ID, id = "treasure_bag_progress", side = Side.CLIENT)
public record TreasureBagProgressPacket(
        ResourceLocation id,
        List<TreasureBagHelper.ItemProgressData> items,
        List<Integer> unlockedIndices
) implements INetworkPacket<TreasureBagProgressPacket> {
    @Override
    public void handle(INetworkContext context) {
        context.enqueueWork(() -> TreasureBagClient.onProgressReceived(this));
    }
}
