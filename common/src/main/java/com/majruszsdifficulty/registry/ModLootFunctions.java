package com.majruszsdifficulty.registry;

import cc.sighs.oelib.registry.DeferredRegister;
import cc.sighs.oelib.registry.RegisterSupplier;
import com.majruszsdifficulty.MajruszsDifficulty;
import com.majruszsdifficulty.loot.CurseRandomly;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;

public final class ModLootFunctions {
    private static final DeferredRegister<LootItemFunctionType<?>> REGISTER = DeferredRegister.create(Registries.LOOT_FUNCTION_TYPE, MajruszsDifficulty.MOD_ID);

    public static final RegisterSupplier<LootItemFunctionType<CurseRandomly>> CURSE_RANDOMLY_LOOT_FUNCTION = REGISTER.register("curse_randomly", CurseRandomly::create);

    public static void register() {
        REGISTER.register();
    }

    private ModLootFunctions() {
    }
}
