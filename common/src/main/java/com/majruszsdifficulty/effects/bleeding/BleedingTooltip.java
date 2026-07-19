package com.majruszsdifficulty.effects.bleeding;

import cc.sighs.oelib.event.EventBus;
import cc.sighs.oelib.event.Subscribe;
import com.majruszsdifficulty.effects.Bleeding;
import com.majruszsdifficulty.events.ClientItemTooltipEvent;
import com.majruszsdifficulty.events.OnBleedingTooltip;
import com.majruszsdifficulty.gamestage.GameStageHelper;
import com.majruszsdifficulty.internal.annotation.Dist;
import com.majruszsdifficulty.internal.annotation.OnlyIn;
import com.majruszsdifficulty.internal.entity.EffectDef;
import com.majruszsdifficulty.internal.platform.Side;

@OnlyIn(Dist.CLIENT)
public class BleedingTooltip {
    @Subscribe
    private static void addCustom(ClientItemTooltipEvent data) {
        if (!Bleeding.isEnabled() || Side.getLocalPlayer() == null) {
            return;
        }
        EffectDef effectDef = Bleeding.getCurrentEffect(GameStageHelper.determineGameStage(Side.getLocalPlayer()));

        EventBus.post(new OnBleedingTooltip(data, effectDef.amplifier));
    }
}
