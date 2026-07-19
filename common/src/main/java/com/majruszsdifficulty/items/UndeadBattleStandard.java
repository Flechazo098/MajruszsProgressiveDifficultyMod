package com.majruszsdifficulty.items;

import com.majruszsdifficulty.internal.item.ItemHelper;
import com.majruszsdifficulty.internal.text.TextHelper;
import com.majruszsdifficulty.undeadarmy.UndeadArmyHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

public class UndeadBattleStandard extends Item {
    public UndeadBattleStandard() {
        super(new Properties().stacksTo(1).rarity(Rarity.UNCOMMON));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        if (!level.isClientSide && UndeadArmyHelper.tryToSpawn(player)) {
            ItemHelper.consumeItemOnUse(itemStack, player);
        }
        return InteractionResultHolder.sidedSuccess(itemStack, level.isClientSide);
    }

    @Override
    public void appendHoverText(ItemStack itemStack, TooltipContext context, List<Component> components, TooltipFlag flag) {
        components.add(TextHelper.translatable("item.majruszsdifficulty.undead_battle_standard.item_tooltip1").withStyle(ChatFormatting.GRAY));
        if (!flag.isAdvanced()) {
            return;
        }

        components.add(TextHelper.translatable("item.majruszsdifficulty.undead_battle_standard.item_tooltip2").withStyle(ChatFormatting.GRAY));
        components.add(TextHelper.translatable("item.majruszsdifficulty.undead_battle_standard.item_tooltip3").withStyle(ChatFormatting.GRAY));
    }
}
