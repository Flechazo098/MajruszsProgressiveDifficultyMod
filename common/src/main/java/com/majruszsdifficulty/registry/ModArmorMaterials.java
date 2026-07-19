package com.majruszsdifficulty.registry;

import cc.sighs.oelib.registry.DeferredRegister;
import cc.sighs.oelib.registry.RegisterSupplier;
import com.majruszsdifficulty.MajruszsDifficulty;
import com.majruszsdifficulty.items.CustomArmorMaterial;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.ArmorMaterial;

public final class ModArmorMaterials {
    private static final DeferredRegister<ArmorMaterial> REGISTER = DeferredRegister.create(Registries.ARMOR_MATERIAL, MajruszsDifficulty.MOD_ID);

    public static final RegisterSupplier<ArmorMaterial> TATTERED_ARMOR_MATERIAL = REGISTER.register("tattered", CustomArmorMaterial::tattered);
    public static final RegisterSupplier<ArmorMaterial> ENDERIUM_ARMOR_MATERIAL = REGISTER.register("enderium", CustomArmorMaterial::enderium);

    public static void register() {
        REGISTER.register();
    }

    private ModArmorMaterials() {
    }
}
