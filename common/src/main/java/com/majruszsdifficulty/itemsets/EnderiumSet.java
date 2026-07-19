package com.majruszsdifficulty.itemsets;

import cc.sighs.oelib.event.EventPriority;
import cc.sighs.oelib.event.Subscribe;
import com.majruszsdifficulty.events.ServerChorusFruitTeleportEvent;
import com.majruszsdifficulty.events.ServerEndermanAngerEvent;
import com.majruszsdifficulty.events.ServerLivingEntityDeathEvent;
import com.majruszsdifficulty.internal.entity.EffectHelper;
import com.majruszsdifficulty.internal.entity.EntityHelper;
import com.majruszsdifficulty.internal.level.LevelHelper;
import com.majruszsdifficulty.internal.math.AnyPos;
import com.majruszsdifficulty.internal.math.Random;
import com.majruszsdifficulty.internal.text.TextHelper;
import com.majruszsdifficulty.internal.time.TimeHelper;
import com.majruszsdifficulty.registry.ModItems;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.stream.Stream;

public class EnderiumSet {
    static final Holder<MobEffect>[] EFFECTS = new Holder[]{
            MobEffects.ABSORPTION,
            MobEffects.DAMAGE_BOOST,
            MobEffects.DAMAGE_RESISTANCE,
            MobEffects.FIRE_RESISTANCE,
            MobEffects.JUMP,
            MobEffects.MOVEMENT_SPEED,
            MobEffects.SATURATION
    };
    static final ItemSetRequirement HELMET = ItemSetRequirement.of(ModItems.ENDERIUM_HELMET_ITEM, EquipmentSlot.HEAD);
    static final ItemSetRequirement CHESTPLATE = ItemSetRequirement.of(ModItems.ENDERIUM_CHESTPLATE_ITEM, EquipmentSlot.CHEST);
    static final ItemSetRequirement LEGGINGS = ItemSetRequirement.of(ModItems.ENDERIUM_LEGGINGS_ITEM, EquipmentSlot.LEGS);
    static final ItemSetRequirement BOOTS = ItemSetRequirement.of(ModItems.ENDERIUM_BOOTS_ITEM, EquipmentSlot.FEET);
    static final ItemSetBonus ENDERMAN_PROTECTION = ItemSetBonus.requires(HELMET)
            .component("majruszsdifficulty.sets.enderium.bonus_1", TextHelper.translatable("item.majruszsdifficulty.enderium_helmet"));
    static final ItemSetBonus END_LOOTING = ItemSetBonus.any(2)
            .component("majruszsdifficulty.sets.enderium.bonus_2");
    static final ItemSetBonus CHORUS_FRUIT = ItemSetBonus.any(3)
            .component("majruszsdifficulty.sets.enderium.bonus_3", Items.CHORUS_FRUIT.getDescription());
    static final ItemSetBonus VOID_PROTECTION = ItemSetBonus.any(4)
            .component("majruszsdifficulty.sets.enderium.bonus_4");
    static final ItemSet ITEM_SET = ItemSet.create()
            .component("majruszsdifficulty.sets.enderium.name")
            .format(ChatFormatting.DARK_PURPLE)
            .require(HELMET, CHESTPLATE, LEGGINGS, BOOTS)
            .bonus(ENDERMAN_PROTECTION, END_LOOTING, CHORUS_FRUIT, VOID_PROTECTION);

    @Subscribe
    private static void cancelEndermanAnger(ServerEndermanAngerEvent data) {
        if (ITEM_SET.canTrigger(ENDERMAN_PROTECTION, data.player)) {
            data.cancel();
        }
    }

    public static int modifyLootingLevel(Holder<Enchantment> enchantment, LivingEntity entity, int original) {
        return enchantment.is(Enchantments.LOOTING) && entity.level().dimension() == Level.END
                && ITEM_SET.canTrigger(END_LOOTING, entity) ? original + 1 : original;
    }

    @Subscribe
    private static void giveRandomEffect(ServerChorusFruitTeleportEvent data) {
        if (!ITEM_SET.canTrigger(CHORUS_FRUIT, data.entity)) {
            return;
        }
        List<Holder<MobEffect>> notAppliedEffects = Stream.of(EFFECTS)
                .filter(effect -> EffectHelper.getAmplifier(effect::value, data.entity).isEmpty())
                .toList();
        if (notAppliedEffects.isEmpty()) {
            notAppliedEffects = List.of(EFFECTS);
        }

        data.entity.addEffect(new MobEffectInstance(Random.next(notAppliedEffects), TimeHelper.toTicks(90), 0));
        data.cancelTeleport();
    }

    @Subscribe(priority = EventPriority.HIGHEST)
    private static void cancelDeath(ServerLivingEntityDeathEvent data) {
        if (!(data.target instanceof ServerPlayer player)
                || !ITEM_SET.canTrigger(VOID_PROTECTION, data.target)
                || data.target.getY() >= data.target.level().getMinBuildHeight() - 64
                || !data.source.is(DamageTypes.FELL_OUT_OF_WORLD)) {
            return;
        }
        LevelHelper.getSpawnPoint(player)
                .map(spawnPoint -> new LevelHelper.SpawnPoint(spawnPoint.level(), AnyPos.from(spawnPoint.position()).add(0.0, 0.5, 0.0).vec3()))
                .ifPresent(spawnPoint -> spawnPoint.teleport(player));
        EntityHelper.cheatDeath(data.target);
        data.target.setDeltaMovement(0.0, 0.5, 0.0);
        data.cancelDeath();
    }
}
