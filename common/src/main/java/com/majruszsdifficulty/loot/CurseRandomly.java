package com.majruszsdifficulty.loot;

import com.majruszsdifficulty.registry.ModLootFunctions;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import java.util.List;

public class CurseRandomly extends LootItemConditionalFunction {
    public static final MapCodec<CurseRandomly> CODEC = RecordCodecBuilder.mapCodec(instance ->
            commonFields(instance).apply(instance, CurseRandomly::new)
    );

    public static LootItemFunctionType<CurseRandomly> create() {
        return new LootItemFunctionType<>(CODEC);
    }

    public CurseRandomly(List<LootItemCondition> itemConditions) {
        super(itemConditions);
    }

    @Override
    public LootItemFunctionType<CurseRandomly> getType() {
        return ModLootFunctions.CURSE_RANDOMLY_LOOT_FUNCTION.get();
    }

    @Override
    protected ItemStack run(ItemStack itemStack, LootContext context) {
        List<Holder<Enchantment>> curses = this.generateEnchantments(itemStack, context, true);
        if (!curses.isEmpty()) {
            itemStack = CurseRandomly.enchantItem(itemStack, context.getRandom(), curses);
        }
        List<Holder<Enchantment>> enchantments = this.generateEnchantments(itemStack, context, false);
        if (!enchantments.isEmpty()) {
            itemStack = CurseRandomly.enchantItem(itemStack, context.getRandom(), enchantments);
        }

        return itemStack;
    }

    private List<Holder<Enchantment>> generateEnchantments(ItemStack itemStack, LootContext context, boolean curses) {
        return context.getLevel()
                .registryAccess()
                .lookupOrThrow(Registries.ENCHANTMENT)
                .getOrThrow(EnchantmentTags.ON_RANDOM_LOOT)
                .stream()
                .filter(enchantment -> enchantment.is(EnchantmentTags.CURSE) == curses)
                .filter(enchantment -> enchantment.value().canEnchant(itemStack))
                .toList();
    }

    private static ItemStack enchantItem(ItemStack itemStack, RandomSource randomSource, List<Holder<Enchantment>> enchantments) {
        Holder<Enchantment> enchantment = enchantments.get(randomSource.nextInt(enchantments.size()));
        int level = Mth.nextInt(randomSource, enchantment.value().getMinLevel(), enchantment.value().getMaxLevel());
        if (itemStack.is(Items.BOOK)) {
            return EnchantedBookItem.createForEnchantment(new EnchantmentInstance(enchantment, level));
        }

        itemStack.enchant(enchantment, level);
        return itemStack;
    }
}
