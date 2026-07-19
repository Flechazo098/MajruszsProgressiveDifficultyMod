package com.majruszsdifficulty.compat.modmenu;

import cc.sighs.oelib.config.ui.screen.ConfigScreen;
import com.majruszsdifficulty.MajruszsDifficulty;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.minecraft.client.gui.screens.Screen;

public final class MajruszsDifficultyModMenu implements ModMenuApi {
    @Override
    public ConfigScreenFactory<? extends Screen> getModConfigScreenFactory() {
        return parent -> new ConfigScreen(parent, MajruszsDifficulty.MOD_ID);
    }
}
