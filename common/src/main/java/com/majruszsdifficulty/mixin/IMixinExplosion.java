package com.majruszsdifficulty.mixin;

import net.minecraft.world.level.Explosion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Explosion.class)
public interface IMixinExplosion {
    @Mutable
    @Accessor("radius")
    void majruszsdifficulty$setRadius(float radius);

    @Mutable
    @Accessor("fire")
    void majruszsdifficulty$setFire(boolean fire);
}
