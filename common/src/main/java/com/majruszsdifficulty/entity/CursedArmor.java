package com.majruszsdifficulty.entity;

import cc.sighs.oelib.event.Subscribe;
import com.majruszsdifficulty.MajruszsDifficulty;
import com.majruszsdifficulty.bloodmoon.BloodMoonHelper;
import com.majruszsdifficulty.config.MobFeatureConfig;
import com.majruszsdifficulty.events.ServerEntityJoinEvent;
import com.majruszsdifficulty.events.ServerLivingEntityIncomingDamageEvent;
import com.majruszsdifficulty.events.ServerLivingEntityTickEvent;
import com.majruszsdifficulty.events.ServerLootGeneratedEvent;
import com.majruszsdifficulty.internal.animations.Animations;
import com.majruszsdifficulty.internal.animations.AnimationsDef;
import com.majruszsdifficulty.internal.animations.IAnimableEntity;
import com.majruszsdifficulty.internal.emitter.ParticleEmitter;
import com.majruszsdifficulty.internal.emitter.SoundEmitter;
import com.majruszsdifficulty.internal.entity.EntityHelper;
import com.majruszsdifficulty.internal.item.EquipmentSlots;
import com.majruszsdifficulty.internal.item.ItemHelper;
import com.majruszsdifficulty.internal.item.LootHelper;
import com.majruszsdifficulty.internal.level.BlockHelper;
import com.majruszsdifficulty.internal.math.AnyPos;
import com.majruszsdifficulty.internal.math.Random;
import com.majruszsdifficulty.internal.text.TextHelper;
import com.majruszsdifficulty.internal.time.TimeHelper;
import com.majruszsdifficulty.registry.ModEntities;
import com.majruszsdifficulty.resource.ModResources;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class CursedArmor extends Monster implements IAnimableEntity {
    private static final List<LocationDef> DEFAULT_LOCATIONS = List.of(
            new LocationDef(
                    MajruszsDifficulty.id("gameplay/cursed_armor_dungeon"),
                    List.of(ResourceLocation.parse("chests/simple_dungeon")),
                    0.5f
            ),
            new LocationDef(
                    MajruszsDifficulty.id("gameplay/cursed_armor_stronghold"),
                    List.of(
                            ResourceLocation.parse("chests/stronghold_corridor"),
                            ResourceLocation.parse("chests/stronghold_crossing"),
                            ResourceLocation.parse("chests/stronghold_library")
                    ),
                    0.4f
            ),
            new LocationDef(
                    MajruszsDifficulty.id("gameplay/cursed_armor_portal"),
                    List.of(ResourceLocation.parse("chests/ruined_portal")),
                    1.0f
            ),
            new LocationDef(
                    MajruszsDifficulty.id("gameplay/cursed_armor_nether"),
                    List.of(
                            ResourceLocation.parse("chests/bastion_bridge"),
                            ResourceLocation.parse("chests/bastion_hoglin_stable"),
                            ResourceLocation.parse("chests/bastion_other"),
                            ResourceLocation.parse("chests/bastion_treasure"),
                            ResourceLocation.parse("chests/nether_bridge")
                    ),
                    0.25f
            ),
            new LocationDef(
                    MajruszsDifficulty.id("gameplay/cursed_armor_end"),
                    List.of(ResourceLocation.parse("chests/end_city_treasure")),
                    0.5f
            )
    );
    private final Animations animations = Animations.create();

    public static List<LocationDef> defaultLocations() {
        return DEFAULT_LOCATIONS;
    }

    public static EntityType<CursedArmor> createEntityType() {
        return EntityType.Builder.of(CursedArmor::new, MobCategory.MONSTER)
                .sized(0.5f, 1.9f)
                .build("cursed_armor");
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 30.0)
                .add(Attributes.MOVEMENT_SPEED, 0.23)
                .add(Attributes.ATTACK_DAMAGE, 3.0)
                .add(Attributes.FOLLOW_RANGE, 35.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.23)
                .add(Attributes.ARMOR, 4.0);
    }

    public static boolean checkMonsterSpawnRules(EntityType<? extends Monster> entityType, ServerLevelAccessor level, MobSpawnType mobSpawnType,
                                                 BlockPos blockPos, RandomSource random
    ) {
        return Monster.checkMonsterSpawnRules(entityType, level, mobSpawnType, blockPos, random) && BloodMoonHelper.isActive();
    }

    public CursedArmor(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected int getBaseExperienceReward() {
        return 7;
    }

    @Subscribe
    private static void cancelDamageWhileAssembling(ServerLivingEntityIncomingDamageEvent data) {
        if (data.target instanceof CursedArmor cursedArmor && cursedArmor.isAssembling()) {
            data.cancelDamage();
        }
    }

    @Override
    public AnimationsDef getAnimationsDef() {
        return this.level().isClientSide ? ModResources.CURSED_ARMOR_ANIMATIONS.get() : ModResources.CURSED_ARMOR_LOGIC_ANIMATIONS;
    }

    @Override
    public Animations getAnimations() {
        return this.animations;
    }

    public void assemble() {
        if (this.animations.isEmpty()) {
            this.playAnimation("assemble")
                    .addCallback(27, () -> {
                        if (this.getItemBySlot(EquipmentSlot.CHEST).getItem() instanceof ArmorItem armorItem) {
                            SoundEmitter.of(armorItem.getEquipSound().value())
                                    .source(SoundSource.HOSTILE)
                                    .position(this.position())
                                    .emit(this.level());
                        }
                    });
        }
    }

    public void equip(LocationDef locationDef) {
        LootHelper.getLootTable(locationDef.loot)
                .getRandomItems(LootHelper.toGiftParams(this))
                .forEach(itemStack -> {
                    if (itemStack.getItem() instanceof ShieldItem) {
                        this.setItemSlot(EquipmentSlot.OFFHAND, itemStack);
                    } else {
                        ItemHelper.equip(this, itemStack);
                    }
                });

        EquipmentSlots.ALL.forEach(slot -> this.setDropChance(slot, (float) MobFeatureConfig.get().cursedArmor().itemDropChance()));
    }

    public boolean isAssembling() {
        return !this.animations.isEmpty();
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new AssembleGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0, false));
        this.goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 1.0));
        this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 8.0f));
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolem.class, true));
    }

    private static Optional<LocationDef> find(ResourceLocation chestId) {
        return MobFeatureConfig.get().cursedArmor().locations().stream()
                .filter(locationDef -> locationDef.chests.stream().anyMatch(chestId::equals))
                .findFirst();
    }

    private static LocationDef getRandomLocationDef() {
        return Random.next(MobFeatureConfig.get().cursedArmor().locations());
    }

    @Subscribe
    private static void spawnCursedArmor(ServerLootGeneratedEvent data) {
        if (data.origin == null || !(BlockHelper.getEntity(data.getLevel(), data.origin) instanceof ChestBlockEntity)
                || !Random.check(CursedArmor.find(data.lootId).map(def -> def.chance).orElse(0.0f))) {
            return;
        }
        TimeHelper.nextTick(delay -> {
            CursedArmor cursedArmor = EntityHelper.createSpawner(ModEntities.CURSED_ARMOR_ENTITY, data.getLevel())
                    .position(CursedArmor.getSpawnPosition(data))
                    .beforeEvent(entity -> {
                        float yRot = BlockHelper.getState(data.getLevel(), data.origin)
                                .getOptionalValue(ChestBlock.FACING)
                                .map(Direction::toYRot)
                                .orElse(Random.nextInt(0, 4) * 90.0f);
                        entity.setYRot(yRot);
                        entity.setYHeadRot(yRot);
                        entity.setYBodyRot(yRot);
                    })
                    .spawn();
            if (cursedArmor != null) {
                cursedArmor.assemble();
                cursedArmor.equip(CursedArmor.find(data.lootId).orElseThrow());
                if (data.entity instanceof ServerPlayer player) {
                    TimeHelper.nextTick(subdelay -> player.closeContainer());
                }
            }
        });
    }

    @Subscribe
    private static void giveRandomArmor(ServerEntityJoinEvent data) {
        if (data.isLoadedFromDisk || !(data.entity instanceof CursedArmor cursedArmor)) {
            return;
        }
        TimeHelper.nextTick(delay -> {
            if (!cursedArmor.isRemoved() && cursedArmor.getArmorCoverPercentage() == 0.0f) {
                cursedArmor.assemble();
                cursedArmor.equip(CursedArmor.getRandomLocationDef());
            }
        });
    }

    @Subscribe
    private static void setCustomName(ServerEntityJoinEvent data) {
        if (data.isLoadedFromDisk || !(data.entity instanceof CursedArmor) || !Random.check(MobFeatureConfig.get().cursedArmor().customNameChance())) {
            return;
        }
        data.entity.setCustomName(TextHelper.literal(Random.next(MobFeatureConfig.get().cursedArmor().customNames())));
    }

    private static Vec3 getSpawnPosition(ServerLootGeneratedEvent data) {
        ServerLevel level = data.getServerLevel();
        Function<Float, Boolean> isAir = y -> BlockHelper.getState(level, data.origin.add(0.0, y, 0.0)).isAir();
        if (isAir.apply(1.0f) && isAir.apply(2.0f)) {
            return data.origin.add(0.0, 0.5, 0.0);
        } else {
            Vec3i offset = BlockHelper.getState(level, data.origin)
                    .getOptionalValue(ChestBlock.FACING)
                    .map(Direction::getNormal)
                    .orElse(new Vec3i(1, 0, 0));
            return data.origin.add(offset.getX(), offset.getY(), offset.getZ());
        }
    }

    @Subscribe
    private static void spawnIdleParticles(ServerLivingEntityTickEvent data) {
        if (!(data.entity() instanceof CursedArmor)
                || !TimeHelper.haveSecondsPassed(0.2f)) {
            return;
        }
        CursedArmor.spawnParticles(data, new Vec3(0.0, data.entity().getBbHeight() * 0.5, 0.0), 0.3, 1);
    }

    @Subscribe
    private static void spawnAssemblingParticles(ServerLivingEntityTickEvent data) {
        if (!(data.entity() instanceof CursedArmor cursedArmor)
                || !cursedArmor.isAssembling()
                || !TimeHelper.haveSecondsPassed(0.2f)) {
            return;
        }
        CursedArmor.spawnParticles(data, new Vec3(0.0, 0.0, 0.0), 0.6, 5);
    }

    private static void spawnParticles(ServerLivingEntityTickEvent data, Vec3 emitterOffset, double offsetMultiplier, int particlesCount) {
        ParticleEmitter.of(ParticleTypes.ENCHANT)
                .position(data.entity().position().add(emitterOffset))
                .offset(() -> AnyPos.from(data.entity().getBbWidth(), data.entity().getBbHeight(), data.entity().getBbWidth()).mul(offsetMultiplier).vec3())
                .speed(0.5f)
                .count(particlesCount)
                .emit(data.getLevel());
    }

    public static class AssembleGoal extends Goal {
        private final CursedArmor cursedArmor;

        public AssembleGoal(CursedArmor cursedArmor) {
            this.cursedArmor = cursedArmor;

            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            return this.cursedArmor.isAssembling();
        }
    }

    public static class LocationDef {
        public static final Codec<LocationDef> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ResourceLocation.CODEC.fieldOf("loot").forGetter(value -> value.loot),
                ResourceLocation.CODEC.listOf().fieldOf("chests").forGetter(value -> value.chests),
                Codec.floatRange(0.0f, 1.0f).fieldOf("chance").forGetter(value -> value.chance)
        ).apply(instance, LocationDef::new));
        public ResourceLocation loot;
        public List<ResourceLocation> chests;
        public float chance;

        public LocationDef(ResourceLocation loot, List<ResourceLocation> chests, float chance) {
            this.loot = loot;
            this.chests = chests;
            this.chance = chance;
        }

        public LocationDef() {
            this(null, List.of(), 0.0f);
        }
    }
}
