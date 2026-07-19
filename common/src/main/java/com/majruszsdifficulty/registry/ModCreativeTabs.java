package com.majruszsdifficulty.registry;

import cc.sighs.oelib.registry.DeferredRegister;
import cc.sighs.oelib.registry.RegisterSupplier;
import com.majruszsdifficulty.MajruszsDifficulty;
import com.majruszsdifficulty.items.CreativeModeTabs;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.CreativeModeTab;

public final class ModCreativeTabs {
    private static final DeferredRegister<CreativeModeTab> REGISTER = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MajruszsDifficulty.MOD_ID);

    public static final RegisterSupplier<CreativeModeTab> PRIMARY_MODE_TAB = REGISTER.register("primary", CreativeModeTabs.primary());

    public static void register() {
        REGISTER.register();
    }

    private ModCreativeTabs() {
    }
}
