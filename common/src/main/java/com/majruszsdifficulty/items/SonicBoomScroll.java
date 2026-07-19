package com.majruszsdifficulty.items;

import com.majruszsdifficulty.config.ItemConfig;
import com.majruszsdifficulty.internal.emitter.ParticleEmitter;
import com.majruszsdifficulty.internal.entity.EntityHelper;
import com.majruszsdifficulty.internal.math.AnyPos;
import com.majruszsdifficulty.internal.platform.Side;
import com.majruszsdifficulty.internal.text.TextHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class SonicBoomScroll extends ScrollItem {
    @Override
    protected void useScroll(ItemStack itemStack, Level level, LivingEntity entity, float useRatio) {
        super.useScroll(itemStack, level, entity, useRatio);

        if (!Side.isLogicalServer()) {
            return;
        }

        AnyPos direction = EntityHelper.getLookDirection(entity);
        ItemConfig.Scroll settings = ItemConfig.get().sonicBoomScroll();
        int attackRange = settings.attackRange().lerp(useRatio);
        for (int idx = 0; idx < attackRange; ++idx) {
            Vec3 position = direction.mul(idx).add(entity.getEyePosition(0.75f)).vec3();

            ParticleEmitter.of(ParticleTypes.SONIC_BOOM)
                    .position(position)
                    .emit(level);
            level.getEntitiesOfClass(LivingEntity.class, AABB.ofSize(position, 4.0, 4.0, 4.0), target -> !target.equals(entity))
                    .forEach(target -> {
                        Vec3 knockbackDirection = direction.mul(-1.0, 0.0, -1.0).vec3();

                        target.hurt(level.damageSources().indirectMagic(entity, entity), settings.attackDamage());
                        target.knockback(1.0, knockbackDirection.x, knockbackDirection.z);
                    });
        }
    }

    @Override
    protected SoundEvent getPrepareSound() {
        return SoundEvents.WARDEN_SONIC_CHARGE;
    }

    @Override
    protected SoundEvent getCastSound() {
        return SoundEvents.WARDEN_SONIC_BOOM;
    }

    @Override
    public void appendHoverText(ItemStack itemStack, Item.TooltipContext context, List<Component> components, TooltipFlag flag) {
        ItemConfig.Scroll settings = ItemConfig.get().sonicBoomScroll();
        List.of(
                TextHelper.translatable("majruszsdifficulty.scrolls.attack_damage", settings.attackDamage()).withStyle(ChatFormatting.DARK_GREEN),
                TextHelper.translatable("majruszsdifficulty.scrolls.attack_range", "%d-%d".formatted(settings.attackRange().from(), settings.attackRange().to()))
                        .withStyle(ChatFormatting.DARK_GREEN)
        ).forEach(components::add);
    }
}
