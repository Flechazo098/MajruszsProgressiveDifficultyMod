package com.majruszsdifficulty.config;

import cc.sighs.oelib.config.ConfigSchema;
import cc.sighs.oelib.config.ConfigUnit;
import cc.sighs.oelib.config.codecs.ConfigMetaCodec;
import cc.sighs.oelib.config.field.ConfigField;
import cc.sighs.oelib.config.model.ConfigStorageFormat;
import com.majruszsdifficulty.MajruszsDifficulty;
import com.majruszsdifficulty.internal.entity.EffectDef;
import com.majruszsdifficulty.registry.ModEffects;
import net.minecraft.resources.ResourceLocation;

import java.lang.invoke.MethodHandles;
import java.util.List;

public record ItemConfig(Bandage bandage, Scroll evokerFangScroll, Scroll sonicBoomScroll, WitherSword witherSword) {
    public static final ConfigUnit<ItemConfig> UNIT = ConfigSchema.defineServer(
            MethodHandles.lookup(),
            ResourceLocation.fromNamespaceAndPath(MajruszsDifficulty.MOD_ID, "items"),
            ItemConfig.class,
            meta -> meta.directory(MajruszsDifficulty.MOD_ID).fileName("items").format(ConfigStorageFormat.JSON),
            schema -> schema.group(
                    ConfigSchema.record("bandage", Bandage.class, bandageCodec(), ItemConfig::bandage),
                    ConfigSchema.record("evoker_fang_scroll", Scroll.class, scroll(12, new IntRange(8, 20)), ItemConfig::evokerFangScroll),
                    ConfigSchema.record("sonic_boom_scroll", Scroll.class, scroll(14, new IntRange(12, 30)), ItemConfig::sonicBoomScroll),
                    ConfigSchema.record("wither_sword", WitherSword.class, witherSwordCodec(), ItemConfig::witherSword)
            ).apply(schema, ItemConfig::new)
    );

    private static ConfigMetaCodec<Bandage> bandageCodec() {
        return ConfigSchema.metaCodec(Bandage.class, schema -> schema.group(
                ConfigField.list("normal_effects", EffectDef.CODEC)
                        .tooltip().defaultValue(List.of(new EffectDef(ModEffects.GLASS_REGENERATION_EFFECT.id(), 0, 20.0f)))
                        .forGetter(Bandage::normalEffects),
                ConfigField.list("golden_effects", EffectDef.CODEC)
                        .tooltip().defaultValue(List.of(
                                new EffectDef(ModEffects.GLASS_REGENERATION_EFFECT.id(), 1, 20.0f),
                                new EffectDef(ModEffects.BLEEDING_IMMUNITY_EFFECT.id(), 0, 90.0f)
                        )).forGetter(Bandage::goldenEffects)
        ).apply(schema, Bandage::new));
    }

    private static ConfigMetaCodec<WitherSword> witherSwordCodec() {
        return ConfigSchema.metaCodec(WitherSword.class, schema -> schema.group(
                ConfigSchema.record("effect", EffectSettings.class, effectSettingsCodec(), WitherSword::effect)
        ).apply(schema, WitherSword::new));
    }

    private static ConfigMetaCodec<EffectSettings> effectSettingsCodec() {
        return ConfigSchema.metaCodec(EffectSettings.class, schema -> schema.group(
                ConfigField.string("id").tooltip().defaultValue("minecraft:wither").forGetter(EffectSettings::id),
                ConfigField.intRange("amplifier", 0, 10).tooltip().defaultValue(1).forGetter(EffectSettings::amplifier),
                ConfigField.doubleRange("duration", 1.0, 1000.0).tooltip().defaultValue(6.0).forGetter(EffectSettings::duration)
        ).apply(schema, EffectSettings::new));
    }

    private static ConfigMetaCodec<Scroll> scroll(int damage, IntRange range) {
        return ConfigSchema.metaCodec(Scroll.class, schema -> schema.group(
                ConfigField.intRange("attack_damage", 1, 100).tooltip().defaultValue(damage).forGetter(Scroll::attackDamage),
                ConfigSchema.record("attack_range", IntRange.class, intRange(range), Scroll::attackRange)
        ).apply(schema, Scroll::new));
    }

    private static ConfigMetaCodec<IntRange> intRange(IntRange defaults) {
        return ConfigSchema.metaCodec(IntRange.class, schema -> schema.group(
                ConfigField.intRange("from", 1, 100).tooltip().defaultValue(defaults.from()).forGetter(IntRange::from),
                ConfigField.intRange("to", 1, 100).tooltip().defaultValue(defaults.to()).forGetter(IntRange::to)
        ).apply(schema, IntRange::new));
    }

    public static ItemConfig get() {
        return UNIT.get();
    }

    public record Bandage(List<EffectDef> normalEffects, List<EffectDef> goldenEffects) {
    }

    public record IntRange(int from, int to) {
        public int lerp(float ratio) {
            return Math.round(from + (to - from) * ratio);
        }
    }

    public record Scroll(int attackDamage, IntRange attackRange) {
    }

    public record EffectSettings(String id, int amplifier, double duration) {
        public EffectDef toEffectDef() {
            ResourceLocation location = ResourceLocation.tryParse(this.id);
            return new EffectDef(location, this.amplifier, (float) this.duration);
        }
    }

    public record WitherSword(EffectSettings effect) {
    }
}
