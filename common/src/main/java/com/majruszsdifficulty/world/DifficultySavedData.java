package com.majruszsdifficulty.world;

import cc.sighs.oelib.event.Subscribe;
import com.majruszsdifficulty.MajruszsDifficulty;
import com.majruszsdifficulty.bloodmoon.BloodMoonHelper;
import com.majruszsdifficulty.events.ServerPlayerJoinedEvent;
import com.majruszsdifficulty.events.ServerStartedEvent;
import com.majruszsdifficulty.gamestage.GameStageHelper;
import com.majruszsdifficulty.internal.platform.Side;
import com.majruszsdifficulty.network.ClientStateSyncPacket;
import com.majruszsdifficulty.treasurebag.TreasureBagHelper;
import com.majruszsdifficulty.undeadarmy.UndeadArmyHelper;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.saveddata.SavedData;

public final class DifficultySavedData extends SavedData {
    private static final String FILE_ID = MajruszsDifficulty.MOD_ID;
    private static final SavedData.Factory<DifficultySavedData> FACTORY = new SavedData.Factory<>(
            DifficultySavedData::createDefault,
            DifficultySavedData::load,
            DataFixTypes.LEVEL
    );

    @Subscribe
    private static void onServerStarted(ServerStartedEvent event) {
        get(event.server());
    }

    @Subscribe
    private static void onPlayerJoined(ServerPlayerJoinedEvent event) {
        sync(event.player());
    }

    public static DifficultySavedData get(MinecraftServer server) {
        return server.overworld().getDataStorage().computeIfAbsent(FACTORY, FILE_ID);
    }

    public static void markDirty() {
        MinecraftServer server = Side.getServer();
        if (server != null) {
            get(server).setDirty();
        }
    }

    public static void sync(ServerPlayer player) {
        new ClientStateSyncPacket(
                BloodMoonHelper.isActive(),
                GameStageHelper.getGlobalGameStage().getId(),
                GameStageHelper.getGameStage(player).getId()
        ).sendTo(player);
    }

    public static void syncAll() {
        MinecraftServer server = Side.getServer();
        if (server != null) {
            server.getPlayerList().getPlayers().forEach(DifficultySavedData::sync);
        }
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        tag.putBoolean("blood_moon", BloodMoonHelper.isActive());
        tag.put("game_stages", GameStageHelper.save());
        tag.put("treasure_bags", TreasureBagHelper.save());
        tag.put("undead_armies", UndeadArmyHelper.save());
        return tag;
    }

    private static DifficultySavedData load(CompoundTag tag, HolderLookup.Provider registries) {
        boolean bloodMoon = tag.contains("blood_moon", Tag.TAG_COMPOUND)
                ? tag.getCompound("blood_moon").getBoolean("is_active")
                : tag.getBoolean("blood_moon");
        BloodMoonHelper.load(bloodMoon);
        CompoundTag gameStages;
        if (tag.contains("game_stages", Tag.TAG_COMPOUND)) {
            gameStages = tag.getCompound("game_stages");
        } else {
            gameStages = new CompoundTag();
            if (tag.contains("global_game_stage")) {
                gameStages.putString("global_game_stage", tag.getString("global_game_stage"));
            }
            if (tag.contains("player_game_stages", Tag.TAG_COMPOUND)) {
                gameStages.put("player_game_stages", tag.getCompound("player_game_stages").copy());
            }
        }
        GameStageHelper.load(gameStages);
        TreasureBagHelper.load(tag.getCompound("treasure_bags"));
        UndeadArmyHelper.load(tag.getList("undead_armies", Tag.TAG_COMPOUND));
        return new DifficultySavedData();
    }

    private static DifficultySavedData createDefault() {
        BloodMoonHelper.load(false);
        GameStageHelper.load(new CompoundTag());
        TreasureBagHelper.load(new CompoundTag());
        UndeadArmyHelper.load(new ListTag());
        return new DifficultySavedData();
    }
}
