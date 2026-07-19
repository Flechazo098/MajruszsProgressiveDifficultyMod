package com.majruszsdifficulty.effects.bleeding;

import cc.sighs.oelib.event.Subscribe;
import com.majruszsdifficulty.MajruszsDifficulty;
import com.majruszsdifficulty.events.OnBleedingTooltip;
import com.majruszsdifficulty.events.ServerMobEffectApplicableEvent;
import com.majruszsdifficulty.internal.item.EquipmentSlots;
import com.majruszsdifficulty.internal.math.Random;
import com.majruszsdifficulty.internal.math.Range;
import com.majruszsdifficulty.registry.ModEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;

public class BleedingArmorProtection {
    @Subscribe
    private static void blockBleeding(ServerMobEffectApplicableEvent data) {
        if (data.effect.equals(MajruszsDifficulty.effectHolder(ModEffects.BLEEDING_EFFECT))
                && Random.check(BleedingArmorProtection.calculateCancelChance(data))) {
            data.cancelEffect();
        }
    }

    private static float calculateCancelChance(ServerMobEffectApplicableEvent data) {
        float chance = 1.0f;
        for (EquipmentSlot slot : EquipmentSlots.ARMOR) {
            if (data.entity.getItemBySlot(slot).getItem() instanceof ArmorItem armorItem) {
                chance *= 1.0f - BleedingArmorProtection.getArmorMultiplier(armorItem);
            }
        }

        return 1.0f - chance;
    }

    @Subscribe
    private static void addTooltip(OnBleedingTooltip data) {
        if (data.itemStack.getItem() instanceof ArmorItem armorItem) {
            data.addArmor(armorItem.getEquipmentSlot(), BleedingArmorProtection.getArmorMultiplier(armorItem));
        }
    }

    private static float getArmorMultiplier(ArmorItem item) {
        BleedingConfig.ArmorProtection settings = BleedingConfig.get().armorProtection();
        return 1.0f - Range.of(0.0f, 0.9f).clamp((float) (settings.base() + settings.perArmor() * item.getDefense() + settings.perToughness() * item.getToughness()));
    }
}
