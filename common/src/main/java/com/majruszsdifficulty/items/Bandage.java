package com.majruszsdifficulty.items;

import com.majruszsdifficulty.MajruszsDifficulty;
import com.majruszsdifficulty.config.ItemConfig;
import com.majruszsdifficulty.internal.emitter.SoundEmitter;
import com.majruszsdifficulty.internal.entity.EffectDef;
import com.majruszsdifficulty.internal.item.ItemHelper;
import com.majruszsdifficulty.internal.math.Random;
import com.majruszsdifficulty.internal.text.TextHelper;
import com.majruszsdifficulty.internal.time.TimeHelper;
import com.majruszsdifficulty.registry.ModEffects;
import com.majruszsdifficulty.registry.ModItems;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.function.Supplier;

public class Bandage extends Item {
    private final Supplier<List<EffectDef>> effects;

    public static Supplier<Bandage> normal() {
        return () -> new Bandage(Rarity.COMMON, () -> ItemConfig.get().bandage().normalEffects());
    }

    public static Supplier<Bandage> golden() {
        return () -> new Bandage(Rarity.UNCOMMON, () -> ItemConfig.get().bandage().goldenEffects());
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        return this.apply(itemStack, player, player)
                ? InteractionResultHolder.sidedSuccess(itemStack, level.isClientSide)
                : InteractionResultHolder.pass(itemStack);
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack itemStack, Player player, LivingEntity target, InteractionHand hand) {
        return this.apply(itemStack, player, target)
                ? InteractionResult.sidedSuccess(player.level().isClientSide)
                : InteractionResult.PASS;
    }

    private boolean apply(ItemStack itemStack, Player player, LivingEntity target) {
        if (ItemHelper.isOnCooldown(player, ModItems.BANDAGE_ITEM.get(), ModItems.GOLDEN_BANDAGE_ITEM.get())) {
            return false;
        }

        this.getEffects().forEach(effectDef -> target.addEffect(effectDef.toEffectInstance()));
        SoundEmitter.of(SoundEvents.ITEM_PICKUP)
                .volume(Random.nextFloat(0.4f, 0.6f))
                .position(target.position())
                .emit(target.level());
        Bandage.removeBleeding(this, player, target);
        ItemHelper.addCooldown(player, TimeHelper.toTicks(0.7), ModItems.BANDAGE_ITEM.get(), ModItems.GOLDEN_BANDAGE_ITEM.get());
        ItemHelper.consumeItemOnUse(itemStack, player);
        return true;
    }

    private static void removeBleeding(Bandage item, Player player, LivingEntity target) {
        if (target.hasEffect(MajruszsDifficulty.effectHolder(ModEffects.BLEEDING_EFFECT)) && player instanceof ServerPlayer serverPlayer) {
            if (target.equals(serverPlayer)) {
                MajruszsDifficulty.triggerAdvancement(serverPlayer, "bandage_used");
            } else if (item.equals(ModItems.GOLDEN_BANDAGE_ITEM.get())) {
                MajruszsDifficulty.triggerAdvancement(serverPlayer, "golden_bandage_used_on_others");
            }
        }
        target.removeEffect(MajruszsDifficulty.effectHolder(ModEffects.BLEEDING_EFFECT));
    }

    @Override
    public void appendHoverText(ItemStack itemStack, TooltipContext context, List<Component> components, TooltipFlag flag) {
        for (EffectDef effectDef : this.effects.get()) {
            components.add(effectDef.toComponent().withStyle(ChatFormatting.BLUE));
        }

        components.add(TextHelper.empty());
        components.add(TextHelper.translatable("potion.whenDrank").withStyle(ChatFormatting.DARK_PURPLE));
        components.add(TextHelper.translatable("item.majruszsdifficulty.bandage.effect").withStyle(ChatFormatting.BLUE));
    }

    private Bandage(Rarity rarity, Supplier<List<EffectDef>> effects) {
        super(new Properties().stacksTo(16).rarity(rarity));

        this.effects = effects;
    }

    private List<EffectDef> getEffects() {
        return this.effects.get();
    }
}
