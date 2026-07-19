package com.majruszsdifficulty.internal.modhelper;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.server.level.ServerPlayer;

import java.util.Optional;

public final class BasicAdvancement extends SimpleCriterionTrigger<BasicAdvancement.Instance> {
    @Override
    public Codec<Instance> codec() {
        return Instance.CODEC;
    }

    public void trigger(ServerPlayer player, String advancementId) {
        this.trigger(player, instance -> instance.type().equals(advancementId));
    }

    public record Instance(Optional<ContextAwarePredicate> player, String type) implements SimpleInstance {
        public static final Codec<Instance> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(Instance::player),
                Codec.STRING.fieldOf("type").forGetter(Instance::type)
        ).apply(instance, Instance::new));
    }
}
