package com.majruszsdifficulty.blocks;

import com.majruszsdifficulty.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.MapColor;
import org.jetbrains.annotations.Nullable;

public class InfernalSponge extends Block {
    public InfernalSponge() {
        super(Properties.of().mapColor(MapColor.NETHER).strength(0.6f).sound(SoundType.GRASS));
    }

    @Override
    public void setPlacedBy(Level level, BlockPos position, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, position, state, placer, stack);
        if (!level.isClientSide) {
            InfernalSponge.absorb(level, position);
        }
    }

    @Override
    public void neighborChanged(BlockState blockState, Level level, BlockPos blockPos, Block neighbor, BlockPos neighborPos, boolean p_56806_) {
        InfernalSponge.absorb(level, blockPos);

        super.neighborChanged(blockState, level, blockPos, neighbor, neighborPos, p_56806_);
    }

    private static void absorb(Level level, BlockPos spongePos) {
        int absorbedBlocks = BlockPos.breadthFirstTraversal(spongePos, 6, 65, (blockPos, consumer) -> {
            for (Direction direction : Direction.values()) {
                consumer.accept(blockPos.relative(direction));
            }
        }, blockPos -> {
            if (blockPos.equals(spongePos)) {
                return true;
            }

            BlockState blockState = level.getBlockState(blockPos);
            FluidState fluidState = level.getFluidState(blockPos);
            if (!fluidState.is(Fluids.LAVA) && !fluidState.is(Fluids.FLOWING_LAVA)) {
                return false;
            }
            if (blockState.getBlock() instanceof BucketPickup bucketPickup && !bucketPickup.pickupBlock(null, level, blockPos, blockState).isEmpty()) {
                return true;
            }

            if (blockState.getBlock() instanceof LiquidBlock) {
                level.setBlock(blockPos, Blocks.AIR.defaultBlockState(), 3);
            } else {
                BlockEntity blockentity = blockState.hasBlockEntity() ? level.getBlockEntity(blockPos) : null;
                dropResources(blockState, level, blockPos, blockentity);
                level.setBlock(blockPos, Blocks.AIR.defaultBlockState(), 3);
            }

            return true;
        }) - 1;

        if (absorbedBlocks > 0) {
            level.setBlock(spongePos, ModBlocks.SOAKED_INFERNAL_SPONGE_BLOCK.get().defaultBlockState(), 2);
        }
    }

    public static class Item extends BlockItem {
        public Item() {
            super(ModBlocks.INFERNAL_SPONGE_BLOCK.get(), new Properties().rarity(Rarity.UNCOMMON).fireResistant());
        }
    }
}
