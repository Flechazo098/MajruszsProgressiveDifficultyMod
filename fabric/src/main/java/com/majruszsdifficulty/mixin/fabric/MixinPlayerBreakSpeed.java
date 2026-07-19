package com.majruszsdifficulty.mixin.fabric;

import cc.sighs.oelib.event.EventBus;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.majruszsdifficulty.events.PlayerBreakSpeedEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BlockBehaviour.class)
public abstract class MixinPlayerBreakSpeed {
    @ModifyExpressionValue(
            method = "getDestroyProgress",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/player/Player;getDestroySpeed(Lnet/minecraft/world/level/block/state/BlockState;)F"
            )
    )
    private float majruszsdifficulty$modifyBreakSpeed(float original, BlockState state, Player player,
                                                       BlockGetter level, BlockPos position) {
        PlayerBreakSpeedEvent event = new PlayerBreakSpeedEvent(player, state, position, original);
        EventBus.post(event);
        return event.speed;
    }
}
