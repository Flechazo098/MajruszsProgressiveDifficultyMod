package com.majruszsdifficulty.mixin;

import com.majruszsdifficulty.internal.math.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.Heightmap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NaturalSpawner.class)
public abstract class MixinNaturalSpawner {
    @Inject(method = "spawnCategoryForChunk", at = @At("HEAD"), cancellable = true)
    private static void majruszsdifficulty$modifySpawnRate(MobCategory category, ServerLevel level, LevelChunk chunk,
                                                           NaturalSpawner.SpawnPredicate predicate, NaturalSpawner.AfterSpawnCallback callback, CallbackInfo info) {
        float value = com.majruszsdifficulty.bloodmoon.listeners.SpawnRateIncreaser.modify(category, 1.0f);
        value = com.majruszsdifficulty.features.SpawnRateIncreaser.modify(category, value);
        int attempts = Random.round(value);
        if (attempts <= 0) {
            info.cancel();
            return;
        }
        for (int i = 1; i < attempts; ++i) {
            int x = chunk.getPos().getMinBlockX() + Random.nextInt(16);
            int z = chunk.getPos().getMinBlockZ() + Random.nextInt(16);
            int y = Random.nextInt(level.getMinBuildHeight(), chunk.getHeight(Heightmap.Types.WORLD_SURFACE, x, z) + 1);
            NaturalSpawner.spawnCategoryForPosition(category, level, new BlockPos(x, y, z));
        }
    }
}
