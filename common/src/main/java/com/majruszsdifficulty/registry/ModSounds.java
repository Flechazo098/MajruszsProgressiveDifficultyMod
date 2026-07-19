package com.majruszsdifficulty.registry;

import cc.sighs.oelib.registry.DeferredRegister;
import cc.sighs.oelib.registry.RegisterSupplier;
import com.majruszsdifficulty.MajruszsDifficulty;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;

public final class ModSounds {
    private static final DeferredRegister<SoundEvent> REGISTER = DeferredRegister.create(Registries.SOUND_EVENT, MajruszsDifficulty.MOD_ID);

    public static final RegisterSupplier<SoundEvent> UNDEAD_ARMY_APPROACHING_SOUND = register("undead_army.approaching");
    public static final RegisterSupplier<SoundEvent> UNDEAD_ARMY_WAVE_STARTED_SOUND = register("undead_army.wave_started");

    public static void register() {
        REGISTER.register();
    }

    private static RegisterSupplier<SoundEvent> register(String name) {
        return REGISTER.register(name, () -> SoundEvent.createVariableRangeEvent(MajruszsDifficulty.id(name)));
    }

    private ModSounds() {
    }
}
