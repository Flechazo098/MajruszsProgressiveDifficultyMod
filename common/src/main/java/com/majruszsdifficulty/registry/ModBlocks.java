package com.majruszsdifficulty.registry;

import cc.sighs.oelib.registry.DeferredRegister;
import cc.sighs.oelib.registry.RegisterSupplier;
import com.majruszsdifficulty.MajruszsDifficulty;
import com.majruszsdifficulty.blocks.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;

public final class ModBlocks {
    private static final DeferredRegister<Block> REGISTER = DeferredRegister.create(Registries.BLOCK, MajruszsDifficulty.MOD_ID);

    public static final RegisterSupplier<EnderiumBlock> ENDERIUM_BLOCK = REGISTER.register("enderium_block", EnderiumBlock::new);
    public static final RegisterSupplier<EnderiumShardOre> ENDERIUM_SHARD_ORE_BLOCK = REGISTER.register("enderium_shard_ore", EnderiumShardOre::new);
    public static final RegisterSupplier<FragileEndStone> FRAGILE_END_STONE_BLOCK = REGISTER.register("fragile_end_stone", FragileEndStone::new);
    public static final RegisterSupplier<InfernalSponge> INFERNAL_SPONGE_BLOCK = REGISTER.register("infernal_sponge", InfernalSponge::new);
    public static final RegisterSupplier<InfestedEndStone> INFESTED_END_STONE_BLOCK = REGISTER.register("infested_end_stone", InfestedEndStone::new);
    public static final RegisterSupplier<SoakedInfernalSponge> SOAKED_INFERNAL_SPONGE_BLOCK = REGISTER.register("soaked_infernal_sponge", SoakedInfernalSponge::new);

    public static void register() {
        REGISTER.register();
    }

    private ModBlocks() {
    }
}
