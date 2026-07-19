package com.majruszsdifficulty.features;

import cc.sighs.oelib.event.Subscribe;
import com.majruszsdifficulty.config.AdvancedFeatureConfig;
import com.majruszsdifficulty.events.ServerEntityJoinEvent;
import com.majruszsdifficulty.gamestage.GameStageHelper;
import com.majruszsdifficulty.internal.math.Random;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.monster.Pillager;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.FireworkExplosion;
import net.minecraft.world.item.component.Fireworks;
import net.minecraft.world.item.enchantment.Enchantments;

import java.util.List;

public class PillagerWithFireworks {
    private static AdvancedFeatureConfig.PillagerWithFireworks settings() {
        return AdvancedFeatureConfig.get().pillagerWithFireworks();
    }

    @Subscribe
    private static void giveFireworks(ServerEntityJoinEvent data) {
        AdvancedFeatureConfig.PillagerWithFireworks settings = settings();
        if (data.entity.getType() != EntityType.PILLAGER || data.isLoadedFromDisk || !settings.isEnabled()
                || !data.isAtLeast(GameStageHelper.find(settings.requiredGameStage()))
                || !data.passesChance((float) settings.chance(), settings.isScaledByCrd())) {
            return;
        }
        Pillager pillager = (Pillager) data.entity;
        ItemStack crossbow = pillager.getMainHandItem();
        if (crossbow.is(Items.CROSSBOW) && Random.check((float) settings.crossbowMultishotChance())) {
            var multishot = data.getServerLevel().registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.MULTISHOT);
            crossbow.enchant(multishot, 1);
        }

        ItemStack fireworks = new ItemStack(Items.FIREWORK_ROCKET, settings.fireworkCount());
        FireworkExplosion explosion = new FireworkExplosion(
                FireworkExplosion.Shape.SMALL_BALL,
                IntList.of(0x4a2a30, 0x666666),
                IntList.of(),
                false,
                false
        );
        fireworks.set(DataComponents.FIREWORKS, new Fireworks(0, List.of(explosion)));
        pillager.setItemSlot(EquipmentSlot.OFFHAND, fireworks);
        pillager.setDropChance(EquipmentSlot.OFFHAND, (float) settings.fireworkDropChance());
    }
}
