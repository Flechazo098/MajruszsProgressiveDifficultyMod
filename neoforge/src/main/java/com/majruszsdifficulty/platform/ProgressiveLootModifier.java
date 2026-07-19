package com.majruszsdifficulty.platform;

import com.majruszsdifficulty.loot.LootRuntimeHooks;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.loot.LootModifier;

public final class ProgressiveLootModifier extends LootModifier {
    public static final MapCodec<ProgressiveLootModifier> CODEC = RecordCodecBuilder.mapCodec(instance ->
            codecStart(instance).apply(instance, ProgressiveLootModifier::new)
    );

    public ProgressiveLootModifier(LootItemCondition[] conditions) {
        super(conditions);
    }

    @Override
    protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> loot, LootContext context) {
        return LootRuntimeHooks.modify(context.getQueriedLootTableId(), loot, context);
    }

    @Override
    public MapCodec<ProgressiveLootModifier> codec() {
        return CODEC;
    }
}
