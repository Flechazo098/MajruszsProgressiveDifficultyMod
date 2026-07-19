package com.majruszsdifficulty.resource;

import com.majruszsdifficulty.MajruszsDifficulty;
import com.majruszsdifficulty.internal.animations.AnimationsDef;
import com.majruszsdifficulty.effects.bleeding.BleedingPostProcessor;
import net.minecraft.server.packs.resources.ResourceManager;

import java.util.List;
import java.util.Map;

public final class ModResources {
    public static final AnimationsDef CERBERUS_LOGIC_ANIMATIONS = AnimationsDef.logical(Map.of("bite", 0.4f, "fire_breath", 1.2f));
    public static final AnimationsDef CURSED_ARMOR_LOGIC_ANIMATIONS = AnimationsDef.logical(Map.of("assemble", 1.75f));
    public static final AnimationsDef TANK_LOGIC_ANIMATIONS = AnimationsDef.logical(Map.of("normal_attack_right", 1.0f, "normal_attack_left", 1.0f, "heavy_attack", 1.0f));

    public static final ClientResource<AnimationsDef> CERBERUS_ANIMATIONS = animation("cerberus_animation");
    public static final ClientResource<AnimationsDef> CURSED_ARMOR_ANIMATIONS = animation("cursed_armor_animation");
    public static final ClientResource<AnimationsDef> TANK_ANIMATIONS = animation("tank_animations");
    private static final List<ClientResource<?>> CLIENT_RESOURCES = List.of(
            CERBERUS_ANIMATIONS,
            CURSED_ARMOR_ANIMATIONS,
            TANK_ANIMATIONS
    );

    public static void reloadClient(ResourceManager manager) {
        CLIENT_RESOURCES.forEach(resource -> resource.reload(manager));
        BleedingPostProcessor.requestReload();
    }

    private static ClientResource<AnimationsDef> animation(String name) {
        return new ClientResource<>(MajruszsDifficulty.id("animations/" + name + ".json"), AnimationsDef.CODEC);
    }

    private ModResources() {
    }
}
