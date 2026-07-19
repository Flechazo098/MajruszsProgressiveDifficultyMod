package com.majruszsdifficulty.items;

import cc.sighs.oelib.event.Subscribe;
import com.majruszsdifficulty.config.ItemConfig;
import com.majruszsdifficulty.events.ServerLivingEntityIncomingDamageEvent;
import com.majruszsdifficulty.internal.entity.EntityHelper;
import com.majruszsdifficulty.internal.level.LevelHelper;
import com.majruszsdifficulty.internal.math.AnyPos;
import com.majruszsdifficulty.internal.math.AnyRot;
import com.majruszsdifficulty.internal.math.Range;
import com.majruszsdifficulty.internal.text.TextHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.EvokerFangs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class EvokerFangScroll extends ScrollItem {
    @Override
    protected void useScroll(ItemStack itemStack, Level level, LivingEntity entity, float useRatio) {
        super.useScroll(itemStack, level, entity, useRatio);

        double rotation = Math.toRadians(entity.getYRot()) - Math.PI / 2.0;
        ItemConfig.Scroll settings = ItemConfig.get().evokerFangScroll();
        this.getAttackPattern(entity, settings.attackRange().lerp(useRatio))
                .forEach(spawnPoint -> {
                    EvokerFangs evokerFangs = new EvokerFangs(level, spawnPoint.pos.x, spawnPoint.pos.y, spawnPoint.pos.z, (float) rotation, spawnPoint.cooldown, entity);
                    EntityHelper.getOrCreateExtraTag(evokerFangs).putInt("MajruszsProgressiveDifficultyEvokerFangDamage", settings.attackDamage());

                    level.addFreshEntity(evokerFangs);
                });
    }

    @Override
    protected SoundEvent getPrepareSound() {
        return SoundEvents.EVOKER_PREPARE_SUMMON;
    }

    @Override
    protected SoundEvent getCastSound() {
        return SoundEvents.EVOKER_CAST_SPELL;
    }

    private List<SpawnPoint> getAttackPattern(LivingEntity entity, int attackLength) {
        List<SpawnPoint> spawnPoints = new ArrayList<>();
        AnyRot lookRotation = EntityHelper.getLookRotation(entity);
        for (int x = 0; x <= attackLength; ++x) {
            for (int z = -1; z <= 1; ++z) {
                int cooldown = Math.abs(x) + 4;
                Vec3 position = AnyPos.from(entity.position()).floor().add(AnyPos.from(x, 0, z).rot(lookRotation).round()).vec3();
                LevelHelper.findBlockPosOnGround(entity.level(), position.x, Range.of(position.y - 3, position.y + 3), position.z)
                        .ifPresent(blockPos -> spawnPoints.add(new SpawnPoint(AnyPos.from(blockPos).add(0.5, 0.0, 0.5).vec3(), cooldown)));
            }
        }

        return spawnPoints;
    }

    @Subscribe
    private static void increaseDamage(ServerLivingEntityIncomingDamageEvent data) {
        if (data.source.getDirectEntity() instanceof EvokerFangs
                && EntityHelper.getExtraTag(data.source.getDirectEntity()) != null) {
            data.damage += EntityHelper.getExtraTag(data.source.getDirectEntity()).getInt("MajruszsProgressiveDifficultyEvokerFangDamage") - data.original;
        }
    }

    @Override
    public void appendHoverText(ItemStack itemStack, Item.TooltipContext context, List<Component> components, TooltipFlag flag) {
        ItemConfig.Scroll settings = ItemConfig.get().evokerFangScroll();
        List.of(
                TextHelper.translatable("majruszsdifficulty.scrolls.attack_damage", settings.attackDamage()).withStyle(ChatFormatting.DARK_GREEN),
                TextHelper.translatable("majruszsdifficulty.scrolls.attack_range", "%d-%d".formatted(settings.attackRange().from(), settings.attackRange().to()))
                        .withStyle(ChatFormatting.DARK_GREEN)
        ).forEach(components::add);
    }

    private record SpawnPoint(Vec3 pos, int cooldown) {
    }

}
