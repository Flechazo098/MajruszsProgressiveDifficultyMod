package com.majruszsdifficulty.registry;

import cc.sighs.oelib.registry.DeferredRegister;
import cc.sighs.oelib.registry.RegisterSupplier;
import com.majruszsdifficulty.MajruszsDifficulty;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;

public final class ModDataComponents {
    private static final DeferredRegister<DataComponentType<?>> REGISTER = DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, MajruszsDifficulty.MOD_ID);

    public static final RegisterSupplier<DataComponentType<Integer>> SOUL_JAR_BONUS_MASK = REGISTER.register(
            "soul_jar_bonus_mask", () -> DataComponentType.<Integer>builder().persistent(Codec.INT).networkSynchronized(ByteBufCodecs.VAR_INT).build()
    );
    public static final RegisterSupplier<DataComponentType<BlockPos>> ENDERIUM_LOCATOR_POSITION = REGISTER.register(
            "enderium_locator_position", () -> DataComponentType.<BlockPos>builder().persistent(BlockPos.CODEC).networkSynchronized(BlockPos.STREAM_CODEC).build()
    );
    public static final RegisterSupplier<DataComponentType<Integer>> ENDERIUM_LOCATOR_COUNTER = REGISTER.register(
            "enderium_locator_counter", () -> DataComponentType.<Integer>builder().persistent(Codec.INT).networkSynchronized(ByteBufCodecs.VAR_INT).build()
    );

    public static void register() {
        REGISTER.register();
    }

    private ModDataComponents() {
    }
}
