package com.majruszsdifficulty.features;

import cc.sighs.oelib.event.Subscribe;
import com.majruszsdifficulty.config.FeatureConfig;
import com.majruszsdifficulty.events.ServerEntityJoinEvent;
import com.majruszsdifficulty.gamestage.GameStageHelper;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.monster.Evoker;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class EvokerWithTotem {
    private static FeatureConfig.EnabledStageChance settings() {
        return FeatureConfig.get().evokerWithTotem();
    }

    @Subscribe
    private static void giveTotem(ServerEntityJoinEvent data) {
        if (!(data.entity instanceof Evoker) || data.isLoadedFromDisk || !settings().isEnabled()
                || !data.isAtLeast(GameStageHelper.find(settings().requiredGameStage()))
                || !data.passesChance((float) settings().chance(), settings().isScaledByCrd())) {
            return;
        }
        ((Evoker) data.entity).setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.TOTEM_OF_UNDYING));
    }
}
