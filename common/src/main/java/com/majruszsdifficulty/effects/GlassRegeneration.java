package com.majruszsdifficulty.effects;

import cc.sighs.oelib.event.Subscribe;
import com.majruszsdifficulty.MajruszsDifficulty;
import com.majruszsdifficulty.events.ServerLivingEntityDamagedEvent;
import com.majruszsdifficulty.internal.emitter.SoundEmitter;
import com.majruszsdifficulty.internal.entity.EffectHelper;
import com.majruszsdifficulty.internal.time.TimeHelper;
import com.majruszsdifficulty.registry.ModEffects;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

public class GlassRegeneration extends MobEffect {
    private static final SoundEmitter GLASS_BREAK = SoundEmitter.of(SoundEvents.GLASS_BREAK)
            .source(SoundSource.PLAYERS)
            .volume(SoundEmitter.randomized(0.25f));

    public GlassRegeneration() {
        super(MobEffectCategory.BENEFICIAL, 0xffcd5cab);
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        if (entity.getHealth() < entity.getMaxHealth()) {
            entity.heal(1.0f);
        }

        return true;
    }

    @Override
    public void applyInstantenousEffect(@Nullable Entity source, @Nullable Entity indirectSource, LivingEntity entity, int amplifier, double health) {
        this.applyEffectTick(entity, amplifier);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        int cooldown = TimeHelper.toTicks(8.0) >> amplifier;

        return cooldown <= 0 || duration % cooldown == 0;
    }

    @Subscribe
    private static void removeOnHit(ServerLivingEntityDamagedEvent data) {
        if (EffectHelper.has(ModEffects.GLASS_REGENERATION_EFFECT, data.target)) {
            data.target.removeEffect(MajruszsDifficulty.effectHolder(ModEffects.GLASS_REGENERATION_EFFECT));
            GLASS_BREAK.position(data.target.position()).emit(data.getServerLevel());
        }
    }
}
