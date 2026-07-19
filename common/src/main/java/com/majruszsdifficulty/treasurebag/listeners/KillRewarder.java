package com.majruszsdifficulty.treasurebag.listeners;

import cc.sighs.oelib.event.Subscribe;
import com.majruszsdifficulty.events.ServerLivingEntityDamagedEvent;
import com.majruszsdifficulty.events.ServerLivingEntityDeathEvent;
import com.majruszsdifficulty.internal.entity.EntityHelper;
import com.majruszsdifficulty.internal.item.ItemHelper;
import com.majruszsdifficulty.items.TreasureBag;
import com.majruszsdifficulty.registry.ModItems;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;

public class KillRewarder {
    private static final String PLAYERS_TAG = "TreasureBagPlayersToReward";
    private static final Map<String, Supplier<TreasureBag>> REWARDS = Map.of(
            "minecraft:elder_guardian", ModItems.ELDER_GUARDIAN_TREASURE_BAG_ITEM,
            "minecraft:ender_dragon", ModItems.ENDER_DRAGON_TREASURE_BAG_ITEM,
            "minecraft:warden", ModItems.WARDEN_TREASURE_BAG_ITEM,
            "minecraft:wither", ModItems.WITHER_TREASURE_BAG_ITEM
    );

    @Subscribe
    private static void markForReward(ServerLivingEntityDamagedEvent data) {
        if (data.attacker instanceof Player
                && REWARDS.containsKey(BuiltInRegistries.ENTITY_TYPE.getKey(data.target.getType()).toString())) {
            var tag = EntityHelper.getOrCreateExtraTag(data.target);
            Set<UUID> players = readPlayers(tag);
            players.add(data.attacker.getUUID());
            writePlayers(tag, players);
        }
    }

    @Subscribe
    private static void giveTreasureBag(ServerLivingEntityDeathEvent data) {
        Supplier<TreasureBag> reward = REWARDS.get(BuiltInRegistries.ENTITY_TYPE.getKey(data.target.getType()).toString());
        if (reward == null) {
            return;
        }
        TreasureBag treasureBag = reward.get();
        readPlayers(EntityHelper.getOrCreateExtraTag(data.target)).forEach(uuid -> {
            Player player = data.getLevel().getPlayerByUUID(uuid);
            if (player != null) {
                ItemHelper.giveToPlayer(new ItemStack(treasureBag), player);
            }
        });
    }

    private static Set<UUID> readPlayers(CompoundTag tag) {
        Set<UUID> players = new HashSet<>();
        for (Tag value : tag.getList(PLAYERS_TAG, Tag.TAG_INT_ARRAY)) {
            players.add(NbtUtils.loadUUID(value));
        }
        return players;
    }

    private static void writePlayers(CompoundTag tag, Set<UUID> players) {
        ListTag values = new ListTag();
        players.forEach(uuid -> values.add(NbtUtils.createUUID(uuid)));
        tag.put(PLAYERS_TAG, values);
    }
}
