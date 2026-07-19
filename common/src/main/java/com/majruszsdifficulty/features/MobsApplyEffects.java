package com.majruszsdifficulty.features;

import cc.sighs.oelib.event.Subscribe;
import com.majruszsdifficulty.config.MobFeatureConfig;
import com.majruszsdifficulty.events.ServerLivingEntityDamagedEvent;
import com.majruszsdifficulty.gamestage.GameStage;
import com.majruszsdifficulty.gamestage.GameStageHelper;
import com.majruszsdifficulty.internal.entity.EffectDef;
import com.majruszsdifficulty.internal.level.LevelHelper;
import com.majruszsdifficulty.internal.math.Random;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;

public class MobsApplyEffects {
    @Subscribe
    private static void tryToApply(ServerLivingEntityDamagedEvent data) {
        if (data.attacker == null) {
            return;
        }
        GameStage gameStage = GameStageHelper.determineGameStage(data.getLevel(), data.target.position());
        float crd = (float) LevelHelper.getClampedRegionalDifficultyAt(data.getLevel(), data.target.blockPosition());
        for (ExtraEffectDef extraEffect : MobFeatureConfig.get().mobsApplyEffects()) {
            if (data.attacker.getType() != extraEffect.entityType) {
                continue;
            }

            if (extraEffect.requiredGameStage.getOrdinal() > gameStage.getOrdinal()) {
                continue;
            }

            if (!Random.check(extraEffect.isScaledByCrd ? extraEffect.chance * crd : extraEffect.chance)) {
                continue;
            }

            data.target.addEffect(extraEffect.effect.toEffectInstance());
        }
    }

    public static class ExtraEffectDef {
        public static final Codec<ExtraEffectDef> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.STRING.fieldOf("required_game_stage").forGetter(value -> value.requiredGameStage.getId()),
                Codec.floatRange(0.0f, 1.0f).fieldOf("chance").forGetter(value -> value.chance),
                Codec.BOOL.fieldOf("is_scaled_by_crd").forGetter(value -> value.isScaledByCrd),
                BuiltInRegistries.ENTITY_TYPE.byNameCodec().fieldOf("entity_type").forGetter(value -> value.entityType),
                EffectDef.CODEC.fieldOf("effect").forGetter(value -> value.effect)
        ).apply(instance, ExtraEffectDef::new));
        public GameStage requiredGameStage = GameStageHelper.find(GameStage.NORMAL_ID);
        public float chance;
        public boolean isScaledByCrd;
        public EntityType<?> entityType;
        public EffectDef effect;

        public ExtraEffectDef(String gameStageId, float chance, boolean isScaledByCRD, EntityType<?> entityType, EffectDef effect) {
            this.requiredGameStage = GameStageHelper.find(gameStageId);
            this.chance = chance;
            this.isScaledByCrd = isScaledByCRD;
            this.entityType = entityType;
            this.effect = effect;
        }

        public ExtraEffectDef() {
        }
    }
}
