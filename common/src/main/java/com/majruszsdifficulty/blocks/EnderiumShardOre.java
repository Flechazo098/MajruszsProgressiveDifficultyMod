package com.majruszsdifficulty.blocks;

import com.majruszsdifficulty.internal.math.Random;
import com.majruszsdifficulty.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.MapColor;

public class EnderiumShardOre extends Block {
    public EnderiumShardOre() {
        super(Properties.of()
                .mapColor(MapColor.SAND)
                .requiresCorrectToolForDrops()
                .strength(10.0f, 1200.0f)
                .sound(SoundType.ANCIENT_DEBRIS));
    }

    @Override
    protected void tryDropExperience(ServerLevel level, BlockPos position, ItemStack itemStack, IntProvider intProvider) {
        var silkTouch = level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.SILK_TOUCH);
        if (EnchantmentHelper.getItemEnchantmentLevel(silkTouch, itemStack) == 0) {
            this.popExperience(level, position, Random.nextInt(6, 11));
        }
    }

    public static class Item extends BlockItem {
        public Item() {
            super(ModBlocks.ENDERIUM_SHARD_ORE_BLOCK.get(), new Properties().stacksTo(64));
        }
    }
}
