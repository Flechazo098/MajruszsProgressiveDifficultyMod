package com.majruszsdifficulty.treasurebag;

import cc.sighs.oelib.event.Subscribe;
import com.majruszsdifficulty.events.ServerPlayerJoinedEvent;
import com.majruszsdifficulty.internal.item.LootHelper;
import com.majruszsdifficulty.items.TreasureBag;
import com.majruszsdifficulty.mixin.IMixinLootPool;
import com.majruszsdifficulty.mixin.IMixinLootPoolSingletonContainer;
import com.majruszsdifficulty.mixin.IMixinLootTable;
import com.majruszsdifficulty.network.TreasureBagProgressPacket;
import com.majruszsdifficulty.treasurebag.events.OnTreasureBagOpened;
import com.majruszsdifficulty.world.DifficultySavedData;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;

import java.util.*;

public class TreasureBagHelper {
    private static final List<TreasureBag> TREASURE_BAGS = new ArrayList<>();
    private static Map<String, PlayerProgress> PLAYERS = new HashMap<>();

    public static void register(TreasureBag treasureBag) {
        TREASURE_BAGS.add(treasureBag);
    }

    public static void unlockAll(Player player) {
        PLAYERS.get(player.getStringUUID()).treasureBags.forEach((id, bagProgress) -> {
            List<Integer> unlockedIndices = new ArrayList<>();
            for (int idx = 0; idx < bagProgress.items.size(); ++idx) {
                if (bagProgress.items.get(idx).unlock()) {
                    unlockedIndices.add(idx);
                }
            }
            TreasureBagHelper.sendToPlayer(player, ResourceLocation.parse(id), bagProgress, unlockedIndices);
        });
    }

    public static void clearProgress(Player player) {
        PLAYERS.remove(player.getStringUUID());
        TreasureBagHelper.createDefaultProgress(player);
    }

    public static void createDefaultProgress(Player player) {
        String uuid = player.getStringUUID();
        PlayerProgress playerProgress = PLAYERS.computeIfAbsent(uuid, key -> new PlayerProgress());
        LootParams params = LootHelper.toGiftParams(player);
        for (TreasureBag treasureBag : TREASURE_BAGS) {
            ResourceLocation bagId = BuiltInRegistries.ITEM.getKey(treasureBag);
            BagProgress bagProgress = playerProgress.get(treasureBag);
            if (bagProgress.items.isEmpty()) {
                TreasureBagHelper.createDefaultProgress(bagProgress, params, treasureBag.getLootId());
            }

            TreasureBagHelper.sendToPlayer(player, bagId, bagProgress);
        }
        DifficultySavedData.markDirty();
    }

    public static PlayerProgress getProgress(Player player) {
        return PLAYERS.get(player.getUUID().toString());
    }

    public static BagProgress getProgress(Player player, TreasureBag item) {
        return TreasureBagHelper.getProgress(player).get(item);
    }

    @Subscribe
    private static void createDefaultProgress(ServerPlayerJoinedEvent data) {
        TreasureBagHelper.createDefaultProgress(data.player());
    }

    @Subscribe
    private static void updateProgress(OnTreasureBagOpened data) {
        List<Integer> unlockedIndices = new ArrayList<>();
        BagProgress bagProgress = TreasureBagHelper.getProgress(data.player(), data.treasureBag());
        for (ItemStack itemStack : data.loot()) {
            for (int idx = 0; idx < bagProgress.items.size(); ++idx) {
                ItemProgress itemProgress = bagProgress.items.get(idx);
                if (itemProgress.id.equals(BuiltInRegistries.ITEM.getKey(itemStack.getItem()))) {
                    if (itemProgress.unlock()) {
                        unlockedIndices.add(idx);
                    }
                }
            }
        }

        if (!unlockedIndices.isEmpty()) {
            DifficultySavedData.markDirty();
            TreasureBagHelper.sendToPlayer(data.player(), BuiltInRegistries.ITEM.getKey(data.treasureBag()), bagProgress, unlockedIndices);
        }
    }

    private static void sendToPlayer(Player player, ResourceLocation id, BagProgress bagProgress) {
        TreasureBagHelper.sendToPlayer(player, id, bagProgress, List.of());
    }

    private static void sendToPlayer(Player player, ResourceLocation id, BagProgress bagProgress, List<Integer> unlockedIndices) {
        new TreasureBagProgressPacket(
                id,
                bagProgress.items.stream().map(ItemProgressData::from).toList(),
                List.copyOf(unlockedIndices)
        ).sendTo((ServerPlayer) player);
    }

    private static void createDefaultProgress(BagProgress bagProgress, LootParams params, ResourceLocation lootId) {
        for (LootItem lootItem : TreasureBagHelper.getLootItems(LootHelper.getLootTable(lootId))) {
            lootItem.createItemStack(itemStack -> {
                ResourceLocation itemId = TreasureBagHelper.getId(itemStack);
                if (bagProgress.items.stream().noneMatch(itemProgress -> itemProgress.id.equals(itemId))) {
                    bagProgress.items.add(new ItemProgress(itemId, false, ((IMixinLootPoolSingletonContainer) lootItem).getQuality()));
                }
            }, new LootContext.Builder(params).create(Optional.of(lootId)));
        }
        bagProgress.items.sort(Comparator.comparingInt(a -> -a.quality));
    }

    private static List<LootItem> getLootItems(LootTable lootTable) {
        return ((IMixinLootTable) lootTable).majruszsdifficulty$getPools().stream()
                .flatMap(lootPool -> ((IMixinLootPool) lootPool).getEntries().stream())
                .filter(entry -> entry instanceof LootItem)
                .map(entry -> (LootItem) entry)
                .toList();
    }

    private static ResourceLocation getId(ItemStack itemStack) {
        return itemStack.is(Items.BOOK) ? ResourceLocation.parse("minecraft:enchanted_book") : BuiltInRegistries.ITEM.getKey(itemStack.getItem());
    }

    public static CompoundTag save() {
        CompoundTag players = new CompoundTag();
        PLAYERS.forEach((playerId, progress) -> {
            CompoundTag bags = new CompoundTag();
            progress.treasureBags.forEach((bagId, bag) -> {
                ListTag items = new ListTag();
                for (ItemProgress item : bag.items) {
                    CompoundTag itemTag = new CompoundTag();
                    itemTag.putString("id", item.id.toString());
                    itemTag.putBoolean("is_unlocked", item.isUnlocked);
                    itemTag.putInt("quality", item.quality);
                    items.add(itemTag);
                }
                bags.put(bagId, items);
            });
            players.put(playerId, bags);
        });
        return players;
    }

    public static void load(CompoundTag players) {
        PLAYERS = new HashMap<>();
        for (String playerId : players.getAllKeys()) {
            PlayerProgress progress = new PlayerProgress();
            CompoundTag bags = players.getCompound(playerId);
            if (bags.contains("treasure_bags", Tag.TAG_COMPOUND)) {
                bags = bags.getCompound("treasure_bags");
            }
            for (String bagId : bags.getAllKeys()) {
                BagProgress bag = new BagProgress();
                for (Tag value : bags.getList(bagId, Tag.TAG_COMPOUND)) {
                    CompoundTag itemTag = (CompoundTag) value;
                    ResourceLocation id = ResourceLocation.tryParse(itemTag.getString("id"));
                    if (id != null) {
                        bag.items.add(new ItemProgress(id, itemTag.getBoolean("is_unlocked"), itemTag.getInt("quality")));
                    }
                }
                progress.treasureBags.put(bagId, bag);
            }
            PLAYERS.put(playerId, progress);
        }
    }

    public static class PlayerProgress {
        public Map<String, BagProgress> treasureBags = new HashMap<>();

        public BagProgress get(TreasureBag item) {
            return this.treasureBags.computeIfAbsent(BuiltInRegistries.ITEM.getKey(item).toString(), key -> new BagProgress());
        }
    }

    public static class BagProgress {
        public List<ItemProgress> items = new ArrayList<>();
    }

    public static class ItemProgress {
        public ResourceLocation id;
        public boolean isUnlocked;
        public int quality;

        public ItemProgress(ResourceLocation id, boolean isUnlocked, int quality) {
            this.id = id;
            this.isUnlocked = isUnlocked;
            this.quality = quality;
        }

        public ItemProgress() {
        }

        public boolean unlock() {
            boolean wasUnlockedAlready = this.isUnlocked;
            this.isUnlocked = true;

            return !wasUnlockedAlready;
        }
    }

    public record ItemProgressData(ResourceLocation id, boolean isUnlocked, int quality) {
        private static ItemProgressData from(ItemProgress progress) {
            return new ItemProgressData(progress.id, progress.isUnlocked, progress.quality);
        }
    }

}
