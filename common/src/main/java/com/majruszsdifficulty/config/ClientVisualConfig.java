package com.majruszsdifficulty.config;

import cc.sighs.oelib.config.ConfigSchema;
import cc.sighs.oelib.config.ConfigUnit;
import cc.sighs.oelib.config.field.ConfigField;
import cc.sighs.oelib.config.model.ConfigStorageFormat;
import com.majruszsdifficulty.MajruszsDifficulty;

import java.lang.invoke.MethodHandles;

public record ClientVisualConfig(boolean usePostProcessingBlood) {
    public static final ConfigUnit<ClientVisualConfig> UNIT = ConfigSchema.defineClient(
            MethodHandles.lookup(),
            MajruszsDifficulty.id("client_visuals"),
            ClientVisualConfig.class,
            meta -> meta.directory(MajruszsDifficulty.MOD_ID).fileName("client_visuals").format(ConfigStorageFormat.JSON),
            schema -> schema.group(
                    ConfigField.bool("use_post_processing_blood")
                            .tooltip()
                            .defaultValue(false)
                            .forGetter(ClientVisualConfig::usePostProcessingBlood)
            ).apply(schema, ClientVisualConfig::new)
    );

    public static ClientVisualConfig get() {
        return UNIT.get();
    }
}
