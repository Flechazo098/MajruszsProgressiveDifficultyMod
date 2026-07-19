package com.majruszsdifficulty.undeadarmy;

import cc.sighs.oelib.event.EventBus;
import com.majruszsdifficulty.gamestage.GameStage;
import com.majruszsdifficulty.gamestage.GameStageHelper;
import com.majruszsdifficulty.internal.entity.EntityHelper;
import com.majruszsdifficulty.internal.math.AnyPos;
import com.majruszsdifficulty.internal.platform.Side;
import com.majruszsdifficulty.internal.text.TextHelper;
import com.majruszsdifficulty.internal.time.TimeHelper;
import com.majruszsdifficulty.undeadarmy.events.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.BossEvent;
import net.minecraft.world.Difficulty;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public class UndeadArmy {
    public final ServerBossEvent waveInfo = (ServerBossEvent) new ServerBossEvent(TextHelper.empty(), BossEvent.BossBarColor.WHITE, BossEvent.BossBarOverlay.NOTCHED_10).setCreateWorldFog(true);
    public final ServerBossEvent bossInfo = new ServerBossEvent(TextHelper.empty(), BossEvent.BossBarColor.RED, BossEvent.BossBarOverlay.NOTCHED_6);
    public final List<ServerPlayer> participants = new ArrayList<>();
    public List<MobInfo> mobsLeft = new ArrayList<>();
    public GameStage gameStage;
    public BlockPos position;
    public Direction direction;
    public Phase phase = new Phase();
    public int currentWave = 0;
    public Entity boss = null;
    public boolean areEntitiesLoaded = false;

    public UndeadArmy() {
        this.waveInfo.setVisible(false);
        this.bossInfo.setVisible(false);
    }

    public void start(BlockPos position, Direction direction) {
        this.gameStage = GameStageHelper.determineGameStage(this.getLevel(), position.getCenter());
        this.position = position;
        this.direction = direction;
        this.areEntitiesLoaded = true;
        this.setState(Phase.State.STARTED, 6.4f);

        EventBus.post(new OnUndeadArmyStarted(this));
    }

    public void finish() {
        this.setState(Phase.State.FINISHED, 0.0f);
    }

    public void tick() {
        if (!this.areEntitiesLoaded) {
            this.areEntitiesLoaded = this.mobsLeft.stream().allMatch(mobInfo -> mobInfo.uuid == null || EntityHelper.isLoaded(this.getLevel(), mobInfo.uuid));
            if (this.areEntitiesLoaded) {
                EventBus.post(new OnUndeadArmyLoaded(this));
            } else {
                return;
            }
        }

        if (this.getLevel().getDifficulty() == Difficulty.PEACEFUL) {
            this.finish();
            return;
        }

        EventBus.post(new OnUndeadArmyTicked(this));
    }

    public void highlight() {
        this.forEachSpawnedUndead(entity -> entity.addEffect(new MobEffectInstance(MobEffects.GLOWING, TimeHelper.toTicks(15.0), 0)));
    }

    public void killAllMobs() {
        this.forEachSpawnedUndead(Entity::kill);
        this.mobsLeft.clear();
    }

    public void setState(Phase.State state, float durationLeft) {
        this.phase.state = state;
        this.phase.ticksLeft = TimeHelper.toTicks(durationLeft);
        this.phase.ticksTotal = Math.max(this.phase.ticksLeft, 1);

        EventBus.post(new OnUndeadArmyStateChanged(this));
        if (this.phase.state == Phase.State.WAVE_PREPARING && this.currentWave > 0) {
            EventBus.post(new OnUndeadArmyWaveFinished(this));
        } else if (this.phase.state == Phase.State.UNDEAD_DEFEATED && this.isLastWave()) {
            EventBus.post(new OnUndeadArmyDefeated(this));
        }
    }

    public double distanceTo(BlockPos position) {
        return AnyPos.from(position.getCenter()).dist2d(this.position.getCenter()).doubleValue();
    }

    public boolean hasFinished() {
        return this.phase.state == Phase.State.FINISHED;
    }

    public boolean isInRange(BlockPos position) {
        return this.distanceTo(position) < UndeadArmyConfig.get().areaRadius();
    }

    public boolean isLastWave() {
        return this.currentWave == UndeadArmyConfig.get().waves().stream().filter(waveDef -> this.gameStage.getOrdinal() >= waveDef.gameStage.getOrdinal()).count();
    }

    public boolean isPartOfWave(Entity entity) {
        return this.mobsLeft.stream().anyMatch(mobInfo -> mobInfo.uuid != null && mobInfo.uuid.equals(entity.getUUID()));
    }

    private void forEachSpawnedUndead(Consumer<LivingEntity> consumer) {
        this.mobsLeft.stream()
                .map(mobInfo -> mobInfo.toEntity(this.getLevel()))
                .filter(entity -> entity != null)
                .forEach(entity -> consumer.accept((LivingEntity) entity));
    }

    public ServerLevel getLevel() {
        return Side.getServer().overworld();
    }

    public CompoundTag save() {
        CompoundTag tag = new CompoundTag();
        ListTag mobs = new ListTag();
        for (MobInfo mob : this.mobsLeft) {
            CompoundTag mobTag = new CompoundTag();
            mobTag.putString("type", BuiltInRegistries.ENTITY_TYPE.getKey(mob.type).toString());
            if (mob.equipment != null) {
                mobTag.putString("equipment", mob.equipment.toString());
            }
            mobTag.putLong("position", mob.position.asLong());
            mobTag.putBoolean("is_boss", mob.isBoss);
            if (mob.uuid != null) {
                mobTag.putUUID("uuid", mob.uuid);
            }
            mobs.add(mobTag);
        }
        tag.put("mobs_left", mobs);
        tag.putString("game_stage", this.gameStage.getId());
        tag.putLong("position", this.position.asLong());
        tag.putString("direction", this.direction.name());
        CompoundTag phaseTag = new CompoundTag();
        phaseTag.putString("state", this.phase.state.name());
        phaseTag.putInt("ticks_left", this.phase.ticksLeft);
        phaseTag.putInt("ticks_total", this.phase.ticksTotal);
        phaseTag.putInt("health_total", this.phase.healthTotal);
        tag.put("phase", phaseTag);
        tag.putInt("current_wave", this.currentWave);
        return tag;
    }

    public static UndeadArmy load(CompoundTag tag) {
        UndeadArmy army = new UndeadArmy();
        army.gameStage = GameStageHelper.find(tag.getString("game_stage"));
        army.position = readBlockPos(tag, "position");
        try {
            army.direction = Direction.valueOf(tag.getString("direction"));
        } catch (IllegalArgumentException exception) {
            army.direction = Direction.NORTH;
        }
        CompoundTag phaseTag = tag.getCompound("phase");
        try {
            army.phase.state = Phase.State.valueOf(phaseTag.getString("state"));
        } catch (IllegalArgumentException exception) {
            army.phase.state = Phase.State.CREATED;
        }
        army.phase.ticksLeft = phaseTag.getInt("ticks_left");
        army.phase.ticksTotal = Math.max(phaseTag.getInt("ticks_total"), 1);
        army.phase.healthTotal = phaseTag.getInt("health_total");
        army.currentWave = tag.getInt("current_wave");
        for (Tag value : tag.getList("mobs_left", Tag.TAG_COMPOUND)) {
            CompoundTag mobTag = (CompoundTag) value;
            ResourceLocation typeId = ResourceLocation.tryParse(mobTag.getString("type"));
            if (typeId == null) {
                continue;
            }
            EntityType<?> type = BuiltInRegistries.ENTITY_TYPE.getOptional(typeId).orElse(null);
            if (type == null) {
                continue;
            }
            MobInfo mob = new MobInfo();
            mob.type = type;
            mob.equipment = mobTag.contains("equipment") ? ResourceLocation.tryParse(mobTag.getString("equipment")) : null;
            mob.position = readBlockPos(mobTag, "position");
            mob.isBoss = mobTag.getBoolean("is_boss");
            mob.uuid = mobTag.hasUUID("uuid") ? mobTag.getUUID("uuid") : null;
            army.mobsLeft.add(mob);
        }
        return army;
    }

    private static BlockPos readBlockPos(CompoundTag tag, String key) {
        if (tag.contains(key, Tag.TAG_LONG)) {
            return BlockPos.of(tag.getLong(key));
        }
        String[] values = tag.getString(key).split(",");
        if (values.length == 3) {
            try {
                return new BlockPos(Integer.parseInt(values[0]), Integer.parseInt(values[1]), Integer.parseInt(values[2]));
            } catch (NumberFormatException ignored) {
            }
        }
        return BlockPos.ZERO;
    }

    public enum Direction {
        WEST(-1, 0),
        EAST(1, 0),
        NORTH(0, -1),
        SOUTH(0, 1);

        public final int x, z;

        Direction(int x, int z) {
            this.x = x;
            this.z = z;
        }
    }

    public static class Phase {
        public State state = State.CREATED;
        public int ticksLeft = 0;
        public int ticksTotal = 1;
        public int healthTotal = 0;

        public float getRatio() {
            return Mth.clamp(1.0f - (float) this.ticksLeft / this.ticksTotal, 0.0f, 1.0f);
        }

        public float getTicksActive() {
            return this.ticksTotal - this.ticksLeft;
        }

        public enum State {
            CREATED, STARTED, WAVE_PREPARING, WAVE_ONGOING, UNDEAD_DEFEATED, UNDEAD_WON, FINISHED
        }
    }

    public static class MobInfo {
        public EntityType<?> type;
        public ResourceLocation equipment;
        public BlockPos position;
        public boolean isBoss = false;
        public UUID uuid = null;

        public MobInfo(UndeadArmyConfig.MobDef def, BlockPos position, boolean isBoss) {
            this.type = BuiltInRegistries.ENTITY_TYPE.getOptional(def.typeId)
                    .orElseThrow(() -> new IllegalStateException("Unknown entity type in undead army config: " + def.typeId));
            this.equipment = def.equipment;
            this.position = position;
            this.isBoss = isBoss;
        }

        public MobInfo() {
        }

        public @Nullable Entity toEntity(ServerLevel level) {
            if (this.uuid == null) {
                return null;
            }

            Entity entity = level.getEntity(this.uuid);
            if (entity instanceof LivingEntity livingEntity && livingEntity.deathTime >= 20) {
                return null; // compatibility with RpgZ
            }

            return entity;
        }

        public float getHealth(ServerLevel level) {
            return this.toEntity(level) instanceof LivingEntity entity ? entity.getHealth() : 0.0f;
        }

        public float getMaxHealth(ServerLevel level) {
            return this.toEntity(level) instanceof LivingEntity entity ? entity.getMaxHealth() : 0.0f;
        }
    }
}
