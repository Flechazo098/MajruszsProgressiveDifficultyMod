package com.majruszsdifficulty.mixin.fabric;

import cc.sighs.oelib.event.EventBus;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.majruszsdifficulty.events.PlayerBreakSpeedEvent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Player.class)
public abstract class MixinPlayerBreakSpeed {
    @ModifyReturnValue(method = "getDestroySpeed", at = @At("RETURN"))
    private float majruszsdifficulty$modifyBreakSpeed(float original, BlockState state) {
        PlayerBreakSpeedEvent event = new PlayerBreakSpeedEvent((Player) (Object) this, state, null, original);
        EventBus.post(event);
        return event.speed;
    }
}
