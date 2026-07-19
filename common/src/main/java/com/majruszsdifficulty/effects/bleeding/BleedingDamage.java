package com.majruszsdifficulty.effects.bleeding;

import cc.sighs.oelib.event.EventBus;
import cc.sighs.oelib.event.Subscribe;
import com.majruszsdifficulty.MajruszsDifficulty;
import com.majruszsdifficulty.effects.Bleeding;
import com.majruszsdifficulty.events.OnBleedingCheck;
import com.majruszsdifficulty.events.ServerLivingEntityDamagedEvent;
import com.majruszsdifficulty.events.ServerLivingEntityTickEvent;
import com.majruszsdifficulty.internal.entity.EffectHelper;
import com.majruszsdifficulty.internal.entity.EntityHelper;
import com.majruszsdifficulty.internal.math.Random;
import com.majruszsdifficulty.internal.time.TimeHelper;
import com.majruszsdifficulty.registry.ModDamageTypes;
import com.majruszsdifficulty.registry.ModEffects;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.Map;

public class BleedingDamage {
    static final int DAMAGE_COOLDOWN = TimeHelper.toTicks(4.0);
    static final Map<Integer, Integer> ENTITY_TICKS = new HashMap<>();

    static {
    }

    @Subscribe
    private static void tryToApply(ServerLivingEntityDamagedEvent data) {
        if (!Bleeding.isEnabled() || !Bleeding.canApplyTo(data.target)) {
            return;
        }
        OnBleedingCheck bleedingCheck = new OnBleedingCheck(data);
        EventBus.post(bleedingCheck);
        if (bleedingCheck.isBleedingTriggered() && Bleeding.apply(data.target, data.attacker)) {
            BleedingDamage.dealDamage(data.target);
            BleedingDamage.giveAdvancements(data);
        }
    }

    @Subscribe
    private static void tick(ServerLivingEntityTickEvent data) {
        if (!EffectHelper.has(ModEffects.BLEEDING_EFFECT, data.entity())) {
            return;
        }
        int amplifier = EffectHelper.getAmplifier(ModEffects.BLEEDING_EFFECT, data.entity()).orElse(0);
        int extraDuration = Random.round(0.3 * (amplifier + 2) * (7.26 * EntityHelper.getWalkDistanceDelta(data.entity()) + 1));
        int duration = ENTITY_TICKS.getOrDefault(data.entity().getId(), 0) + extraDuration;
        if (duration > DAMAGE_COOLDOWN) {
            BleedingDamage.dealDamage(data.entity());
            duration = 0;
        }

        ENTITY_TICKS.put(data.entity().getId(), duration);
    }

    private static void dealDamage(LivingEntity entity) {
        Holder<DamageType> damageType = entity.level()
                .registryAccess()
                .registryOrThrow(Registries.DAMAGE_TYPE)
                .getHolderOrThrow(ModDamageTypes.BLEEDING_DAMAGE_SOURCE);

        if (entity.getEffect(MajruszsDifficulty.effectHolder(ModEffects.BLEEDING_EFFECT)) instanceof Bleeding.MobEffectInstance effectInstance) {
            Vec3 motion = entity.getDeltaMovement();
            entity.hurt(new DamageSource(damageType, null, effectInstance.damageSourceEntity), 1.0f);
            entity.setDeltaMovement(motion); // sets previous motion to avoid any knockback from bleeding
        } else {
            entity.hurt(new DamageSource(damageType), 1.0f);
        }
    }

    private static void giveAdvancements(ServerLivingEntityDamagedEvent data) {
        if (data.target instanceof ServerPlayer player) {
            MajruszsDifficulty.triggerAdvancement(player, "bleeding_received");
            if (data.source.is(DamageTypes.CACTUS)) {
                MajruszsDifficulty.triggerAdvancement(player, "cactus_bleeding");
            }
        }

        if (data.attacker instanceof ServerPlayer player) {
            MajruszsDifficulty.triggerAdvancement(player, "bleeding_inflicted");
        }
    }
}
