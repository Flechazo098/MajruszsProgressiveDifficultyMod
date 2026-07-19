package com.majruszsdifficulty.items;

import com.majruszsdifficulty.MajruszsDifficulty;
import com.majruszsdifficulty.internal.text.TextHelper;
import com.majruszsdifficulty.registry.ModItems;
import com.majruszsdifficulty.registry.ModPotions;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;

import java.util.function.Supplier;
import java.util.stream.Stream;

public class CreativeModeTabs {
    private static final Component PRIMARY = TextHelper.translatable("itemGroup.majruszsdifficulty.primary");

    public static Supplier<CreativeModeTab> primary() {
        return () -> CreativeModeTab.builder(CreativeModeTab.Row.TOP, 0)
                .title(PRIMARY)
                .displayItems(CreativeModeTabs::definePrimaryItems)
                .icon(() -> new ItemStack(ModItems.UNDEAD_BATTLE_STANDARD_ITEM.get()))
                .build();
    }

    private static void definePrimaryItems(CreativeModeTab.ItemDisplayParameters params, CreativeModeTab.Output output) {
        Stream.of(
                ModItems.INFERNAL_SPONGE_ITEM,
                ModItems.SOAKED_INFERNAL_SPONGE_ITEM,
                ModItems.ENDERIUM_BLOCK_ITEM,
                ModItems.ENDERIUM_SHARD_ORE_ITEM,
                ModItems.FRAGILE_END_STONE_ITEM,
                ModItems.INFESTED_END_STONE_ITEM,
                ModItems.BANDAGE_ITEM,
                ModItems.GOLDEN_BANDAGE_ITEM,
                ModItems.CLOTH_ITEM,
                ModItems.UNDEAD_BATTLE_STANDARD_ITEM,
                ModItems.SOUL_JAR_ITEM,
                ModItems.RECALL_POTION_ITEM,
                ModItems.EVOKER_FANG_SCROLL_ITEM,
                ModItems.SONIC_BOOM_SCROLL_ITEM,
                ModItems.CERBERUS_FANG_ITEM,
                ModItems.ENDER_POUCH_ITEM,
                ModItems.ENDERIUM_SHARD_LOCATOR_ITEM,
                ModItems.ENDERIUM_SHARD_ITEM,
                ModItems.ENDERIUM_INGOT_ITEM,
                ModItems.ENDERIUM_SMITHING_TEMPLATE_ITEM
        ).map(item -> new ItemStack(item.get())).forEach(output::accept);

        Stream.of(Items.TIPPED_ARROW, Items.POTION, Items.SPLASH_POTION, Items.LINGERING_POTION).forEach(item ->
                Stream.of(
                        ModPotions.WITHER_POTION,
                        ModPotions.WITHER_LONG_POTION,
                        ModPotions.WITHER_STRONG_POTION
                ).forEach(potion -> output.accept(PotionContents.createItemStack(item, MajruszsDifficulty.potionHolder(potion))))
        );

        Stream.of(
                ModItems.WITHER_SWORD_ITEM,
                ModItems.ENDERIUM_SWORD_ITEM,
                ModItems.ENDERIUM_SHOVEL_ITEM,
                ModItems.ENDERIUM_PICKAXE_ITEM,
                ModItems.ENDERIUM_AXE_ITEM,
                ModItems.ENDERIUM_HOE_ITEM,
                ModItems.TATTERED_HELMET_ITEM,
                ModItems.TATTERED_CHESTPLATE_ITEM,
                ModItems.TATTERED_LEGGINGS_ITEM,
                ModItems.TATTERED_BOOTS_ITEM,
                ModItems.ENDERIUM_HELMET_ITEM,
                ModItems.ENDERIUM_CHESTPLATE_ITEM,
                ModItems.ENDERIUM_LEGGINGS_ITEM,
                ModItems.ENDERIUM_BOOTS_ITEM,
                ModItems.ANGLER_TREASURE_BAG_ITEM,
                ModItems.ELDER_GUARDIAN_TREASURE_BAG_ITEM,
                ModItems.ENDER_DRAGON_TREASURE_BAG_ITEM,
                ModItems.PILLAGER_TREASURE_BAG_ITEM,
                ModItems.UNDEAD_ARMY_TREASURE_BAG_ITEM,
                ModItems.WARDEN_TREASURE_BAG_ITEM,
                ModItems.WITHER_TREASURE_BAG_ITEM,
                ModItems.CERBERUS_SPAWN_EGG_ITEM,
                ModItems.CREEPERLING_SPAWN_EGG_ITEM,
                ModItems.CURSED_ARMOR_SPAWN_EGG_ITEM,
                ModItems.GIANT_SPAWN_EGG_ITEM,
                ModItems.ILLUSIONER_SPAWN_EGG_ITEM,
                ModItems.TANK_SPAWN_EGG_ITEM
        ).map(item -> new ItemStack(item.get())).forEach(output::accept);
    }
}
