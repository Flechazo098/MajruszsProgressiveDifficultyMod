package com.majruszsdifficulty.features;

import cc.sighs.oelib.event.Subscribe;
import com.majruszsdifficulty.MajruszsDifficulty;
import com.majruszsdifficulty.config.MobFeatureConfig;
import com.majruszsdifficulty.events.ServerEntityJoinEvent;
import com.majruszsdifficulty.gamestage.GameStage;
import com.majruszsdifficulty.gamestage.GameStageHelper;
import com.majruszsdifficulty.internal.command.Command;
import com.majruszsdifficulty.internal.command.CommandData;
import com.majruszsdifficulty.internal.command.IParameter;
import com.majruszsdifficulty.internal.entity.EntityHelper;
import com.majruszsdifficulty.internal.item.ItemHelper;
import com.majruszsdifficulty.internal.item.LootHelper;
import com.majruszsdifficulty.internal.level.LevelHelper;
import com.majruszsdifficulty.internal.math.AnyPos;
import com.majruszsdifficulty.internal.math.Random;
import com.majruszsdifficulty.internal.math.Range;
import com.majruszsdifficulty.internal.platform.Side;
import com.majruszsdifficulty.undeadarmy.UndeadArmyHelper;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.*;

public class MobGroups {
    private static final Map<String, GroupDef> DEFAULT_GROUPS = Map.of(
            "skeletons", new GroupDef(
                    GameStage.NORMAL_ID,
                    0.1f,
                    true,
                    List.of(
                            new LeaderDef(EntityType.SKELETON, MajruszsDifficulty.id("mob_groups/skeleton_leader"))
                    ),
                    Range.of(1, 3),
                    List.of(
                            new SidekickDef(EntityType.SKELETON, MajruszsDifficulty.id("mob_groups/skeleton_sidekick"))
                    )
            ),
            "undead", new GroupDef(
                    GameStage.NORMAL_ID,
                    0.1f,
                    true,
                    List.of(
                            new LeaderDef(EntityType.SKELETON, MajruszsDifficulty.id("undead_army/wave_3_skeleton")),
                            new LeaderDef(EntityType.STRAY, MajruszsDifficulty.id("undead_army/wave_3_skeleton")),
                            new LeaderDef(EntityType.ZOMBIE, MajruszsDifficulty.id("undead_army/wave_3_mob")),
                            new LeaderDef(EntityType.HUSK, MajruszsDifficulty.id("undead_army/wave_3_mob"))
                    ),
                    Range.of(2, 4),
                    List.of(
                            new SidekickDef(EntityType.SKELETON, MajruszsDifficulty.id("undead_army/wave_2_mob")),
                            new SidekickDef(EntityType.STRAY, MajruszsDifficulty.id("undead_army/wave_2_mob")),
                            new SidekickDef(EntityType.ZOMBIE, MajruszsDifficulty.id("undead_army/wave_2_mob")),
                            new SidekickDef(EntityType.HUSK, MajruszsDifficulty.id("undead_army/wave_2_mob"))
                    )
            ),
            "zombie_miners", new GroupDef(
                    GameStage.EXPERT_ID,
                    0.25f,
                    true,
                    List.of(
                            new LeaderDef(EntityType.ZOMBIE, MajruszsDifficulty.id("mob_groups/zombie_leader"))
                    ),
                    Range.of(1, 3),
                    List.of(
                            new SidekickDef(EntityType.ZOMBIE, MajruszsDifficulty.id("mob_groups/zombie_sidekick"))
                    )
            ),
            "piglins", new GroupDef(
                    GameStage.EXPERT_ID,
                    0.25f,
                    true,
                    List.of(
                            new LeaderDef(EntityType.PIGLIN, MajruszsDifficulty.id("mob_groups/piglin_leader"))
                    ),
                    Range.of(1, 3),
                    List.of(
                            new SidekickDef(EntityType.PIGLIN, MajruszsDifficulty.id("mob_groups/piglin_sidekick"))
                    )
            )
    );
    private static final IParameter<String> NAME = Command.string().named("name").suggests(() -> MobFeatureConfig.get().mobGroups().keySet().stream().toList());

    public static Map<String, GroupDef> defaultGroups() {
        return DEFAULT_GROUPS;
    }

    public static void registerCommand() {
        Command.create()
                .literal("summongroup")
                .hasPermission(4)
                .parameter(NAME)
                .execute(MobGroups::spawn)
                .register();
    }

    @Subscribe
    private static void tryToSpawnGroup(ServerEntityJoinEvent data) {
        if (data.isLoadedFromDisk || !(data.entity instanceof PathfinderMob leader) || MobGroups.belongsToMobGroup(data.entity)
                || (data.getLevel().equals(Side.getServer().overworld()) && UndeadArmyHelper.isPartOfUndeadArmy(data.entity))) {
            return;
        }
        GameStage gameStage = GameStageHelper.determineGameStage(data.getLevel(), data.getPosition());
        for (Map.Entry<String, GroupDef> entry : MobFeatureConfig.get().mobGroups().entrySet()) {
            String id = entry.getKey();
            if (id.equals("zombie_miners") && data.entity.position().y > 50.0f) {
                continue;
            }

            GroupDef groupDef = entry.getValue();
            if (groupDef.requiredGameStage.getOrdinal() > gameStage.getOrdinal()) {
                continue;
            }

            LeaderDef leaderDef = Random.next(groupDef.leaders.stream().filter(def -> def.type.equals(leader.getType())).toList());
            if (leaderDef == null) {
                continue;
            }

            double chance = groupDef.chance * (groupDef.isScaledByCRD ? LevelHelper.getClampedRegionalDifficultyAt(data.getLevel(), leader.blockPosition()) : 1.0);
            if (!Random.check(chance)) {
                continue;
            }

            MobGroups.markAsGroupPart(data.entity);
            MobGroups.spawn((PathfinderMob) data.entity, groupDef);
            MobGroups.giveItems(leader, leaderDef.equipment);

            break;
        }
    }

    private static void spawn(PathfinderMob leader, GroupDef groupDef) {
        Level level = leader.level();
        int count = Random.nextInt(groupDef.count);
        for (int idx = 0; idx < count; ++idx) {
            SidekickDef sidekickDef = Random.next(groupDef.sidekicks);
            Entity entity = EntityHelper.createSpawner(() -> sidekickDef.type, level)
                    .position(MobGroups.getRandomizedPosition(level, leader.position()))
                    .mobSpawnType(MobSpawnType.EVENT)
                    .beforeEvent(MobGroups::markAsGroupPart)
                    .spawn();
            if (!(entity instanceof PathfinderMob sidekick)) {
                continue;
            }

            MobGroups.addSidekickGoals(sidekick, leader);
            MobGroups.giveItems(sidekick, sidekickDef.equipment);
        }
    }

    private static void giveItems(PathfinderMob mob, ResourceLocation id) {
        LootHelper.getLootTable(id)
                .getRandomItems(LootHelper.toGiftParams(mob))
                .forEach(itemStack -> ItemHelper.equip(mob, itemStack));

        Arrays.stream(EquipmentSlot.values())
                .forEach(slot -> mob.setDropChance(slot, 0.05f));
    }

    private static void addSidekickGoals(PathfinderMob sidekick, PathfinderMob leader) {
        EntityHelper.getGoalSelector(sidekick).addGoal(1, new FollowGroupLeaderGoal(sidekick, leader, 1.0, 6.0f, 5.0f));
        EntityHelper.getTargetSelector(sidekick).addGoal(1, new TargetAsLeaderGoal(sidekick, leader));
    }

    private static Vec3 getRandomizedPosition(Level level, Vec3 position) {
        for (int idx = 0; idx < 3; ++idx) {
            Vec3 newPosition = AnyPos.from(position).add(Random.nextInt(-3, 4), 0.0, Random.nextInt(-3, 4)).vec3();
            Optional<BlockPos> spawnPoint = LevelHelper.findBlockPosOnGround(level, newPosition.x, Range.of(newPosition.y - 3, newPosition.y + 3), newPosition.z);
            if (spawnPoint.isPresent()) {
                return AnyPos.from(spawnPoint.get()).add(0.5, 0.0, 0.5).vec3();
            }
        }

        return position;
    }

    private static void markAsGroupPart(Entity entity) {
        EntityHelper.getOrCreateExtraTag(entity).putBoolean("belongs_to_mob_group", true);
    }

    private static boolean belongsToMobGroup(Entity entity) {
        CompoundTag tag = EntityHelper.getExtraTag(entity);

        return tag != null && tag.getBoolean("belongs_to_mob_group");
    }

    private static int spawn(CommandData data) throws CommandSyntaxException {
        if (!(data.getCaller() instanceof Player player)) {
            return -1;
        }

        String name = data.get(NAME);
        GroupDef groupDef = MobFeatureConfig.get().mobGroups().get(name);
        if (groupDef == null) {
            return -1;
        }

        LeaderDef leaderDef = Random.next(groupDef.leaders);
        Entity entity = EntityHelper.createSpawner(() -> leaderDef.type, player.level())
                .position(player.position())
                .mobSpawnType(MobSpawnType.COMMAND)
                .spawn();
        if (!(entity instanceof PathfinderMob leader)) {
            return -1;
        }

        MobGroups.spawn(leader, groupDef);
        MobGroups.giveItems(leader, leaderDef.equipment);

        return 0;
    }

    public static class TargetAsLeaderGoal extends TargetGoal {
        private static final TargetingConditions CONDITIONS = TargetingConditions.forCombat().ignoreLineOfSight().ignoreInvisibilityTesting();
        private final PathfinderMob leader;

        public TargetAsLeaderGoal(PathfinderMob sidekick, PathfinderMob leader) {
            super(sidekick, false);

            this.leader = leader;
            this.setFlags(EnumSet.of(Flag.TARGET));
        }

        @Override
        public boolean canUse() {
            return this.leader != null
                    && this.leader.isAlive()
                    && this.canAttack(this.leader.getTarget(), CONDITIONS)
                    && this.leader.getTarget() != this.mob.getTarget();
        }

        @Override
        public void start() {
            this.mob.setTarget(this.leader.getTarget());
            this.targetMob = this.leader.getTarget();
            this.unseenMemoryTicks = 300;

            super.start();
        }
    }

    public static class FollowGroupLeaderGoal extends Goal {
        private final Mob sidekick;
        private final Mob leader;
        private final double speedModifier;
        private final float maxDistanceFromLeader;
        private final float stopDistance;
        private final PathNavigation navigation;
        private int ticksToRecalculatePath;

        public FollowGroupLeaderGoal(Mob sidekick, Mob leader, double speedModifier, float maxDistanceFromLeader, float stopDistance) {
            this.sidekick = sidekick;
            this.leader = leader;
            this.navigation = sidekick.getNavigation();
            this.speedModifier = speedModifier;
            this.maxDistanceFromLeader = maxDistanceFromLeader;
            this.stopDistance = stopDistance;
            this.ticksToRecalculatePath = 0;

            this.setFlags(EnumSet.of(Flag.MOVE, Flag.JUMP));
        }

        @Override
        public boolean canUse() {
            return this.leader != null
                    && this.leader.isAlive()
                    && this.leader.distanceTo(this.sidekick) >= this.maxDistanceFromLeader
                    && this.sidekick.getTarget() == null;
        }

        @Override
        public void tick() {
            if (this.leader == null || --this.ticksToRecalculatePath > 0) {
                return;
            }

            this.sidekick.getLookControl().setLookAt(this.leader, 10.0F, (float) this.sidekick.getHeadRotSpeed());
            this.ticksToRecalculatePath = 20;
            this.navigation.moveTo(this.leader, this.speedModifier);
        }

        @Override
        public boolean canContinueToUse() {
            return this.leader != null
                    && !this.navigation.isDone()
                    && this.sidekick.distanceTo(this.leader) > this.stopDistance;
        }

        @Override
        public void start() {
            this.ticksToRecalculatePath = 0;
        }
    }

    public static class GroupDef {
        private static final Codec<Range<Integer>> COUNT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.intRange(1, 10).fieldOf("min").forGetter(range -> range.from),
                Codec.intRange(1, 10).fieldOf("max").forGetter(range -> range.to)
        ).apply(instance, Range::validated));
        public static final Codec<GroupDef> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.STRING.fieldOf("required_game_stage").forGetter(value -> value.requiredGameStage.getId()),
                Codec.floatRange(0.0f, 1.0f).fieldOf("chance").forGetter(value -> value.chance),
                Codec.BOOL.fieldOf("is_scaled_by_crd").forGetter(value -> value.isScaledByCRD),
                LeaderDef.CODEC.listOf().fieldOf("leader_types").forGetter(value -> value.leaders),
                COUNT_CODEC.fieldOf("sidekicks_count").forGetter(value -> value.count),
                SidekickDef.CODEC.listOf().fieldOf("sidekick_types").forGetter(value -> value.sidekicks)
        ).apply(instance, GroupDef::new));
        public GameStage requiredGameStage = GameStageHelper.find(GameStage.NORMAL_ID);
        public float chance = 0.0f;
        public boolean isScaledByCRD = false;
        public List<LeaderDef> leaders = List.of();
        public Range<Integer> count = Range.of(1, 10);
        public List<SidekickDef> sidekicks = List.of();

        public GroupDef(String gameStageId, float chance, boolean isScaledByCRD, List<LeaderDef> leaders, Range<Integer> count,
                        List<SidekickDef> sidekicks
        ) {
            this.requiredGameStage = GameStageHelper.find(gameStageId);
            this.chance = chance;
            this.isScaledByCRD = isScaledByCRD;
            this.leaders = leaders;
            this.count = count;
            this.sidekicks = sidekicks;
        }

        public GroupDef() {
        }
    }

    public static class LeaderDef {
        public static final Codec<LeaderDef> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                BuiltInRegistries.ENTITY_TYPE.byNameCodec().fieldOf("type").forGetter(value -> value.type),
                ResourceLocation.CODEC.fieldOf("equipment").forGetter(value -> value.equipment)
        ).apply(instance, LeaderDef::new));
        public EntityType<?> type;
        public ResourceLocation equipment;

        public LeaderDef(EntityType<?> type, ResourceLocation equipment) {
            this.type = type;
            this.equipment = equipment;
        }

        public LeaderDef() {
        }
    }

    public static class SidekickDef {
        public static final Codec<SidekickDef> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                BuiltInRegistries.ENTITY_TYPE.byNameCodec().fieldOf("type").forGetter(value -> value.type),
                ResourceLocation.CODEC.fieldOf("equipment").forGetter(value -> value.equipment)
        ).apply(instance, SidekickDef::new));
        public EntityType<?> type;
        public ResourceLocation equipment;

        public SidekickDef(EntityType<?> type, ResourceLocation equipment) {
            this.type = type;
            this.equipment = equipment;
        }

        public SidekickDef() {
        }
    }

}
