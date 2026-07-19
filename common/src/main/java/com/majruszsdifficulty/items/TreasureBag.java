package com.majruszsdifficulty.items;

import cc.sighs.oelib.event.EventBus;
import com.majruszsdifficulty.MajruszsDifficulty;
import com.majruszsdifficulty.internal.annotation.Dist;
import com.majruszsdifficulty.internal.annotation.OnlyIn;
import com.majruszsdifficulty.internal.emitter.SoundEmitter;
import com.majruszsdifficulty.internal.item.ItemHelper;
import com.majruszsdifficulty.internal.item.LootHelper;
import com.majruszsdifficulty.internal.platform.Side;
import com.majruszsdifficulty.internal.time.TimeHelper;
import com.majruszsdifficulty.mixin.IMixinAbstractContainerScreen;
import com.majruszsdifficulty.network.TreasureBagRightClickPacket;
import com.majruszsdifficulty.treasurebag.TreasureBagHelper;
import com.majruszsdifficulty.treasurebag.events.OnTreasureBagOpened;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.function.Supplier;

public class TreasureBag extends Item {
    private final ResourceLocation lootId;

    public static Supplier<TreasureBag> angler() {
        return () -> new TreasureBag(MajruszsDifficulty.id("gameplay/treasure_bag_angler"));
    }

    public static Supplier<TreasureBag> elderGuardian() {
        return () -> new TreasureBag(MajruszsDifficulty.id("gameplay/treasure_bag_elder_guardian"));
    }

    public static Supplier<TreasureBag> enderDragon() {
        return () -> new TreasureBag(MajruszsDifficulty.id("gameplay/treasure_bag_ender_dragon"));
    }

    public static Supplier<TreasureBag> pillager() {
        return () -> new TreasureBag(MajruszsDifficulty.id("gameplay/treasure_bag_pillager"));
    }

    public static Supplier<TreasureBag> undeadArmy() {
        return () -> new TreasureBag(MajruszsDifficulty.id("gameplay/treasure_bag_undead_army"));
    }

    public static Supplier<TreasureBag> warden() {
        return () -> new TreasureBag(MajruszsDifficulty.id("gameplay/treasure_bag_warden"));
    }

    public static Supplier<TreasureBag> wither() {
        return () -> new TreasureBag(MajruszsDifficulty.id("gameplay/treasure_bag_wither"));
    }

    public TreasureBag(ResourceLocation lootId) {
        super(new Properties().stacksTo(16).rarity(Rarity.UNCOMMON));

        this.lootId = lootId;
        TreasureBagHelper.register(this);
    }

    public ResourceLocation getLootId() {
        return this.lootId;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        if (!level.isClientSide) {
            TreasureBag.open(itemStack, player);
        }
        return InteractionResultHolder.sidedSuccess(itemStack, level.isClientSide);
    }

    public static void openInInventory(int containerIndex, ServerPlayer player) {
        // delayed on purpose to avoid accessing randoms from 'network' thread
        TimeHelper.nextTick(d -> {
            if (!player.containerMenu.isValidSlotIndex(containerIndex)) {
                return;
            }

            ItemStack itemStack = player.containerMenu.getSlot(containerIndex).getItem();
            if (itemStack.getItem() instanceof TreasureBag) {
                TreasureBag.open(itemStack, player);
            }
        });
    }

    private static void open(ItemStack itemStack, Player player) {
        TreasureBag treasureBag = (TreasureBag) itemStack.getItem();
        List<ItemStack> loot = LootHelper.getLootTable(treasureBag.lootId).getRandomItems(LootHelper.toGiftParams(player));
        SoundEmitter.of(SoundEvents.ITEM_PICKUP)
                .position(player.position())
                .emit(player.level());
        EventBus.post(new OnTreasureBagOpened(player, treasureBag, loot));
        loot.forEach(reward -> ItemHelper.giveToPlayer(reward, player));
        ItemHelper.consumeItemOnUse(itemStack, player);
    }

    @OnlyIn(Dist.CLIENT)
    public static class Client {
        public static boolean handleInventoryClick(Screen screen, int button) {
            if (button != 1 || !(screen instanceof AbstractContainerScreen<?> container)) {
                return false;
            }
            Slot slot = ((IMixinAbstractContainerScreen) container).majruszsdifficulty$getHoveredSlot();
            if (slot == null || !(slot.getItem().getItem() instanceof TreasureBag) || Side.getLocalPlayer() == null) {
                return false;
            }
            int index = slot.index;
            if (index < 0 || index >= Side.getLocalPlayer().containerMenu.slots.size()
                    || !slot.getItem().equals(Side.getLocalPlayer().containerMenu.getSlot(index).getItem())) {
                index = slot.getContainerSlot();
            }
            new TreasureBagRightClickPacket(index).sendToServer();
            return true;
        }
    }
}
