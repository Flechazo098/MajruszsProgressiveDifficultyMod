package com.majruszsdifficulty.items;

import com.majruszsdifficulty.registry.ModItems;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;

import java.util.function.Supplier;

public enum CustomItemTier implements Tier {
    WITHER(BlockTags.INCORRECT_FOR_DIAMOND_TOOL, 360, 15, 5.0f, 3.0f, () -> Ingredient.of(ModItems.CERBERUS_FANG_ITEM.get())),
    ENDERIUM(BlockTags.INCORRECT_FOR_NETHERITE_TOOL, 2137, 15, 10.0f, 5.0f, () -> Ingredient.of(ModItems.ENDERIUM_INGOT_ITEM.get()));

    final TagKey<Block> incorrectBlocksForDrops;
    final int maxUses, enchantability;
    final float efficiency, attackDamage;
    final Supplier<Ingredient> repairMaterial;

    CustomItemTier(TagKey<Block> incorrectBlocksForDrops, int uses, int enchantability, float efficiency, float damage, Supplier<Ingredient> material) {
        this.incorrectBlocksForDrops = incorrectBlocksForDrops;
        this.maxUses = uses;
        this.enchantability = enchantability;
        this.efficiency = efficiency;
        this.attackDamage = damage;
        this.repairMaterial = material;
    }

    @Override
    public int getUses() {
        return this.maxUses;
    }

    @Override
    public float getSpeed() {
        return this.efficiency;
    }

    @Override
    public float getAttackDamageBonus() {
        return this.attackDamage;
    }

    @Override
    public TagKey<Block> getIncorrectBlocksForDrops() {
        return this.incorrectBlocksForDrops;
    }

    @Override
    public int getEnchantmentValue() {
        return this.enchantability;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return this.repairMaterial.get();
    }
}
