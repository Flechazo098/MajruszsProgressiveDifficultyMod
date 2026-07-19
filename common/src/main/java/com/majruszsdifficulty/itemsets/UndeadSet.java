package com.majruszsdifficulty.itemsets;

import cc.sighs.oelib.event.Subscribe;
import com.majruszsdifficulty.events.OnSoulJarMultiplierGet;
import com.majruszsdifficulty.events.ServerLivingEntityDeathEvent;
import com.majruszsdifficulty.internal.text.TextHelper;
import com.majruszsdifficulty.registry.ModItems;
import net.minecraft.ChatFormatting;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;

public class UndeadSet {
    static final ItemSetRequirement HELMET = ItemSetRequirement.of(ModItems.TATTERED_HELMET_ITEM, EquipmentSlot.HEAD);
    static final ItemSetRequirement CHESTPLATE = ItemSetRequirement.of(ModItems.TATTERED_CHESTPLATE_ITEM, EquipmentSlot.CHEST);
    static final ItemSetRequirement LEGGINGS = ItemSetRequirement.of(ModItems.TATTERED_LEGGINGS_ITEM, EquipmentSlot.LEGS);
    static final ItemSetRequirement BOOTS = ItemSetRequirement.of(ModItems.TATTERED_BOOTS_ITEM, EquipmentSlot.FEET);
    static final ItemSetBonus DURABILITY = ItemSetBonus.any(3)
            .component("majruszsdifficulty.sets.undead.bonus_1", 1);
    static final ItemSetBonus SOUL_JAR = ItemSetBonus.any(4)
            .component("majruszsdifficulty.sets.undead.bonus_2", TextHelper.translatable("item.majruszsdifficulty.soul_jar"));
    static final ItemSet ITEM_SET = ItemSet.create()
            .component("majruszsdifficulty.sets.undead.name")
            .format(ChatFormatting.LIGHT_PURPLE)
            .require(HELMET, CHESTPLATE, LEGGINGS, BOOTS)
            .bonus(DURABILITY, SOUL_JAR);

    @Subscribe
    private static void restoreDurability(ServerLivingEntityDeathEvent data) {
        if (data.attacker == null || !(data.target instanceof Mob) || !ITEM_SET.canTrigger(DURABILITY, data.attacker)) {
            return;
        }
        data.attacker.getArmorSlots().forEach(itemStack -> {
            if (itemStack.isDamaged()) {
                itemStack.setDamageValue(itemStack.getDamageValue() - 1);
            }
        });
    }

    @Subscribe
    private static void increaseSoulBonuses(OnSoulJarMultiplierGet data) {
        if (ITEM_SET.canTrigger(SOUL_JAR, data.entity)) {
            data.multiplier *= 2.0f;
        }
    }
}
