package com.majruszsdifficulty.events;

import cc.sighs.oelib.event.Event;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public final class PlayerBreakSpeedEvent implements Event {
    public final Player player;
    public final BlockState blockState;
    public final @Nullable BlockPos position;
    public final float original;
    public float speed;

    public PlayerBreakSpeedEvent(Player player, BlockState state, @Nullable BlockPos position, float speed) {
        this.player = player;
        this.blockState = state;
        this.position = position;
        this.original = speed;
        this.speed = speed;
    }
}
