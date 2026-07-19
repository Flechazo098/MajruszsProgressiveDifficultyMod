package com.majruszsdifficulty.features;

import cc.sighs.oelib.event.Subscribe;
import com.majruszsdifficulty.config.GameplayConfig;
import com.majruszsdifficulty.events.ServerLootGeneratedEvent;
import com.majruszsdifficulty.gamestage.GameStageHelper;
import com.majruszsdifficulty.gamestage.GameStageValue;
import com.majruszsdifficulty.internal.emitter.ParticleEmitter;
import com.majruszsdifficulty.internal.math.Random;
import com.majruszsdifficulty.internal.text.RegexString;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class DoubleLoot {
    @Subscribe
    private static void doubleLoot(ServerLootGeneratedEvent data) {
        GameplayConfig.DoubleLoot settings = GameplayConfig.get().doubleLoot();
        if (!settings.isEnabled() || data.lastDamagePlayer == null || data.entity == null
                || !Random.check(GameStageValue.of(settings.chance()).get(GameStageHelper.determineGameStage(data.level, data.getPosition())))) {
            return;
        }
        if (DoubleLoot.replaceLoot(data.generatedLoot)) {
            ParticleEmitter.of(ParticleTypes.HAPPY_VILLAGER)
                    .count(6)
                    .sizeBased(data.entity)
                    .emit(data.getServerLevel());
        }
    }

    private static boolean replaceLoot(List<ItemStack> generatedLoot) {
        List<ItemStack> extraLoot = new ArrayList<>();
        generatedLoot.forEach(itemStack -> {
            if (DoubleLoot.isAllowed(itemStack)) {
                extraLoot.add(itemStack);
            }
        });
        generatedLoot.addAll(extraLoot);

        return !extraLoot.isEmpty();
    }

    private static boolean isAllowed(ItemStack itemStack) {
        if (itemStack.isEmpty()) {
            return false;
        }

        String id = BuiltInRegistries.ITEM.getKey(itemStack.getItem()).toString();
        return GameplayConfig.get().doubleLoot().blacklistedItems().stream().map(RegexString::new).noneMatch(regex -> regex.matches(id));
    }
}
