package com.majruszsdifficulty.internal.item;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public final class EnchantmentHelper {
    public static EnchantmentsDef read(ItemStack itemStack) {
        EnchantmentsDef result = new EnchantmentsDef();
        itemStack.getEnchantments().entrySet().forEach(entry -> entry.getKey().unwrapKey().ifPresent(key ->
                result.enchantments.add(new EnchantmentDef(key.location(), entry.getIntValue()))
        ));
        return result;
    }

    public static final class EnchantmentsDef {
        public final List<EnchantmentDef> enchantments = new ArrayList<>();
    }

    public record EnchantmentDef(ResourceLocation id, int level) {
    }

    private EnchantmentHelper() {
    }
}
