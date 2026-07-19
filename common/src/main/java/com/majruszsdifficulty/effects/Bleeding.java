package com.majruszsdifficulty.effects;

import com.majruszsdifficulty.MajruszsDifficulty;
import com.majruszsdifficulty.effects.bleeding.BleedingConfig;
import com.majruszsdifficulty.gamestage.GameStage;
import com.majruszsdifficulty.gamestage.GameStageHelper;
import com.majruszsdifficulty.gamestage.GameStageValue;
import com.majruszsdifficulty.internal.entity.EffectDef;
import com.majruszsdifficulty.internal.time.TimeHelper;
import com.majruszsdifficulty.registry.ModEffects;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import org.jetbrains.annotations.Nullable;

public class Bleeding extends MobEffect {
    public static boolean apply(LivingEntity target, @Nullable LivingEntity attacker) {
        EffectDef effectDef = Bleeding.getCurrentEffect(GameStageHelper.determineGameStage(target.level(), target.position()));

        return target.addEffect(new MobEffectInstance(TimeHelper.toTicks(effectDef.duration), effectDef.amplifier, attacker));
    }

    public static EffectDef getCurrentEffect(GameStage gameStage) {
        return GameStageValue.of(BleedingConfig.get().effects()).get(gameStage);
    }

    public static boolean isEnabled() {
        return BleedingConfig.get().isEnabled();
    }

    public static boolean canApplyTo(LivingEntity entity) {
        BleedingConfig settings = BleedingConfig.get();
        return settings.isApplicableToAnimals() && entity instanceof Animal && !settings.immuneMobs().contains(entity.getType())
                || settings.isApplicableToIllagers() && entity.getType().is(EntityTypeTags.ILLAGER) && !settings.immuneMobs().contains(entity.getType())
                || settings.otherApplicableMobs().contains(entity.getType());
    }

    public Bleeding() {
        super(MobEffectCategory.HARMFUL, 0xffdd5555);
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        return true;
    }

    @Override
    public void applyInstantenousEffect(@Nullable Entity source, @Nullable Entity indirectSource, LivingEntity entity, int amplifier, double health) {
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return false;
    }

    /**
     * Bleeding effect instance that stores information about the causer of bleeding. (required for converting villager to zombie villager etc.)
     */
    public static class MobEffectInstance extends net.minecraft.world.effect.MobEffectInstance {
        public final @Nullable Entity damageSourceEntity;

        public MobEffectInstance(int duration, int amplifier, @Nullable LivingEntity attacker) {
            super(MajruszsDifficulty.effectHolder(ModEffects.BLEEDING_EFFECT), duration, amplifier, false, false, true);

            this.damageSourceEntity = attacker;
        }
    }
}
