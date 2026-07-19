package com.majruszsdifficulty.internal.item;

import com.majruszsdifficulty.internal.entity.EntityHelper;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class ItemHelper {
    public static Optional<SmeltResult> tryToSmelt(Level level, ItemStack itemStack) {
        Optional<RecipeHolder<SmeltingRecipe>> recipe = level.getRecipeManager().getRecipeFor(RecipeType.SMELTING, new SingleRecipeInput(itemStack), level);
        if (recipe.isPresent()) {
            float experience = recipe.get().value().getExperience() * itemStack.getCount();
            ItemStack result = recipe.get().value().getResultItem(level.registryAccess()).copy();
            result.setCount(result.getCount() * itemStack.getCount());

            return Optional.of(new SmeltResult(result, experience));
        }

        return Optional.empty();
    }

    public static void giveToPlayer(ItemStack itemStack, Player player) {
        if (!player.getInventory().add(itemStack)) {
            player.level().addFreshEntity(new ItemEntity(player.level(), player.getX(), player.getY() + 1.0, player.getZ(), itemStack));
        }
    }

    public static void damage(LivingEntity entity, EquipmentSlot slot, int damage) {
        entity.getItemBySlot(slot).hurtAndBreak(damage, entity, slot);
    }

    public static void damage(LivingEntity entity, InteractionHand hand, int damage) {
        entity.getItemInHand(hand).hurtAndBreak(damage, entity, hand == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);
    }

    /**
     * Required because Mob::equipItemIfPossible makes item a guaranteed drop and enables persistence for mob.
     */
    public static @Nullable EquipmentSlot equip(Mob mob, ItemStack itemStack) {
        if (!mob.canHoldItem(itemStack)) {
            return null;
        }

        EquipmentSlot equipmentSlot = mob.getEquipmentSlotForItem(itemStack);
        equipmentSlot = equipmentSlot.isArmor() ? equipmentSlot : EquipmentSlot.MAINHAND;
        mob.setItemSlot(equipmentSlot, itemStack);

        return equipmentSlot;
    }

    public static void consumeItemOnUse(ItemStack itemStack, Player player) {
        player.awardStat(Stats.ITEM_USED.get(itemStack.getItem()));
        if (!EntityHelper.isOnCreativeMode(player)) {
            itemStack.shrink(1);
        }
    }

    public static void addCooldown(Player player, int duration, Item... items) {
        for (Item item : items) {
            player.getCooldowns().addCooldown(item, duration);
        }
    }

    public static <Type extends EntityType<? extends Mob>> Supplier<SpawnEggItem> createEgg(Supplier<Type> type, int backgroundColor,
                                                                                            int highlightColor
    ) {
        return () -> new SpawnEggItem(type.get(), backgroundColor, highlightColor, new Item.Properties());
    }

    public static ItemStack getHandItem(LivingEntity entity, Predicate<ItemStack> predicate) {
        return predicate.test(entity.getMainHandItem()) ? entity.getMainHandItem() : entity.getOffhandItem();
    }

    public static ItemStack getCurrentlyUsedItem(LivingEntity entity) {
        return entity.isUsingItem() ? entity.getItemInHand(entity.getUsedItemHand()) : ItemStack.EMPTY;
    }

    public static boolean isOnCooldown(Player player, Item... items) {
        for (Item item : items) {
            if (player.getCooldowns().isOnCooldown(item)) {
                return true;
            }
        }

        return false;
    }


    public static boolean isShield(Item item) {
        return item instanceof ShieldItem;
    }

    public static boolean isRangedWeapon(Item item) {
        return item instanceof BowItem
                || item instanceof CrossbowItem;
    }

    public static boolean isMeleeWeapon(Item item) {
        return item instanceof SwordItem
                || item instanceof TridentItem
                || item instanceof AxeItem;
    }

    public static boolean isGoldenToolOrArmor(Item item) {
        return item instanceof SwordItem swordItem && swordItem.getTier() == Tiers.GOLD
                || item instanceof DiggerItem diggerItem && diggerItem.getTier() == Tiers.GOLD
                || item instanceof ArmorItem armorItem && armorItem.getMaterial() == ArmorMaterials.GOLD;
    }

    public static boolean isAnyTool(Item item) {
        return item instanceof SwordItem
                || item instanceof TridentItem
                || item instanceof DiggerItem
                || item instanceof BowItem
                || item instanceof CrossbowItem;
    }

    public static boolean isFishingRod(Item item) {
        return item instanceof FishingRodItem;
    }

    public record SmeltResult(ItemStack itemStack, float experience) {
    }

}
