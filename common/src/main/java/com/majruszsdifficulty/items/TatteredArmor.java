package com.majruszsdifficulty.items;

import com.majruszsdifficulty.MajruszsDifficulty;
import com.majruszsdifficulty.registry.ModArmorMaterials;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Rarity;

import java.util.function.Supplier;

public class TatteredArmor extends ArmorItem {
    public static Supplier<TatteredArmor> boots() {
        return () -> new TatteredArmor(Type.BOOTS);
    }

    public static Supplier<TatteredArmor> chestplate() {
        return () -> new TatteredArmor(Type.CHESTPLATE);
    }

    public static Supplier<TatteredArmor> helmet() {
        return () -> new TatteredArmor(Type.HELMET);
    }

    public static Supplier<TatteredArmor> leggings() {
        return () -> new TatteredArmor(Type.LEGGINGS);
    }

    private TatteredArmor(Type type) {
        super(MajruszsDifficulty.armorMaterialHolder(ModArmorMaterials.TATTERED_ARMOR_MATERIAL), type,
                new Properties().durability(type.getDurability(5)).rarity(Rarity.UNCOMMON));
    }
}
