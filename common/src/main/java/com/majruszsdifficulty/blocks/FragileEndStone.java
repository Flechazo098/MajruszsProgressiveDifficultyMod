package com.majruszsdifficulty.blocks;

import com.majruszsdifficulty.internal.level.BlockHelper;
import com.majruszsdifficulty.internal.math.AnyPos;
import com.majruszsdifficulty.internal.time.TimeHelper;
import com.majruszsdifficulty.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;

public class FragileEndStone extends Block {
    public FragileEndStone() {
        super(Properties.of().mapColor(MapColor.SAND).strength(0.0f, 0.75f).sound(SoundType.STONE));
    }

    @Override
    public void stepOn(Level level, BlockPos blockPos, BlockState blockState, Entity entity) {
        super.stepOn(level, blockPos, blockState, entity);

        if (entity instanceof Player player && !player.isSteppingCarefully()) {
            FragileEndStone.destroy(level, blockState, blockPos, player);
        }
    }

    @Override
    public void fallOn(Level level, BlockState blockState, BlockPos blockPos, Entity entity, float distance) {
        if (distance > 0.7f && entity instanceof Player player) {
            FragileEndStone.destroy(level, blockState, blockPos, player);
        }
    }

    @Override
    public void updateEntityAfterFallOn(BlockGetter block, Entity entity) {
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos blockPos, BlockState blockState, Player player) {
        BlockState result = super.playerWillDestroy(level, blockPos, blockState, player);

        Block block = blockState.getBlock();
        TimeHelper.nextTick(delay -> {
            for (Direction direction : Direction.values()) {
                BlockPos offset = AnyPos.from(blockPos).add(direction).block();
                BlockState neighbor = BlockHelper.getState(level, offset);
                if (neighbor.is(block)) {
                    FragileEndStone.destroy(level, blockState, offset, player);
                }
            }
        });

        return result;
    }

    private static void destroy(Level level, BlockState blockState, BlockPos blockPos, Player player) {
        blockState.getBlock().playerWillDestroy(level, blockPos, blockState, player);
        level.setBlockAndUpdate(blockPos, Blocks.AIR.defaultBlockState());
    }

    public static class Item extends BlockItem {
        public Item() {
            super(ModBlocks.FRAGILE_END_STONE_BLOCK.get(), new Properties().stacksTo(64));
        }
    }
}
