package com.majruszsdifficulty.registry;

import cc.sighs.oelib.registry.DeferredRegister;
import cc.sighs.oelib.registry.RegisterSupplier;
import com.majruszsdifficulty.MajruszsDifficulty;
import com.majruszsdifficulty.blocks.*;
import com.majruszsdifficulty.internal.item.ItemHelper;
import com.majruszsdifficulty.items.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;

public final class ModItems {
    private static final DeferredRegister<Item> REGISTER = DeferredRegister.create(Registries.ITEM, MajruszsDifficulty.MOD_ID);

    public static final RegisterSupplier<Bandage> BANDAGE_ITEM = REGISTER.register("bandage", Bandage.normal());
    public static final RegisterSupplier<CerberusFang> CERBERUS_FANG_ITEM = REGISTER.register("cerberus_fang", CerberusFang::new);
    public static final RegisterSupplier<Cloth> CLOTH_ITEM = REGISTER.register("cloth", Cloth::new);
    public static final RegisterSupplier<EnderiumTool.Axe> ENDERIUM_AXE_ITEM = REGISTER.register("enderium_axe", EnderiumTool.Axe::new);
    public static final RegisterSupplier<EnderiumArmor> ENDERIUM_BOOTS_ITEM = REGISTER.register("enderium_boots", EnderiumArmor.boots());
    public static final RegisterSupplier<EnderiumArmor> ENDERIUM_CHESTPLATE_ITEM = REGISTER.register("enderium_chestplate", EnderiumArmor.chestplate());
    public static final RegisterSupplier<EnderiumArmor> ENDERIUM_HELMET_ITEM = REGISTER.register("enderium_helmet", EnderiumArmor.helmet());
    public static final RegisterSupplier<EnderiumTool.Hoe> ENDERIUM_HOE_ITEM = REGISTER.register("enderium_hoe", EnderiumTool.Hoe::new);
    public static final RegisterSupplier<EnderiumIngot> ENDERIUM_INGOT_ITEM = REGISTER.register("enderium_ingot", EnderiumIngot::new);
    public static final RegisterSupplier<EnderiumArmor> ENDERIUM_LEGGINGS_ITEM = REGISTER.register("enderium_leggings", EnderiumArmor.leggings());
    public static final RegisterSupplier<EnderiumTool.Pickaxe> ENDERIUM_PICKAXE_ITEM = REGISTER.register("enderium_pickaxe", EnderiumTool.Pickaxe::new);
    public static final RegisterSupplier<EnderiumShard> ENDERIUM_SHARD_ITEM = REGISTER.register("enderium_shard", EnderiumShard::new);
    public static final RegisterSupplier<EnderiumShardLocator> ENDERIUM_SHARD_LOCATOR_ITEM = REGISTER.register("enderium_shard_locator", EnderiumShardLocator::new);
    public static final RegisterSupplier<EnderiumTool.Shovel> ENDERIUM_SHOVEL_ITEM = REGISTER.register("enderium_shovel", EnderiumTool.Shovel::new);
    public static final RegisterSupplier<EnderiumTool.Sword> ENDERIUM_SWORD_ITEM = REGISTER.register("enderium_sword", EnderiumTool.Sword::new);
    public static final RegisterSupplier<EnderiumSmithingTemplate> ENDERIUM_SMITHING_TEMPLATE_ITEM = REGISTER.register("enderium_upgrade_smithing_template", EnderiumSmithingTemplate::new);
    public static final RegisterSupplier<EnderPouch> ENDER_POUCH_ITEM = REGISTER.register("ender_pouch", EnderPouch::new);
    public static final RegisterSupplier<EvokerFangScroll> EVOKER_FANG_SCROLL_ITEM = REGISTER.register("evoker_fang_scroll", EvokerFangScroll::new);
    public static final RegisterSupplier<Bandage> GOLDEN_BANDAGE_ITEM = REGISTER.register("golden_bandage", Bandage.golden());
    public static final RegisterSupplier<RecallPotion> RECALL_POTION_ITEM = REGISTER.register("recall_potion", RecallPotion::new);
    public static final RegisterSupplier<SonicBoomScroll> SONIC_BOOM_SCROLL_ITEM = REGISTER.register("sonic_boom_scroll", SonicBoomScroll::new);
    public static final RegisterSupplier<SoulJar> SOUL_JAR_ITEM = REGISTER.register("soul_jar", SoulJar::new);
    public static final RegisterSupplier<TatteredArmor> TATTERED_BOOTS_ITEM = REGISTER.register("tattered_boots", TatteredArmor.boots());
    public static final RegisterSupplier<TatteredArmor> TATTERED_CHESTPLATE_ITEM = REGISTER.register("tattered_chestplate", TatteredArmor.chestplate());
    public static final RegisterSupplier<TatteredArmor> TATTERED_HELMET_ITEM = REGISTER.register("tattered_helmet", TatteredArmor.helmet());
    public static final RegisterSupplier<TatteredArmor> TATTERED_LEGGINGS_ITEM = REGISTER.register("tattered_leggings", TatteredArmor.leggings());
    public static final RegisterSupplier<UndeadBattleStandard> UNDEAD_BATTLE_STANDARD_ITEM = REGISTER.register("undead_battle_standard", UndeadBattleStandard::new);
    public static final RegisterSupplier<WitherSword> WITHER_SWORD_ITEM = REGISTER.register("wither_sword", WitherSword::new);

    public static final RegisterSupplier<EnderiumBlock.Item> ENDERIUM_BLOCK_ITEM = REGISTER.register("enderium_block", EnderiumBlock.Item::new);
    public static final RegisterSupplier<EnderiumShardOre.Item> ENDERIUM_SHARD_ORE_ITEM = REGISTER.register("enderium_shard_ore", EnderiumShardOre.Item::new);
    public static final RegisterSupplier<FragileEndStone.Item> FRAGILE_END_STONE_ITEM = REGISTER.register("fragile_end_stone", FragileEndStone.Item::new);
    public static final RegisterSupplier<InfernalSponge.Item> INFERNAL_SPONGE_ITEM = REGISTER.register("infernal_sponge", InfernalSponge.Item::new);
    public static final RegisterSupplier<InfestedEndStone.Item> INFESTED_END_STONE_ITEM = REGISTER.register("infested_end_stone", InfestedEndStone.Item::new);
    public static final RegisterSupplier<SoakedInfernalSponge.Item> SOAKED_INFERNAL_SPONGE_ITEM = REGISTER.register("soaked_infernal_sponge", SoakedInfernalSponge.Item::new);

    public static final RegisterSupplier<SpawnEggItem> CERBERUS_SPAWN_EGG_ITEM = REGISTER.register("cerberus_spawn_egg", ItemHelper.createEgg(ModEntities.CERBERUS_ENTITY, 0x212121, 0xe0e0e0));
    public static final RegisterSupplier<SpawnEggItem> CREEPERLING_SPAWN_EGG_ITEM = REGISTER.register("creeperling_spawn_egg", ItemHelper.createEgg(ModEntities.CREEPERLING_ENTITY, 0x0da70b, 0x000000));
    public static final RegisterSupplier<SpawnEggItem> CURSED_ARMOR_SPAWN_EGG_ITEM = REGISTER.register("cursed_armor_spawn_egg", ItemHelper.createEgg(ModEntities.CURSED_ARMOR_ENTITY, 0x808080, 0xe1e1e1));
    public static final RegisterSupplier<SpawnEggItem> GIANT_SPAWN_EGG_ITEM = REGISTER.register("giant_spawn_egg", ItemHelper.createEgg(ModEntities.GIANT_ENTITY, 0x00afaf, 0x799c65));
    public static final RegisterSupplier<SpawnEggItem> ILLUSIONER_SPAWN_EGG_ITEM = REGISTER.register("illusioner_spawn_egg", ItemHelper.createEgg(ModEntities.ILLUSIONER_ENTITY, 0x3e293c, 0x959b9b));
    public static final RegisterSupplier<SpawnEggItem> TANK_SPAWN_EGG_ITEM = REGISTER.register("tank_spawn_egg", ItemHelper.createEgg(ModEntities.TANK_ENTITY, 0xc1c1c1, 0x949494));

    public static final RegisterSupplier<TreasureBag> ANGLER_TREASURE_BAG_ITEM = REGISTER.register("angler_treasure_bag", TreasureBag.angler());
    public static final RegisterSupplier<TreasureBag> ELDER_GUARDIAN_TREASURE_BAG_ITEM = REGISTER.register("elder_guardian_treasure_bag", TreasureBag.elderGuardian());
    public static final RegisterSupplier<TreasureBag> ENDER_DRAGON_TREASURE_BAG_ITEM = REGISTER.register("ender_dragon_treasure_bag", TreasureBag.enderDragon());
    public static final RegisterSupplier<TreasureBag> PILLAGER_TREASURE_BAG_ITEM = REGISTER.register("pillager_treasure_bag", TreasureBag.pillager());
    public static final RegisterSupplier<TreasureBag> UNDEAD_ARMY_TREASURE_BAG_ITEM = REGISTER.register("undead_army_treasure_bag", TreasureBag.undeadArmy());
    public static final RegisterSupplier<TreasureBag> WARDEN_TREASURE_BAG_ITEM = REGISTER.register("warden_treasure_bag", TreasureBag.warden());
    public static final RegisterSupplier<TreasureBag> WITHER_TREASURE_BAG_ITEM = REGISTER.register("wither_treasure_bag", TreasureBag.wither());

    static {
        REGISTER.register("advancement_bleeding", FakeItem::new);
        REGISTER.register("advancement_normal", FakeItem::new);
        REGISTER.register("advancement_expert", FakeItem::new);
        REGISTER.register("advancement_master", FakeItem::new);
    }

    public static void register() {
        REGISTER.register();
    }

    private ModItems() {
    }
}
