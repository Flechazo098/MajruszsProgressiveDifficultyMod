package com.majruszsdifficulty.gamestage;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.server.level.ServerPlayer;

import java.util.Optional;

public class GameStageAdvancement extends SimpleCriterionTrigger<GameStageAdvancement.Instance> {
    @Override
    public Codec<Instance> codec() {
        return Instance.CODEC;
    }

    public void trigger(ServerPlayer player, GameStage gameStage) {
        this.trigger(player, instance -> GameStageHelper.find(instance.stageId()).getOrdinal() <= gameStage.getOrdinal());
    }

    public record Instance(Optional<ContextAwarePredicate> player,
                           String stageId) implements SimpleCriterionTrigger.SimpleInstance {
        public static final Codec<Instance> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(Instance::player),
                Codec.STRING.fieldOf("stage_id").forGetter(Instance::stageId)
        ).apply(instance, Instance::new));
    }
}
