package com.majruszsdifficulty.treasurebag;

import cc.sighs.oelib.event.Subscribe;
import com.majruszsdifficulty.events.ClientItemTooltipEvent;
import com.majruszsdifficulty.internal.annotation.Dist;
import com.majruszsdifficulty.internal.annotation.OnlyIn;
import com.majruszsdifficulty.internal.client.ClientHelper;
import com.majruszsdifficulty.internal.platform.Side;
import com.majruszsdifficulty.internal.text.TextHelper;
import com.majruszsdifficulty.items.TreasureBag;
import com.majruszsdifficulty.network.TreasureBagProgressPacket;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class TreasureBagClient {
    private static final Map<String, List<Component>> COMPONENTS = new HashMap<>();

    @Subscribe
    private static void addTooltip(ClientItemTooltipEvent data) {
        if (!(data.itemStack().getItem() instanceof TreasureBag)) {
            return;
        }
        if (data.isAdvanced()) {
            data.add(TextHelper.translatable("majruszsdifficulty.treasure_bag.item_tooltip").withStyle(ChatFormatting.GRAY));
            data.add(TextHelper.empty());
        }

        if (ClientHelper.isShiftDown()) {
            List<Component> components = COMPONENTS.get(BuiltInRegistries.ITEM.getKey(data.itemStack().getItem()).toString());
            if (components == null) {
                return;
            }

            data.components().addAll(components);
        } else {
            data.add(TextHelper.translatable("majruszsdifficulty.treasure_bag.hint_tooltip").withStyle(ChatFormatting.GRAY));
        }
    }

    public static void onProgressReceived(TreasureBagProgressPacket data) {
        List<Component> components = COMPONENTS.computeIfAbsent(data.id().toString(), id -> new ArrayList<>());
        components.clear();
        components.add(TreasureBagClient.toProgressComponent(data.items()));
        for (TreasureBagHelper.ItemProgressData itemProgress : data.items()) {
            components.add(TextHelper.literal(" ").append(TreasureBagClient.toComponent(itemProgress)));
        }
        if (!data.unlockedIndices().isEmpty()) {
            TreasureBagClient.sendToChat(data);
        }
    }

    private static void sendToChat(TreasureBagProgressPacket data) {
        List<Component> components = new ArrayList<>();
        for (int idx = 0; idx < data.items().size(); ++idx) {
            components.add(TextHelper.literal(data.unlockedIndices().contains(idx) ? "+" : " ")
                    .withStyle(ChatFormatting.DARK_GREEN)
                    .append(TreasureBagClient.toComponent(data.items().get(idx)))
            );
        }

        Item item = BuiltInRegistries.ITEM.get(data.id());
        ItemStack itemStack = new ItemStack(item);
        MutableComponent message = TextHelper.translatable("majruszsdifficulty.treasure_bag.new_items", ComponentUtils.wrapInSquareBrackets(item.getDescription())
                .withStyle(itemStack.getRarity().color()));
        MutableComponent description = TreasureBagClient.toProgressComponent(data.items());
        components.forEach(component -> description.append(TextHelper.literal("\n").append(component)));

        Side.getLocalPlayer()
                .sendSystemMessage(message.withStyle(style -> style.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, description))));
    }

    private static MutableComponent toProgressComponent(List<TreasureBagHelper.ItemProgressData> items) {
        long unlockedItems = items.stream().filter(TreasureBagHelper.ItemProgressData::isUnlocked).count();
        int totalItems = items.size();

        return TextHelper.translatable("majruszsdifficulty.treasure_bag.list_tooltip", unlockedItems, totalItems).withStyle(ChatFormatting.GRAY);
    }

    private static Component toComponent(TreasureBagHelper.ItemProgressData itemProgress) {
        if (itemProgress.isUnlocked()) {
            return BuiltInRegistries.ITEM.get(itemProgress.id())
                    .getDescription()
                    .copy()
                    .withStyle(TreasureBagClient.getUnlockedFormatting(itemProgress.quality()));
        } else {
            return TextHelper.literal("???")
                    .withStyle(TreasureBagClient.getLockedFormatting(itemProgress.quality()));
        }
    }

    private static ChatFormatting getUnlockedFormatting(int quality) {
        return switch (quality) {
            case 4 -> ChatFormatting.GOLD;
            case 3 -> ChatFormatting.LIGHT_PURPLE;
            case 2 -> ChatFormatting.BLUE;
            case 1 -> ChatFormatting.GREEN;
            default -> ChatFormatting.GRAY;
        };
    }

    private static ChatFormatting getLockedFormatting(int quality) {
        return switch (quality) {
            case 4 -> ChatFormatting.GOLD;
            case 3 -> ChatFormatting.DARK_PURPLE;
            case 2 -> ChatFormatting.BLUE;
            case 1 -> ChatFormatting.DARK_GREEN;
            default -> ChatFormatting.DARK_GRAY;
        };
    }
}
