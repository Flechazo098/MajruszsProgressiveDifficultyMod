package com.majruszsdifficulty.internal.animations;

import com.majruszsdifficulty.internal.time.TimeHelper;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.ExtraCodecs;
import org.joml.Vector3f;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.function.Supplier;

public class AnimationsDef {
    private static final Codec<Easing> EASING_CODEC = Codec.STRING.xmap(Easing::fromId, Easing::toString);
    private static final Codec<RotationDef> ROTATION_CODEC = vectorCodec(RotationDef::new, new Vector3f());
    private static final Codec<VectorDef> POSITION_CODEC = vectorCodec(VectorDef::new, new Vector3f());
    private static final Codec<ScaleDef> SCALE_CODEC = vectorCodec(ScaleDef::new, new Vector3f(1.0f));
    private static final Codec<TreeMap<Float, RotationDef>> ROTATIONS_CODEC = timelineCodec(ROTATION_CODEC);
    private static final Codec<TreeMap<Float, VectorDef>> POSITIONS_CODEC = timelineCodec(POSITION_CODEC);
    private static final Codec<TreeMap<Float, ScaleDef>> SCALES_CODEC = timelineCodec(SCALE_CODEC);
    private static final Codec<BoneDef> BONE_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ROTATIONS_CODEC.optionalFieldOf("rotation", new TreeMap<>()).forGetter(value -> value.rotations),
            POSITIONS_CODEC.optionalFieldOf("position", new TreeMap<>()).forGetter(value -> value.positions),
            SCALES_CODEC.optionalFieldOf("scale", new TreeMap<>()).forGetter(value -> value.scales)
    ).apply(instance, BoneDef::new));
    private static final Codec<AnimationDef> ANIMATION_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.BOOL.optionalFieldOf("loop", false).forGetter(value -> value.isLooped),
            Codec.FLOAT.optionalFieldOf("animation_length", 1.0f).forGetter(value -> (float) TimeHelper.toSeconds(value.ticks)),
            Codec.unboundedMap(Codec.STRING, BONE_CODEC).optionalFieldOf("bones", Map.of()).forGetter(value -> value.bones)
    ).apply(instance, AnimationDef::new));
    public static final Codec<AnimationsDef> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.unboundedMap(Codec.STRING, ANIMATION_CODEC).fieldOf("animations").forGetter(value -> value.animations)
    ).apply(instance, AnimationsDef::new));

    public Map<String, AnimationDef> animations = new HashMap<>();

    public AnimationsDef() {
    }

    public static AnimationsDef logical(Map<String, Float> lengths) {
        Map<String, AnimationDef> animations = new HashMap<>();
        lengths.forEach((name, length) -> animations.put(name, new AnimationDef(false, length, Map.of())));
        return new AnimationsDef(animations);
    }

    private AnimationsDef(Map<String, AnimationDef> animations) {
        this.animations = new HashMap<>(animations);
    }

    public static class AnimationDef {
        public boolean isLooped = false;
        public int ticks = 20;
        public Map<String, BoneDef> bones = new HashMap<>();

        public AnimationDef() {
        }

        private AnimationDef(boolean isLooped, float length, Map<String, BoneDef> bones) {
            this.isLooped = isLooped;
            this.ticks = TimeHelper.toTicks(length);
            this.bones = new HashMap<>(bones);
        }
    }

    public static class BoneDef {
        public TreeMap<Float, RotationDef> rotations = new TreeMap<>();
        public TreeMap<Float, VectorDef> positions = new TreeMap<>();
        public TreeMap<Float, ScaleDef> scales = new TreeMap<>();

        public BoneDef() {
        }

        private BoneDef(TreeMap<Float, RotationDef> rotations, TreeMap<Float, VectorDef> positions, TreeMap<Float, ScaleDef> scales) {
            this.rotations = rotations;
            this.positions = positions;
            this.scales = scales;
        }
    }

    public static class RotationDef extends VectorDef {
    }

    public static class VectorDef {
        public Vector3f vector = new Vector3f(0.0f, 0.0f, 0.0f);
        public Easing easing = Easing.LINEAR;
    }

    public static class ScaleDef extends VectorDef {
        public ScaleDef() {
            this.vector = new Vector3f(1.0f, 1.0f, 1.0f);
        }
    }

    public enum Easing {
        LINEAR("linear", x -> x),
        EASEOUTQUAD("easeOutQuad", x -> 1.0f - (1.0f - x) * (1.0f - x)),
        EASEINQUAD("easeInQuad", x -> x * x);

        final String id;
        final Function<Float, Float> mapper;

        Easing(String id, Function<Float, Float> mapper) {
            this.id = id;
            this.mapper = mapper;
        }

        @Override
        public String toString() {
            return this.id;
        }

        public float apply(float ratio) {
            return this.mapper.apply(ratio);
        }

        private static Easing fromId(String id) {
            for (Easing easing : values()) {
                if (easing.id.equals(id)) {
                    return easing;
                }
            }
            return LINEAR;
        }
    }

    private static <Type extends VectorDef> Codec<Type> vectorCodec(Supplier<Type> factory, Vector3f defaultVector) {
        return RecordCodecBuilder.create(instance -> instance.group(
                ExtraCodecs.VECTOR3F.optionalFieldOf("vector", defaultVector).forGetter(value -> value.vector),
                EASING_CODEC.optionalFieldOf("easing", Easing.LINEAR).forGetter(value -> value.easing)
        ).apply(instance, (vector, easing) -> {
            Type value = factory.get();
            value.vector = new Vector3f(vector);
            value.easing = easing;
            return value;
        }));
    }

    private static <Type> Codec<TreeMap<Float, Type>> timelineCodec(Codec<Type> valueCodec) {
        return Codec.unboundedMap(Codec.STRING, valueCodec).xmap(values -> {
            TreeMap<Float, Type> timeline = new TreeMap<>();
            values.forEach((timestamp, value) -> timeline.put(Float.parseFloat(timestamp), value));
            return timeline;
        }, values -> {
            Map<String, Type> timeline = new HashMap<>();
            values.forEach((timestamp, value) -> timeline.put(timestamp.toString(), value));
            return timeline;
        });
    }
}
