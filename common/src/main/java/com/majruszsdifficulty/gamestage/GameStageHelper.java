package com.majruszsdifficulty.gamestage;

import cc.sighs.oelib.event.EventBus;
import com.majruszsdifficulty.gamestage.contexts.OnGlobalGameStageChanged;
import com.majruszsdifficulty.gamestage.contexts.OnPlayerGameStageChanged;
import com.majruszsdifficulty.internal.collection.CollectionHelper;
import com.majruszsdifficulty.internal.entity.EntityHelper;
import com.majruszsdifficulty.internal.platform.Side;
import com.majruszsdifficulty.world.DifficultySavedData;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Map;

public class GameStageHelper {
    private static GameStage GAME_STAGE = GameStageHelper.getDefaultGameStage();
    private static Map<String, GameStage> PLAYER_GAME_STAGES = new Object2ObjectOpenHashMap<>();

    public static boolean setGameStage(GameStage gameStage, Player player) {
        String uuid = EntityHelper.getPlayerUUID(player);
        if (!PLAYER_GAME_STAGES.computeIfAbsent(uuid, key -> GameStageHelper.getDefaultGameStage()).equals(gameStage)) {
            GameStage previous = PLAYER_GAME_STAGES.get(uuid);
            PLAYER_GAME_STAGES.put(uuid, gameStage);
            DifficultySavedData.markDirty();
            if (player instanceof ServerPlayer serverPlayer) {
                DifficultySavedData.sync(serverPlayer);
            }
            EventBus.post(new OnPlayerGameStageChanged(previous, gameStage, player));

            return true;
        }

        return false;
    }

    public static boolean setGlobalGameStage(GameStage gameStage) {
        if (!GAME_STAGE.equals(gameStage)) {
            GameStage previous = GAME_STAGE;
            GAME_STAGE = gameStage;
            DifficultySavedData.markDirty();
            DifficultySavedData.syncAll();
            EventBus.post(new OnGlobalGameStageChanged(previous, gameStage));

            return true;
        }

        return false;
    }

    public static boolean increaseGameStage(GameStage gameStage, Player player) {
        GameStage playerStage = GameStageHelper.getGameStage(player);

        return playerStage.getOrdinal() < gameStage.getOrdinal()
                && GameStageHelper.setGameStage(gameStage, player);
    }

    public static boolean increaseGlobalGameStage(GameStage gameStage) {
        GameStage globalStage = GameStageHelper.getGlobalGameStage();

        return globalStage.getOrdinal() < gameStage.getOrdinal()
                && GameStageHelper.setGlobalGameStage(gameStage);
    }

    public static GameStage find(String id) {
        return GameStageHelper.getGameStages().stream().filter(stage -> stage.is(id)).findFirst().orElse(GameStageHelper.getDefaultGameStage());
    }

    public static Map<String, GameStage> mapToGameStages(Map<String, String> names) {
        return CollectionHelper.map(names, GameStageHelper::find, Object2ObjectOpenHashMap::new);
    }

    public static Map<String, String> mapToNames(Map<String, GameStage> gameStages) {
        return CollectionHelper.map(gameStages, GameStage::getId, Object2ObjectOpenHashMap::new);
    }

    public static boolean isPerPlayerDifficultyEnabled() {
        return GameStageConfig.isPerPlayerDifficultyEnabled();
    }

    public static boolean isPerPlayerDifficultyDisabled() {
        return !GameStageConfig.isPerPlayerDifficultyEnabled();
    }

    public static GameStage determineGameStage(Level level, Vec3 pos) {
        if (GameStageHelper.isPerPlayerDifficultyDisabled()) {
            return GameStageHelper.getGlobalGameStage();
        }

        List<? extends Player> players = level.players();
        if (players.isEmpty()) {
            return GameStageHelper.getGlobalGameStage();
        }

        int closestPlayerIdx = 0;
        double closestPlayerDistance = players.get(0).distanceToSqr(pos);
        for (int idx = 1; idx < players.size(); ++idx) {
            double distance = players.get(idx).distanceToSqr(pos);
            if (distance < closestPlayerDistance) {
                closestPlayerIdx = idx;
                closestPlayerDistance = distance;
            }
        }

        return GameStageHelper.getGameStage(players.get(closestPlayerIdx));
    }

    public static GameStage determineGameStage(Player player) {
        return GameStageHelper.isPerPlayerDifficultyEnabled() ? GameStageHelper.getGameStage(player) : GameStageHelper.getGlobalGameStage();
    }

    public static GameStage getGameStage(Player player) {
        GameStage gameStage = PLAYER_GAME_STAGES.get(EntityHelper.getPlayerUUID(player));
        if (gameStage != null) {
            return gameStage;
        }

        return GameStageHelper.getGlobalGameStage();
    }

    public static GameStage getGlobalGameStage() {
        return GAME_STAGE;
    }

    public static GameStage getDefaultGameStage() {
        return GameStageHelper.getGameStages().get(0);
    }

    public static List<GameStage> getGameStages() {
        return GameStageConfig.getStages();
    }

    public static CompoundTag save() {
        CompoundTag tag = new CompoundTag();
        tag.putString("global_game_stage", GAME_STAGE.getId());
        CompoundTag players = new CompoundTag();
        PLAYER_GAME_STAGES.forEach((uuid, stage) -> players.putString(uuid, stage.getId()));
        tag.put("player_game_stages", players);
        return tag;
    }

    public static void load(CompoundTag tag) {
        GAME_STAGE = tag.contains("global_game_stage")
                ? GameStageHelper.find(tag.getString("global_game_stage"))
                : GameStageHelper.getDefaultGameStage();
        PLAYER_GAME_STAGES = new Object2ObjectOpenHashMap<>();
        CompoundTag players = tag.getCompound("player_game_stages");
        players.getAllKeys().forEach(uuid -> PLAYER_GAME_STAGES.put(uuid, GameStageHelper.find(players.getString(uuid))));
    }

    public static void applyClientState(String globalStage, String playerStage) {
        GAME_STAGE = GameStageHelper.find(globalStage);
        PLAYER_GAME_STAGES = new Object2ObjectOpenHashMap<>();
        Player player = Side.getLocalPlayer();
        if (player != null) {
            PLAYER_GAME_STAGES.put(EntityHelper.getPlayerUUID(player), GameStageHelper.find(playerStage));
        }
    }

    private GameStageHelper() {
    }
}
