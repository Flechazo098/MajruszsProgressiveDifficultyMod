package com.majruszsdifficulty.registry;

import cc.sighs.oelib.registry.DeferredRegister;
import com.majruszsdifficulty.MajruszsDifficulty;
import com.majruszsdifficulty.gamestage.GameStageAdvancement;
import com.majruszsdifficulty.internal.modhelper.BasicAdvancement;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.core.registries.Registries;

public final class ModAdvancements {
    private static final DeferredRegister<CriterionTrigger<?>> REGISTER = DeferredRegister.create(Registries.TRIGGER_TYPE, MajruszsDifficulty.MOD_ID);

    public static final GameStageAdvancement GAME_STAGE_ADVANCEMENT = new GameStageAdvancement();
    public static final BasicAdvancement BASIC_ADVANCEMENT = new BasicAdvancement();

    static {
        REGISTER.register("game_stage", () -> GAME_STAGE_ADVANCEMENT);
        REGISTER.register("basic_trigger", () -> BASIC_ADVANCEMENT);
    }

    public static void register() {
        REGISTER.register();
    }

    private ModAdvancements() {
    }
}
