package com.majruszsdifficulty.mixin;

import cc.sighs.oelib.event.EventBus;
import com.majruszsdifficulty.events.ServerRaidDefeatedEvent;
import net.minecraft.world.entity.raid.Raid;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;
import java.util.UUID;

@Mixin(Raid.class)
public abstract class MixinRaid {
    @Shadow
    @Final
    private Set<UUID> heroesOfTheVillage;

    @Shadow
    public abstract boolean isVictory();

    @Unique
    private boolean majruszsdifficulty$victoryPosted;

    @Inject(method = "tick", at = @At("TAIL"))
    private void majruszsdifficulty$afterRaidTick(CallbackInfo callback) {
        if (this.isVictory() && !this.majruszsdifficulty$victoryPosted) {
            this.majruszsdifficulty$victoryPosted = true;
            Raid raid = (Raid) (Object) this;
            EventBus.post(ServerRaidDefeatedEvent.create(raid, this.heroesOfTheVillage));
        }
    }
}
