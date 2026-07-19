package com.majruszsdifficulty.registry;

import cc.sighs.oelib.registry.DeferredRegister;
import cc.sighs.oelib.registry.RegisterSupplier;
import com.majruszsdifficulty.MajruszsDifficulty;
import com.majruszsdifficulty.effects.Bleeding;
import com.majruszsdifficulty.effects.BleedingImmunity;
import com.majruszsdifficulty.effects.GlassRegeneration;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;

public final class ModEffects {
    private static final DeferredRegister<MobEffect> REGISTER = DeferredRegister.create(Registries.MOB_EFFECT, MajruszsDifficulty.MOD_ID);

    public static final RegisterSupplier<Bleeding> BLEEDING_EFFECT = REGISTER.register("bleeding", Bleeding::new);
    public static final RegisterSupplier<BleedingImmunity> BLEEDING_IMMUNITY_EFFECT = REGISTER.register("bleeding_immunity", BleedingImmunity::new);
    public static final RegisterSupplier<GlassRegeneration> GLASS_REGENERATION_EFFECT = REGISTER.register("glass_regeneration", GlassRegeneration::new);

    public static void register() {
        REGISTER.register();
    }

    private ModEffects() {
    }
}
