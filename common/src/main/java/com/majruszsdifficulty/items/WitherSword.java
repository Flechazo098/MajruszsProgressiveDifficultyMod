package com.majruszsdifficulty.items;

import cc.sighs.oelib.event.Subscribe;
import com.majruszsdifficulty.config.ItemConfig;
import com.majruszsdifficulty.events.ServerLivingEntityDamagedEvent;
import com.majruszsdifficulty.internal.text.TextHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;

import java.util.List;

public class WitherSword extends SwordItem {
    public WitherSword() {
        super(CustomItemTier.WITHER, new Properties().attributes(SwordItem.createAttributes(CustomItemTier.WITHER, 3, -2.4f)).rarity(Rarity.UNCOMMON));
    }

    @Subscribe
    private static void apply(ServerLivingEntityDamagedEvent data) {
        if (data.source.isDirect()
                && data.attacker != null
                && data.attacker.getMainHandItem().getItem() instanceof WitherSword) {
            data.target.addEffect(ItemConfig.get().witherSword().effect().toEffectDef().toEffectInstance());
        }
    }

    @Override
    public void appendHoverText(ItemStack itemStack, Item.TooltipContext context, List<Component> components, TooltipFlag flag) {
        components.add(TextHelper.translatable("item.majruszsdifficulty.wither_sword.effect", TextHelper.percent(1.0f), TextHelper.toRoman(ItemConfig.get().witherSword().effect().amplifier() + 1))
                .withStyle(ChatFormatting.DARK_GREEN)
        );
    }
}
