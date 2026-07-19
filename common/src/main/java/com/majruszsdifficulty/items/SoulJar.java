package com.majruszsdifficulty.items;

import cc.sighs.oelib.event.EventBus;
import cc.sighs.oelib.event.Subscribe;
import com.majruszsdifficulty.events.*;
import com.majruszsdifficulty.internal.annotation.Dist;
import com.majruszsdifficulty.internal.annotation.OnlyIn;
import com.majruszsdifficulty.internal.entity.AttributeHandler;
import com.majruszsdifficulty.internal.math.Random;
import com.majruszsdifficulty.internal.platform.Side;
import com.majruszsdifficulty.internal.text.TextHelper;
import com.majruszsdifficulty.registry.ModDataComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.util.FastColor;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class SoulJar extends Item {
    private static final float DAMAGE_BONUS = 3.0f;
    private static final float MOVE_BONUS = 0.15f;
    private static final int ARMOR_BONUS = 3;
    private static final float MINE_BONUS = 0.15f;
    private static final int LUCK_BONUS = 1;
    private static final float SWIM_BONUS = 0.30f;
    private static final AttributeHandler ARMOR_ATTRIBUTE = new AttributeHandler("soul_jar_armor_bonus", () -> Attributes.ARMOR.value(), AttributeModifier.Operation.ADD_VALUE);
    private static final AttributeHandler LUCK_ATTRIBUTE = new AttributeHandler("soul_jar_luck_bonus", () -> Attributes.LUCK.value(), AttributeModifier.Operation.ADD_VALUE);
    private static final AttributeHandler MOVE_ATTRIBUTE = new AttributeHandler("soul_jar_movement_bonus", () -> Attributes.MOVEMENT_SPEED.value(), AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);

    static {
    }

    public SoulJar() {
        super(new Properties().stacksTo(1).rarity(Rarity.UNCOMMON));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        if (!level.isClientSide) {
            SoulJar.tryToRandomize(itemStack);
        }
        return InteractionResultHolder.sidedSuccess(itemStack, level.isClientSide);
    }

    @Subscribe
    private static void increaseDamage(ServerLivingEntityIncomingDamageEvent data) {
        if (data.target.getType().is(EntityTypeTags.UNDEAD)
                && data.attacker != null
                && BonusInfo.has(data.attacker, BonusType.DAMAGE)) {
            data.damage += DAMAGE_BONUS * SoulJar.getMultiplier(data.attacker, data.attacker.getOffhandItem());
            data.spawnMagicParticles = true;
        }
    }

    @Subscribe
    private static void increaseMineSpeed(PlayerBreakSpeedEvent data) {
        if (BonusInfo.has(data.player, BonusType.MINE)) {
            data.speed += data.original * MINE_BONUS * SoulJar.getMultiplier(data.player, data.player.getOffhandItem());
        }
    }

    public static float applySwimBonus(LivingEntity entity, float original) {
        return BonusInfo.has(entity, BonusType.SWIM)
                ? original + original * SWIM_BONUS * SoulJar.getMultiplier(entity, entity.getOffhandItem())
                : original;
    }

    @Subscribe
    private static void updateAttributes(ServerEquipmentChangedEvent data) {
        ItemStack itemStack = data.entity().getOffhandItem();
        float multiplier = SoulJar.canHaveSouls(itemStack) ? SoulJar.getMultiplier(data.entity(), itemStack) : 0.0f;
        BonusInfo bonusInfo = BonusInfo.read(itemStack);

        ARMOR_ATTRIBUTE.setValue((bonusInfo.has(BonusType.ARMOR) ? ARMOR_BONUS : 0.0f) * multiplier).apply(data.entity());
        LUCK_ATTRIBUTE.setValue((bonusInfo.has(BonusType.LUCK) ? LUCK_BONUS : 0.0f) * multiplier).apply(data.entity());
        MOVE_ATTRIBUTE.setValue((bonusInfo.has(BonusType.MOVE) ? MOVE_BONUS : 0.0f) * multiplier).apply(data.entity());
    }

    @Subscribe
    private static void randomize(ServerLootGeneratedEvent data) {
        data.generatedLoot.forEach(SoulJar::tryToRandomize);
    }

    @Subscribe
    private static void decreaseShieldBonus(OnSoulJarMultiplierGet data) {
        if (data.itemStack.getItem() instanceof ShieldItem) {
            data.multiplier *= 2.0f / 3.0f;
        }
    }

    private static void tryToRandomize(ItemStack itemStack) {
        if (itemStack.getItem() instanceof SoulJar) {
            BonusInfo bonusInfo = BonusInfo.read(itemStack);
            if (bonusInfo.getBonusTypes().isEmpty()) {
                bonusInfo.randomize();
                bonusInfo.writeTo(itemStack);
            }
        }
    }

    private static float getMultiplier(LivingEntity entity, ItemStack itemStack) {
        OnSoulJarMultiplierGet event = new OnSoulJarMultiplierGet(entity, itemStack);
        EventBus.post(event);
        return event.getMultiplier();
    }

    private static boolean canHaveSouls(ItemStack itemStack) {
        return itemStack.getItem() instanceof SoulJar
                || itemStack.getItem() instanceof ShieldItem;
    }

    public enum BonusType {
        DAMAGE("smite", "entity.minecraft.wolf", ChatFormatting.RED, 0xcc5555, multiplier -> TextHelper.signed(DAMAGE_BONUS * multiplier)),
        MOVE("move", "entity.minecraft.horse", ChatFormatting.WHITE, 0xdddddd, multiplier -> TextHelper.signedPercent(MOVE_BONUS * multiplier)),
        ARMOR("armor", "entity.majruszsdifficulty.tank", ChatFormatting.BLUE, 0x5555cc, multiplier -> TextHelper.signed((int) (ARMOR_BONUS * multiplier))),
        MINE("mine", "entity.minecraft.sniffer", ChatFormatting.YELLOW, 0xcccc55, multiplier -> TextHelper.signedPercent(MINE_BONUS * multiplier)),
        LUCK("luck", "entity.minecraft.rabbit", ChatFormatting.GREEN, 0x55cc55, multiplier -> TextHelper.signed(LUCK_BONUS * multiplier)),
        SWIM("swim", "entity.minecraft.dolphin", ChatFormatting.AQUA, 0x55cccc, multiplier -> TextHelper.signedPercent(SWIM_BONUS * multiplier));

        final String bonusId;
        final String mobId;
        final ChatFormatting soulFormatting;
        final int color;
        final Function<Float, String> valueProvider;

        BonusType(String bonusId, String mobId, ChatFormatting soulFormatting, int color, Function<Float, String> valueProvider) {
            this.bonusId = "item.majruszsdifficulty.soul_jar.%s".formatted(bonusId);
            this.mobId = mobId;
            this.soulFormatting = soulFormatting;
            this.color = color;
            this.valueProvider = valueProvider;
        }

        public int getBit() {
            return 1 << this.ordinal();
        }

        public int getColor() {
            return this.color;
        }

        public MutableComponent getBonusComponent(float multiplier) {
            return TextHelper.translatable(this.bonusId, this.valueProvider.apply(multiplier))
                    .withStyle(ChatFormatting.BLUE);
        }

        public MutableComponent getSoulComponent() {
            return TextHelper.translatable(this.mobId)
                    .withStyle(this.soulFormatting);
        }
    }

    public static class BonusInfo {
        private static final int BONUS_COUNT = 3;
        public final List<BonusType> bonuses = new ArrayList<>();

        public static BonusInfo read(ItemStack itemStack) {
            BonusInfo bonusInfo = new BonusInfo();
            bonusInfo.setMask(itemStack.getOrDefault(ModDataComponents.SOUL_JAR_BONUS_MASK.get(), 0));
            return bonusInfo;
        }

        public void writeTo(ItemStack itemStack) {
            itemStack.set(ModDataComponents.SOUL_JAR_BONUS_MASK.get(), this.getMask());
        }

        public static boolean has(LivingEntity entity, BonusType bonusType) {
            return BonusInfo.read(entity.getOffhandItem()).has(bonusType);
        }

        public void randomize() {
            this.setMask(BonusInfo.toMask(Random.next(Arrays.stream(BonusType.values()).toList(), BONUS_COUNT)));
        }

        public void setMask(int mask) {
            this.bonuses.clear();

            Arrays.stream(BonusType.values())
                    .filter(bonusType -> (bonusType.getBit() & mask) != 0b0)
                    .forEach(this.bonuses::add);
        }

        public int getMask() {
            return BonusInfo.toMask(this.bonuses);
        }

        public boolean has(BonusType bonusType) {
            return this.bonuses.contains(bonusType);
        }

        public boolean hasBonuses() {
            return !this.bonuses.isEmpty();
        }

        public Optional<BonusType> getBonus(int idx) {
            return idx < this.bonuses.size() ? Optional.of(this.bonuses.get(idx)) : Optional.empty();
        }

        public List<BonusType> getBonusTypes() {
            return this.bonuses;
        }

        public List<Component> getComponents() {
            List<Component> components = new ArrayList<>();
            if (this.bonuses.isEmpty()) {
                Component bonusCount = TextHelper.literal("%d", BONUS_COUNT).withStyle(ChatFormatting.GREEN);
                components.add(TextHelper.translatable("item.majruszsdifficulty.soul_jar.item_tooltip1", bonusCount).withStyle(ChatFormatting.GRAY));
                components.add(TextHelper.translatable("item.majruszsdifficulty.soul_jar.item_tooltip2").withStyle(ChatFormatting.GRAY));
            } else {
                components.add(TextHelper.empty());
                components.add(TextHelper.translatable("item.majruszsdifficulty.soul_jar.item_tooltip3").withStyle(ChatFormatting.GRAY));
                MutableComponent souls = TextHelper.literal("");
                for (BonusType bonusType : this.getBonusTypes()) {
                    souls.append(bonusType.getSoulComponent().append(" "));
                }
                components.add(souls);
            }

            return components;
        }

        private static int toMask(List<BonusType> bonuses) {
            int mask = 0b0;
            for (BonusType bonusType : bonuses) {
                mask |= bonusType.getBit();
            }

            return mask;
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class Client {
        public static int getColor(ItemStack itemStack, int layerIdx) {
            if (layerIdx < 1 || layerIdx > 3) {
                return -1;
            }
            BonusInfo bonusInfo = BonusInfo.read(itemStack);
            int idx = layerIdx - 1;
            int color = bonusInfo.getBonus(idx).map(BonusType::getColor).orElse(0xeeeeee - idx * 0x111111);
            return FastColor.ARGB32.opaque(color);
        }

        @Subscribe
        private static void addTooltip(ClientItemTooltipEvent data) {
            if (!SoulJar.canHaveSouls(data.itemStack())) {
                return;
            }
            BonusInfo bonusInfo = BonusInfo.read(data.itemStack());
            if (Side.getLocalPlayer() != null) {
                float multiplier = SoulJar.getMultiplier(Side.getLocalPlayer(), data.itemStack());
                for (BonusType bonusType : bonusInfo.getBonusTypes()) {
                    data.add(bonusType.getBonusComponent(multiplier));
                }
            }
            if (bonusInfo.hasBonuses() || data.itemStack().getItem() instanceof SoulJar) {
                data.components().addAll(bonusInfo.getComponents());
            }
        }
    }
}
