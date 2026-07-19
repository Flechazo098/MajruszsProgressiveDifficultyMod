package com.majruszsdifficulty.bloodmoon;

import cc.sighs.oelib.config.ConfigSchema;
import cc.sighs.oelib.config.ConfigUnit;
import cc.sighs.oelib.config.field.ConfigField;
import cc.sighs.oelib.config.model.ConfigStorageFormat;
import com.majruszsdifficulty.MajruszsDifficulty;
import com.majruszsdifficulty.internal.math.Range;
import net.minecraft.resources.ResourceLocation;

import java.lang.invoke.MethodHandles;

public record BloodMoonConfig(
        boolean isEnabled,
        double nightTriggerChance,
        double spawnRateMultiplier,
        double crdPenalty
) {
    public static final Range<Long> TIME = Range.of(12300L, 23600L);

    public static final ConfigUnit<BloodMoonConfig> UNIT = ConfigSchema.defineServer(
            MethodHandles.lookup(),
            ResourceLocation.fromNamespaceAndPath(MajruszsDifficulty.MOD_ID, "blood_moon"),
            BloodMoonConfig.class,
            meta -> meta.directory(MajruszsDifficulty.MOD_ID).fileName("blood_moon").format(ConfigStorageFormat.JSON),
            schema -> schema.group(
                    ConfigField.bool("is_enabled").tooltip().defaultValue(false).forGetter(BloodMoonConfig::isEnabled),
                    ConfigField.doubleRange("night_trigger_chance", 0.0, 1.0).tooltip().defaultValue(0.0666).forGetter(BloodMoonConfig::nightTriggerChance),
                    ConfigField.doubleRange("spawn_rate_multiplier", 1.0, 10.0).tooltip().defaultValue(2.0).forGetter(BloodMoonConfig::spawnRateMultiplier),
                    ConfigField.doubleRange("crd_penalty", 0.0, 1.0).tooltip().defaultValue(0.5).forGetter(BloodMoonConfig::crdPenalty)
            ).apply(schema, BloodMoonConfig::new)
    );

    public static BloodMoonConfig get() {
        return UNIT.get();
    }
}
