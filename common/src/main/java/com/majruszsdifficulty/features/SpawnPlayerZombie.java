package com.majruszsdifficulty.features;

import cc.sighs.oelib.event.Subscribe;
import com.majruszsdifficulty.MajruszsDifficulty;
import com.majruszsdifficulty.config.AdvancedFeatureConfig;
import com.majruszsdifficulty.events.ServerLivingEntityDeathEvent;
import com.majruszsdifficulty.gamestage.GameStageHelper;
import com.majruszsdifficulty.internal.entity.EntityHelper;
import com.majruszsdifficulty.internal.level.LevelHelper;
import com.majruszsdifficulty.internal.math.AnyPos;
import com.majruszsdifficulty.internal.math.Random;
import com.majruszsdifficulty.registry.ModEffects;
import com.mojang.authlib.GameProfile;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ResolvableProfile;

public class SpawnPlayerZombie {
    private static AdvancedFeatureConfig.SpawnPlayerZombie settings() {
        return AdvancedFeatureConfig.get().spawnPlayerZombie();
    }

    @Subscribe
    private static void spawn(ServerLivingEntityDeathEvent data) {
        AdvancedFeatureConfig.SpawnPlayerZombie settings = settings();
        if (!settings.isEnabled()
                || !(data.target instanceof Player player)
                || (!data.target.hasEffect(MajruszsDifficulty.effectHolder(ModEffects.BLEEDING_EFFECT)) && !(data.attacker instanceof Zombie))
                || GameStageHelper.determineGameStage(data.getLevel(), data.target.position()).getOrdinal()
                < GameStageHelper.find(settings.requiredGameStage()).getOrdinal()) {
            return;
        }
        float chance = (float) settings.chance();
        if (settings.isScaledByCrd()) {
            chance *= LevelHelper.getClampedRegionalDifficultyAt(data.getLevel(), data.target.blockPosition());
        }
        if (!Random.check(chance)) {
            return;
        }
        EntityType<?> entityType = data.attacker instanceof Zombie attacker ? attacker.getType() : EntityType.ZOMBIE;
        Entity entity = EntityHelper.createSpawner(() -> entityType, data.getLevel())
                .position(AnyPos.from(player.position()).center().vec3())
                .mobSpawnType(MobSpawnType.EVENT)
                .spawn();
        if (!(entity instanceof Zombie zombie)) {
            return;
        }

        if (Random.check((float) settings.headChance())) {
            ItemStack playerSkull = new ItemStack(Items.PLAYER_HEAD);
            playerSkull.set(DataComponents.PROFILE, new ResolvableProfile(new GameProfile(player.getUUID(), player.getScoreboardName())));
            zombie.setItemSlot(EquipmentSlot.HEAD, playerSkull);
            zombie.setDropChance(EquipmentSlot.HEAD, (float) settings.headDropChance());
        }

        zombie.setCustomName(player.getName());
        zombie.setCanPickUpLoot(false);
        zombie.setPersistenceRequired();
    }

    @Subscribe
    private static void giveAdvancement(ServerLivingEntityDeathEvent data) {
        if (data.target instanceof Zombie
                && data.attacker instanceof ServerPlayer player
                && data.target.getName().equals(data.attacker.getName())) {
            MajruszsDifficulty.triggerAdvancement(player, "kill_yourself");
        }
    }
}
