package com.majruszsdifficulty.items;

import com.majruszsdifficulty.internal.emitter.SoundEmitter;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;

public class EnderPouch extends Item {
    public EnderPouch() {
        super(new Properties().stacksTo(1).rarity(Rarity.UNCOMMON));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        if (level instanceof ServerLevel serverLevel) {
            SoundEmitter.of(SoundEvents.ENDERMAN_TELEPORT)
                    .volume(SoundEmitter.randomized(0.3f))
                    .pitch(SoundEmitter.randomized(0.8f))
                    .position(player.position())
                    .emit(serverLevel);
        }

        Component component = itemStack.getItem().getDescription();
        player.openMenu(new SimpleMenuProvider((containerId, inventory, menuPlayer) -> ChestMenu.threeRows(containerId, inventory, menuPlayer.getEnderChestInventory()), component));
        player.awardStat(Stats.OPEN_ENDERCHEST);
        return InteractionResultHolder.sidedSuccess(itemStack, level.isClientSide);
    }
}
