package com.majruszsdifficulty.registry;

import cc.sighs.oelib.registry.DeferredRegister;
import cc.sighs.oelib.registry.RegisterSupplier;
import com.majruszsdifficulty.MajruszsDifficulty;
import com.majruszsdifficulty.recipes.SoulJarShieldRecipe;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;

public final class ModRecipes {
    private static final DeferredRegister<RecipeSerializer<?>> REGISTER = DeferredRegister.create(Registries.RECIPE_SERIALIZER, MajruszsDifficulty.MOD_ID);

    public static final RegisterSupplier<RecipeSerializer<?>> SOUL_JAR_SHIELD_RECIPE = REGISTER.register("soul_jar_shield", SoulJarShieldRecipe.create());

    public static void register() {
        REGISTER.register();
    }

    private ModRecipes() {
    }
}
