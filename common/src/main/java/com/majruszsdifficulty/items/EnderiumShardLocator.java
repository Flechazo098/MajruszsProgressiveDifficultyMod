package com.majruszsdifficulty.items;

import com.majruszsdifficulty.internal.annotation.Dist;
import com.majruszsdifficulty.internal.annotation.OnlyIn;
import com.majruszsdifficulty.internal.level.BlockHelper;
import com.majruszsdifficulty.internal.math.AnyPos;
import com.majruszsdifficulty.registry.ModBlocks;
import com.majruszsdifficulty.registry.ModDataComponents;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class EnderiumShardLocator extends Item {
    private static final int OFFSET = 26;

    public EnderiumShardLocator() {
        super(new Properties().rarity(Rarity.UNCOMMON));
    }


    private static boolean isOre(Level level, BlockPos position) {
        return BlockHelper.getState(level, position).getBlock() == ModBlocks.ENDERIUM_SHARD_ORE_BLOCK.get();
    }

    @OnlyIn(Dist.CLIENT)
    public static class Client {
        public static float getShardDistance(ItemStack itemStack, @Nullable ClientLevel level, @Nullable LivingEntity entity, int seed) {
            if (!(entity instanceof Player player) || level == null) {
                return 1.0f;
            }

            if (player.getInventory().findSlotMatchingItem(itemStack) == -1 && !player.getOffhandItem().equals(itemStack)) {
                return 1.0f;
            }

            ItemInfo itemInfo = new ItemInfo();
            itemInfo.position = itemStack.get(ModDataComponents.ENDERIUM_LOCATOR_POSITION.get());
            itemInfo.counter = itemStack.getOrDefault(ModDataComponents.ENDERIUM_LOCATOR_COUNTER.get(), 0);
            if (itemInfo.position != null && !EnderiumShardLocator.isOre(level, itemInfo.position)) {
                itemInfo.position = null;
            }
            itemInfo.position = Client.findNearestOre(level, player, itemInfo);
            itemInfo.counter = (itemInfo.counter + 1) % (2 * OFFSET);
            if (itemInfo.position != null) {
                itemStack.set(ModDataComponents.ENDERIUM_LOCATOR_POSITION.get(), itemInfo.position);
            } else {
                itemStack.remove(ModDataComponents.ENDERIUM_LOCATOR_POSITION.get());
            }
            itemStack.set(ModDataComponents.ENDERIUM_LOCATOR_COUNTER.get(), itemInfo.counter);

            if (itemInfo.position == null) {
                return 1.0f;
            }

            return (float) Mth.clamp(AnyPos.from(player.position()).dist(itemInfo.position).doubleValue() / OFFSET, 0.0f, 1.0f);
        }

        private static BlockPos findNearestOre(ClientLevel level, Player player, ItemInfo itemInfo) {
            BlockPos nearestPosition = itemInfo.position;
            float nearestDistance = nearestPosition != null ? AnyPos.from(player.position()).dist(nearestPosition.getCenter()).floatValue() : OFFSET;

            for (int x = -OFFSET; x < OFFSET; ++x) {
                for (int z = -OFFSET; z < OFFSET; ++z) {
                    BlockPos position = AnyPos.from(player.blockPosition()).add(x, itemInfo.counter - OFFSET, z).block();
                    if (EnderiumShardLocator.isOre(level, position)) {
                        float distance = AnyPos.from(player.position()).dist(position.getCenter()).floatValue();
                        if (nearestDistance > distance) {
                            nearestPosition = position;
                            nearestDistance = distance;
                        }
                    }
                }
            }

            return nearestPosition;
        }
    }

    private static class ItemInfo {
        public BlockPos position = null;
        public int counter = 0;
    }
}
