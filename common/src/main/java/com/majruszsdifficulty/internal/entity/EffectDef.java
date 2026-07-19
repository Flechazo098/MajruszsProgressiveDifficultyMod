package com.majruszsdifficulty.internal.entity;

import com.majruszsdifficulty.internal.text.TextHelper;
import com.majruszsdifficulty.internal.time.TimeHelper;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;

import java.util.Optional;
import java.util.function.Supplier;

public class EffectDef {
    public static final Codec<EffectDef> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.optionalFieldOf("id").forGetter(value -> Optional.ofNullable(value.effectId)),
            Codec.intRange(0, 10).fieldOf("amplifier").orElse(0).forGetter(value -> value.amplifier),
            Codec.floatRange(1.0f, 1000.0f).fieldOf("duration").orElse(1.0f).forGetter(value -> value.duration)
    ).apply(instance, (effect, amplifier, duration) -> new EffectDef(effect.orElse(null), amplifier, duration)));
    public ResourceLocation effectId;
    public int amplifier;
    public float duration;

    public EffectDef(Supplier<? extends MobEffect> effect, int amplifier, float duration) {
        MobEffect value = effect.get();
        this.effectId = value != null ? BuiltInRegistries.MOB_EFFECT.getKey(value) : null;
        this.amplifier = amplifier;
        this.duration = duration;
    }

    public EffectDef(ResourceLocation effectId, int amplifier, float duration) {
        this.effectId = effectId;
        this.amplifier = amplifier;
        this.duration = duration;
    }

    public EffectDef() {
        this((ResourceLocation) null, 0, 1.0f);
    }

    public MobEffectInstance toEffectInstance() {
        MobEffect effect = this.getEffect();
        return new MobEffectInstance(BuiltInRegistries.MOB_EFFECT.wrapAsHolder(effect), TimeHelper.toTicks(this.duration), this.amplifier);
    }

    public MutableComponent toComponent() {
        Component effectName = this.getEffect().getDisplayName();
        Component fullName = this.amplifier > 0 ? TextHelper.translatable("potion.withAmplifier", effectName.getString(), TextHelper.toRoman(this.amplifier + 1)) : TextHelper.literal(effectName.getString());

        return TextHelper.translatable("potion.withDuration", fullName.getString(), TextHelper.toEffectDuration(this.duration));
    }

    private MobEffect getEffect() {
        return Optional.ofNullable(this.effectId)
                .flatMap(BuiltInRegistries.MOB_EFFECT::getOptional)
                .orElseThrow(() -> new IllegalStateException("Unknown mob effect in config: " + this.effectId));
    }
}
