package com.majruszsdifficulty.entity;

import cc.sighs.oelib.event.Subscribe;
import com.majruszsdifficulty.events.ServerExplosionDetonateEvent;
import com.majruszsdifficulty.mixin.IMixinCreeper;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.level.Level;

public class Creeperling extends Creeper {
    public static EntityType<Creeperling> createEntityType() {
        return EntityType.Builder.of(Creeperling::new, MobCategory.MONSTER)
                .sized(0.6f, 0.9f)
                .eyeHeight(0.75f)
                .build("creeperling");
    }

    public static AttributeSupplier.Builder createChildAttributes() {
        return Creeper.createAttributes()
                .add(Attributes.MAX_HEALTH, 6.0)
                .add(Attributes.MOVEMENT_SPEED, 0.35);
    }

    public Creeperling(EntityType<? extends Creeper> entityType, Level level) {
        super(entityType, level);

        ((IMixinCreeper) this).setExplosionRadius(2);
    }

    @Override
    public boolean isPowered() {
        return false; // disables charged Creeperling
    }

    @Override
    protected int getBaseExperienceReward() {
        return 3;
    }

    @Subscribe
    private static void weakenExplosion(ServerExplosionDetonateEvent data) {
        if (data.entity instanceof Creeperling) {
            data.skipBlockIf(blockPos -> true);
            data.skipEntityIf(entity -> !(entity instanceof LivingEntity));
        }
    }
}
