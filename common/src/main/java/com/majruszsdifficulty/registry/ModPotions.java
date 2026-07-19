package com.majruszsdifficulty.registry;

import cc.sighs.oelib.registry.DeferredRegister;
import cc.sighs.oelib.registry.RegisterSupplier;
import com.majruszsdifficulty.MajruszsDifficulty;
import com.majruszsdifficulty.internal.time.TimeHelper;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.alchemy.Potion;

public final class ModPotions {
    private static final DeferredRegister<Potion> REGISTER = DeferredRegister.create(Registries.POTION, MajruszsDifficulty.MOD_ID);

    public static final RegisterSupplier<Potion> WITHER_POTION = REGISTER.register("wither", () -> new Potion(new MobEffectInstance(MobEffects.WITHER, TimeHelper.toTicks(40.0))));
    public static final RegisterSupplier<Potion> WITHER_LONG_POTION = REGISTER.register("long_wither", () -> new Potion("wither", new MobEffectInstance(MobEffects.WITHER, TimeHelper.toTicks(80.0))));
    public static final RegisterSupplier<Potion> WITHER_STRONG_POTION = REGISTER.register("strong_wither", () -> new Potion("wither", new MobEffectInstance(MobEffects.WITHER, TimeHelper.toTicks(20.0), 1)));

    public static void register() {
        REGISTER.register();
    }

    private ModPotions() {
    }
}
