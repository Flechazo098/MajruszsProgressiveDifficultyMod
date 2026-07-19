package com.majruszsdifficulty.entity;

import com.majruszsdifficulty.internal.animations.Animations;
import com.majruszsdifficulty.internal.animations.AnimationsDef;
import com.majruszsdifficulty.internal.animations.IAnimableEntity;
import com.majruszsdifficulty.internal.emitter.ParticleEmitter;
import com.majruszsdifficulty.internal.emitter.SoundEmitter;
import com.majruszsdifficulty.internal.entity.EntityHelper;
import com.majruszsdifficulty.internal.level.LevelHelper;
import com.majruszsdifficulty.internal.math.AnyPos;
import com.majruszsdifficulty.internal.math.Random;
import com.majruszsdifficulty.resource.ModResources;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class Tank extends Monster implements IAnimableEntity {
    private final Animations animations = Animations.create();

    public static EntityType<Tank> createEntityType() {
        return EntityType.Builder.of(Tank::new, MobCategory.MONSTER)
                .sized(0.99f, 2.7f)
                .eyeHeight(2.35f)
                .build("tank");
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 140.0)
                .add(Attributes.MOVEMENT_SPEED, 0.25)
                .add(Attributes.ATTACK_DAMAGE, 6.0)
                .add(Attributes.FOLLOW_RANGE, 30.0)
                .add(Attributes.ATTACK_KNOCKBACK, 3.5)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.75);
    }

    public Tank(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected int getBaseExperienceReward() {
        return Random.nextInt(10, 17);
    }

    @Override
    public float maxUpStep() {
        return 1.6f;
    }

    @Override
    public void playSound(SoundEvent sound, float volume, float pitch) {
        if (!this.isSilent()) {
            SoundEmitter.of(sound)
                    .volume(SoundEmitter.randomized(volume * 1.25f))
                    .pitch(SoundEmitter.randomized(pitch * 0.75f))
                    .source(this.getSoundSource())
                    .position(this.position())
                    .emit(this.level());
        }
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.SKELETON_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.SKELETON_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.SKELETON_DEATH;
    }

    @Override
    protected void playStepSound(BlockPos blockPos, BlockState blockState) {
        this.playSound(SoundEvents.SKELETON_STEP, 0.15f, 1.0f);
    }

    @Override
    public void tick() {
        super.tick();

        if (this.isSunBurnTick()) {
            this.igniteForSeconds(8.0f);
        }
    }

    @Override
    public AnimationsDef getAnimationsDef() {
        return this.level().isClientSide ? ModResources.TANK_ANIMATIONS.get() : ModResources.TANK_LOGIC_ANIMATIONS;
    }

    @Override
    public Animations getAnimations() {
        return this.animations;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new TankMeleeAttackGoal(this));
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 1.0));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0f));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    private static class TankMeleeAttackGoal extends MeleeAttackGoal {
        private final Tank tank;

        public TankMeleeAttackGoal(Tank tank) {
            super(tank, 1.0, true);

            this.tank = tank;
        }

        @Override
        public void tick() {
            super.tick();

            this.tank.setAggressive(!this.tank.animations.isEmpty());
        }

        @Override
        protected void checkAndPerformAttack(LivingEntity target) {
            if (!this.mob.isWithinMeleeAttackRange(target) || !this.tank.animations.isEmpty()) {
                return;
            }

            this.resetAttackCooldown();
            if (Random.check(0.25f)) {
                this.useHeavyAttack(target);
            } else {
                this.useNormalAttack(target);
            }
        }

        private void useHeavyAttack(LivingEntity target) {
            this.tank.playAnimation("heavy_attack")
                    .addCallback(11, () -> {
                        if (!(target.level() instanceof ServerLevel level)) {
                            return;
                        }
                        Vec3 position = this.getHeavyAttackPosition();

                        this.spawnGroundParticles(level, position);
                        this.hitAllNearbyEntities(level, position);
                        this.playHitSound(1.5f);
                    });
        }

        private void useNormalAttack(LivingEntity target) {
            this.tank.playAnimation(Random.next("normal_attack_left", "normal_attack_right"))
                    .addCallback(9, () -> {
                        if (this.tryToHitEntity(target)) {
                            this.playHitSound(0.8f);
                        }
                    });
        }

        private void hitAllNearbyEntities(ServerLevel level, Vec3 position) {
            for (LivingEntity entity : EntityHelper.getEntitiesNearby(LivingEntity.class, level, position, 2.5)) {
                if (entity.equals(this.tank)) {
                    continue;
                }

                if (this.tryToHitEntity(entity) && entity instanceof ServerPlayer player && player.isBlocking()) {
                    player.disableShield();
                }
            }
        }

        private boolean tryToHitEntity(LivingEntity entity) {
            if (this.tank.canAttack(entity, TargetingConditions.DEFAULT)) {
                return this.tank.doHurtTarget(entity);
            }

            return false;
        }

        private Vec3 getHeavyAttackPosition() {
            return AnyPos.from(this.tank.position()).add(EntityHelper.getDirection2d(this.tank).mul(2.0)).vec3();
        }

        private void spawnGroundParticles(ServerLevel level, Vec3 position) {
            BlockState blockState = LevelHelper.findBlockPosOnGround(level, position, 5)
                    .map(blockPos -> level.getBlockState(blockPos.below()))
                    .orElse(Blocks.DIRT.defaultBlockState());

            ParticleEmitter.of(new BlockParticleOption(ParticleTypes.BLOCK, blockState))
                    .position(AnyPos.from(position).add(0.0, 0.25, 0.0).vec3())
                    .offset(() -> new Vec3(1.0, 0.25, 1.0))
                    .count(120)
                    .speed(0.5f)
                    .emit(level);
        }

        private void playHitSound(float factor) {
            this.mob.playSound(SoundEvents.SKELETON_HURT, 0.75f * factor, 0.8f / factor);
        }
    }
}
