package com.majruszsdifficulty.items;

import com.majruszsdifficulty.internal.item.ItemHelper;
import com.majruszsdifficulty.internal.level.LevelHelper;
import com.majruszsdifficulty.internal.text.TextHelper;
import com.majruszsdifficulty.internal.time.TimeHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;

import java.util.List;

public class RecallPotion extends Item {
    public RecallPotion() {
        super(new Properties().rarity(Rarity.UNCOMMON).stacksTo(16));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        return ItemUtils.startUsingInstantly(world, player, hand);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack itemStack, Level level, LivingEntity livingEntity) {
        if (livingEntity instanceof Player player) {
            ItemHelper.consumeItemOnUse(itemStack, player);
        }

        if (livingEntity instanceof ServerPlayer player) {
            CriteriaTriggers.CONSUME_ITEM.trigger(player, itemStack);
            LevelHelper.getSpawnPoint(player).ifPresent(spawnPoint -> spawnPoint.teleport(player));
            player.addEffect(new MobEffectInstance(MobEffects.CONFUSION, TimeHelper.toTicks(7.0), 1));
        }

        return itemStack;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack itemStack) {
        return UseAnim.DRINK;
    }

    @Override
    public int getUseDuration(ItemStack itemStack, LivingEntity entity) {
        return 32;
    }

    @Override
    public void appendHoverText(ItemStack itemStack, TooltipContext context, List<Component> components, TooltipFlag flag) {
        components.add(TextHelper.empty());
        components.add(TextHelper.translatable("potion.whenDrank").withStyle(ChatFormatting.DARK_PURPLE));
        components.add(TextHelper.translatable("item.majruszsdifficulty.recall_potion.effect").withStyle(ChatFormatting.BLUE));
    }
}
