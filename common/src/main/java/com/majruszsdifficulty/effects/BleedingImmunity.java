package com.majruszsdifficulty.effects;

import cc.sighs.oelib.event.Subscribe;
import com.majruszsdifficulty.MajruszsDifficulty;
import com.majruszsdifficulty.events.ServerMobEffectApplicableEvent;
import com.majruszsdifficulty.internal.entity.EffectHelper;
import com.majruszsdifficulty.registry.ModEffects;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

public class BleedingImmunity extends MobEffect {
    @Subscribe
    private static void blockBleeding(ServerMobEffectApplicableEvent data) {
        if (EffectHelper.has(ModEffects.BLEEDING_IMMUNITY_EFFECT, data.entity)
                && data.effect.equals(MajruszsDifficulty.effectHolder(ModEffects.BLEEDING_EFFECT))) {
            data.cancelEffect();
        }
    }

    public BleedingImmunity() {
        super(MobEffectCategory.BENEFICIAL, 0xff990000);
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        return true;
    }

    @Override
    public void applyInstantenousEffect(@Nullable Entity source, @Nullable Entity indirectSource, LivingEntity entity, int amplifier, double health) {
        entity.removeEffect(MajruszsDifficulty.effectHolder(ModEffects.BLEEDING_EFFECT));
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return false;
    }
}
