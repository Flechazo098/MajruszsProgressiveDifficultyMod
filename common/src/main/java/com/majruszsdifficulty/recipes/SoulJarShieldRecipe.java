package com.majruszsdifficulty.recipes;

import com.majruszsdifficulty.items.SoulJar;
import com.majruszsdifficulty.registry.ModRecipes;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

import java.util.function.Supplier;

public class SoulJarShieldRecipe extends CustomRecipe {
    public static Supplier<RecipeSerializer<?>> create() {
        return () -> new SimpleCraftingRecipeSerializer<>(SoulJarShieldRecipe::new);
    }

    public SoulJarShieldRecipe(CraftingBookCategory category) {
        super(category);
    }

    @Override
    public boolean matches(CraftingInput container, Level level) {
        RecipeData recipeData = SoulJarShieldRecipe.convert(container);

        return !recipeData.shield.isEmpty() && !recipeData.soulJar.isEmpty();
    }

    @Override
    public ItemStack assemble(CraftingInput container, HolderLookup.Provider registries) {
        RecipeData recipeData = SoulJarShieldRecipe.convert(container);
        ItemStack shield = recipeData.shield.copy();
        SoulJar.BonusInfo.read(recipeData.soulJar).writeTo(shield);

        return shield;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.SOUL_JAR_SHIELD_RECIPE.get();
    }

    private static RecipeData convert(CraftingInput container) {
        ItemStack soulJar = ItemStack.EMPTY;
        ItemStack shield = ItemStack.EMPTY;
        for (int i = 0; i < container.size(); ++i) {
            ItemStack itemStack = container.getItem(i);
            if (itemStack.isEmpty()) {
                continue;
            }

            if (itemStack.getItem() instanceof SoulJar && soulJar.isEmpty()) {
                soulJar = itemStack;
            } else if (itemStack.getItem() instanceof ShieldItem && shield.isEmpty()) {
                shield = itemStack;
            } else {
                return new RecipeData(ItemStack.EMPTY, ItemStack.EMPTY);
            }
        }

        return new RecipeData(soulJar, shield);
    }

    record RecipeData(ItemStack soulJar, ItemStack shield) {
    }
}
