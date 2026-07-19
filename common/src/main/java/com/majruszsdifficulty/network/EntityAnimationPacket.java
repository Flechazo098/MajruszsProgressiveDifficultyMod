package com.majruszsdifficulty.network;

import cc.sighs.oelib.network.api.INetworkContext;
import cc.sighs.oelib.network.api.INetworkPacket;
import cc.sighs.oelib.network.api.NetworkPacket;
import cc.sighs.oelib.network.api.Side;
import com.majruszsdifficulty.MajruszsDifficulty;
import com.majruszsdifficulty.internal.animations.IAnimableEntity;
import com.majruszsdifficulty.internal.entity.EntityHelper;

@NetworkPacket(modId = MajruszsDifficulty.MOD_ID, id = "entity_animation", side = Side.CLIENT)
public record EntityAnimationPacket(int entityId, String name,
                                    int trackIndex) implements INetworkPacket<EntityAnimationPacket> {
    @Override
    public void handle(INetworkContext context) {
        context.enqueueWork(() -> EntityHelper.applyToClientEntity(
                this.entityId,
                IAnimableEntity.class,
                entity -> entity.playAnimation(this.name, this.trackIndex)
        ));
    }
}