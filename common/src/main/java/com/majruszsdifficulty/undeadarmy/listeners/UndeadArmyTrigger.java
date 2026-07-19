package com.majruszsdifficulty.undeadarmy.listeners;

import cc.sighs.oelib.event.Subscribe;
import com.majruszsdifficulty.events.ServerLivingEntityDeathEvent;
import com.majruszsdifficulty.internal.entity.EntityHelper;
import com.majruszsdifficulty.internal.text.TextHelper;
import com.majruszsdifficulty.undeadarmy.UndeadArmyConfig;
import com.majruszsdifficulty.undeadarmy.UndeadArmyHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class UndeadArmyTrigger {
    public static final String PROGRESS_TAG = "UndeadArmyUndeadLeft";

    @Subscribe
    private static void update(ServerLivingEntityDeathEvent data) {
        if (UndeadArmyConfig.get().killRequirement() <= 0
                || !data.target.getType().is(EntityTypeTags.UNDEAD)
                || UndeadArmyHelper.isPartOfUndeadArmy(data.target)
                || !(data.attacker instanceof ServerPlayer player)
                || !EntityHelper.isIn(data.attacker, Level.OVERWORLD)) {
            return;
        }

        var tag = EntityHelper.getOrCreateExtraTag(player);
        int undeadLeft = tag.contains(PROGRESS_TAG) ? tag.getInt(PROGRESS_TAG) : UndeadArmyConfig.get().killRequirementFirst();
        --undeadLeft;
        if (undeadLeft <= 0 && UndeadArmyHelper.tryToSpawn(player)) {
            undeadLeft = UndeadArmyConfig.get().killRequirement();
        } else if (undeadLeft == UndeadArmyConfig.get().killRequirementWarning()) {
            player.sendSystemMessage(TextHelper.translatable("majruszsdifficulty.undead_army.warning").withStyle(ChatFormatting.DARK_PURPLE));
        }
        tag.putInt(PROGRESS_TAG, undeadLeft);
    }
}
