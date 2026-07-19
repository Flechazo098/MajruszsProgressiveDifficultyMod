package com.majruszsdifficulty.items;

import com.majruszsdifficulty.MajruszsDifficulty;
import com.majruszsdifficulty.registry.ModItems;
import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public final class CustomArmorMaterial {
    public static ArmorMaterial tattered() {
        return create(
                "tattered",
                Map.of(
                        ArmorItem.Type.BOOTS, 1,
                        ArmorItem.Type.LEGGINGS, 2,
                        ArmorItem.Type.CHESTPLATE, 3,
                        ArmorItem.Type.HELMET, 1,
                        ArmorItem.Type.BODY, 3
                ),
                15,
                SoundEvents.ARMOR_EQUIP_LEATHER,
                () -> Ingredient.of(ModItems.CLOTH_ITEM.get()),
                0.0f,
                0.0f
        );
    }

    public static ArmorMaterial enderium() {
        return create(
                "enderium",
                Map.of(
                        ArmorItem.Type.BOOTS, 4,
                        ArmorItem.Type.LEGGINGS, 6,
                        ArmorItem.Type.CHESTPLATE, 8,
                        ArmorItem.Type.HELMET, 4,
                        ArmorItem.Type.BODY, 8
                ),
                15,
                SoundEvents.ARMOR_EQUIP_NETHERITE,
                () -> Ingredient.of(ModItems.ENDERIUM_INGOT_ITEM.get()),
                4.0f,
                0.1f
        );
    }

    private static ArmorMaterial create(String name, Map<ArmorItem.Type, Integer> defense, int enchantmentValue,
                                        Holder<SoundEvent> equipSound, Supplier<Ingredient> repairIngredient,
                                        float toughness, float knockbackResistance
    ) {
        return new ArmorMaterial(
                defense,
                enchantmentValue,
                equipSound,
                repairIngredient,
                List.of(new ArmorMaterial.Layer(MajruszsDifficulty.id(name))),
                toughness,
                knockbackResistance
        );
    }

    private CustomArmorMaterial() {
    }
}
