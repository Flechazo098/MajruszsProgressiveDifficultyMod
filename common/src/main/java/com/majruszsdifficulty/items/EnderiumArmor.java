package com.majruszsdifficulty.items;

import com.majruszsdifficulty.MajruszsDifficulty;
import com.majruszsdifficulty.registry.ModArmorMaterials;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Rarity;

import java.util.function.Supplier;

public class EnderiumArmor extends ArmorItem {
    public static Supplier<EnderiumArmor> boots() {
        return () -> new EnderiumArmor(Type.BOOTS);
    }

    public static Supplier<EnderiumArmor> chestplate() {
        return () -> new EnderiumArmor(Type.CHESTPLATE);
    }

    public static Supplier<EnderiumArmor> helmet() {
        return () -> new EnderiumArmor(Type.HELMET);
    }

    public static Supplier<EnderiumArmor> leggings() {
        return () -> new EnderiumArmor(Type.LEGGINGS);
    }

    private EnderiumArmor(ArmorItem.Type type) {
        super(MajruszsDifficulty.armorMaterialHolder(ModArmorMaterials.ENDERIUM_ARMOR_MATERIAL), type,
                new Properties().durability(type.getDurability(39)).rarity(Rarity.UNCOMMON).fireResistant());
    }
}
