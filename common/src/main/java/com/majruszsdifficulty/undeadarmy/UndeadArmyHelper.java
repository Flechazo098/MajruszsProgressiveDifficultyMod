package com.majruszsdifficulty.undeadarmy;

import cc.sighs.oelib.event.Subscribe;
import cc.sighs.oelib.event.events.ServerTickEvent;
import com.majruszsdifficulty.internal.entity.EntityHelper;
import com.majruszsdifficulty.internal.level.LevelHelper;
import com.majruszsdifficulty.internal.math.Random;
import com.majruszsdifficulty.internal.platform.Side;
import com.majruszsdifficulty.internal.time.TimeHelper;
import com.majruszsdifficulty.world.DifficultySavedData;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.storage.ServerLevelData;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class UndeadArmyHelper {
    private static List<UndeadArmy> UNDEAD_ARMIES = new ArrayList<>();

    public static boolean tryToSpawn(Player player) {
        return EntityHelper.isOutside(player)
                && EntityHelper.isIn(player, Level.OVERWORLD)
                && UndeadArmyHelper.tryToSpawn(UndeadArmyHelper.getAttackPosition(player), Optional.empty());
    }

    public static boolean tryToSpawn(BlockPos position, Optional<UndeadArmy.Direction> direction) {
        return UndeadArmyConfig.get().isEnabled()
                && UndeadArmyHelper.getLevel().getDifficulty() != Difficulty.PEACEFUL
                && !UndeadArmyHelper.getLevel().getGameRules().getBoolean(GameRules.RULE_DISABLE_RAIDS)
                && UndeadArmyHelper.findNearestUndeadArmy(position) == null
                && UndeadArmyHelper.setupNewArmy(position, direction);
    }

    public static @Nullable UndeadArmy findNearestUndeadArmy(BlockPos position) {
        UndeadArmy nearestArmy = null;
        double minDistance = Double.MAX_VALUE;
        for (UndeadArmy undeadArmy : UNDEAD_ARMIES) {
            if (!undeadArmy.isInRange(position)) {
                continue;
            }

            double distance = undeadArmy.distanceTo(position);
            if (distance < minDistance) {
                nearestArmy = undeadArmy;
                minDistance = distance;
            }
        }

        return nearestArmy;
    }

    public static boolean isPartOfUndeadArmy(Entity entity) {
        return UNDEAD_ARMIES.stream().anyMatch(undeadArmy -> undeadArmy.isPartOfWave(entity));
    }

    public static List<UndeadArmy> getUndeadArmies() {
        return Collections.unmodifiableList(UNDEAD_ARMIES);
    }

    public static ServerLevel getLevel() {
        return Side.getServer().overworld();
    }

    @Subscribe
    private static void tick(ServerTickEvent.Post event) {
        boolean shouldSave = !UNDEAD_ARMIES.isEmpty();
        UNDEAD_ARMIES.forEach(UndeadArmy::tick);
        boolean hasAnyArmyFinished = UNDEAD_ARMIES.removeIf(UndeadArmy::hasFinished);
        if (hasAnyArmyFinished && UNDEAD_ARMIES.isEmpty()) {
            LevelHelper.setClearWeather(UndeadArmyHelper.getLevel(), TimeHelper.toTicks(0.5));
            if (UndeadArmyHelper.getLevel().getLevelData() instanceof ServerLevelData levelData) {
                levelData.setClearWeatherTime(TimeHelper.toTicks(60.0 * 30.0));
            }
        }
        if (shouldSave || hasAnyArmyFinished) {
            DifficultySavedData.markDirty();
        }
    }

    private static boolean setupNewArmy(BlockPos position, Optional<UndeadArmy.Direction> direction) {
        UndeadArmy undeadArmy = new UndeadArmy();
        undeadArmy.start(position, direction.orElse(Random.next(UndeadArmy.Direction.values())));
        UNDEAD_ARMIES.add(undeadArmy);
        DifficultySavedData.markDirty();

        return true;
    }

    public static ListTag save() {
        ListTag list = new ListTag();
        UNDEAD_ARMIES.forEach(army -> list.add(army.save()));
        return list;
    }

    public static void load(ListTag list) {
        UNDEAD_ARMIES = new ArrayList<>();
        for (Tag value : list) {
            UNDEAD_ARMIES.add(UndeadArmy.load((CompoundTag) value));
        }
    }

    private static BlockPos getAttackPosition(Player player) {
        int x = (int) player.getX();
        int z = (int) player.getZ();

        return new BlockPos(x, player.level().getHeight(Heightmap.Types.WORLD_SURFACE, x, z), z);
    }
}
