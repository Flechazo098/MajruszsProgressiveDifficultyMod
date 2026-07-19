package com.majruszsdifficulty.features;

import cc.sighs.oelib.event.Subscribe;
import com.majruszsdifficulty.config.GameplayConfig;
import com.majruszsdifficulty.events.ServerEntityJoinEvent;
import com.majruszsdifficulty.gamestage.GameStage;
import com.majruszsdifficulty.gamestage.GameStageHelper;
import com.majruszsdifficulty.gamestage.GameStageValue;
import com.majruszsdifficulty.internal.entity.AttributeHandler;
import com.majruszsdifficulty.internal.text.RegexString;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class MobsSpawnStronger {
    private static final AttributeHandler HEALTH = new AttributeHandler("progressive_difficulty_health_bonus", () -> Attributes.MAX_HEALTH.value(), AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
    private static final AttributeHandler DAMAGE = new AttributeHandler("progressive_difficulty_damage_bonus", () -> Attributes.ATTACK_DAMAGE.value(), AttributeModifier.Operation.ADD_MULTIPLIED_BASE);

    @Subscribe
    private static void boost(ServerEntityJoinEvent data) {
        GameplayConfig.MobsSpawnStronger settings = GameplayConfig.get().mobsSpawnStronger();
        if (data.isLoadedFromDisk || !settings.isEnabled() || !(data.entity instanceof Mob mob) || !DAMAGE.hasAttribute(mob)
                || settings.excludedMobs().stream().map(RegexString::new).anyMatch(id -> id.matches(BuiltInRegistries.ENTITY_TYPE.getKey(data.entity.getType()).toString()))) {
            return;
        }
        GameStage gameStage = GameStageHelper.determineGameStage(data.getLevel(), data.getPosition());

        HEALTH.setValue(GameStageValue.of(settings.healthBonus()).get(gameStage)).apply(mob);
        DAMAGE.setValue(GameStageValue.of(settings.damageBonus()).get(gameStage)).apply(mob);
        mob.setHealth(mob.getMaxHealth());
    }
}
