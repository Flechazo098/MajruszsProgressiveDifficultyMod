package com.majruszsdifficulty.itemsets;

import cc.sighs.oelib.event.Subscribe;
import com.majruszsdifficulty.events.ClientItemTooltipEvent;
import com.majruszsdifficulty.internal.annotation.Dist;
import com.majruszsdifficulty.internal.annotation.OnlyIn;
import com.majruszsdifficulty.internal.client.ClientHelper;
import com.majruszsdifficulty.internal.platform.Side;
import com.majruszsdifficulty.internal.text.TextHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;

import java.util.ArrayList;
import java.util.List;

public class ItemSet {
    private static final List<ItemSet> ALL = new ArrayList<>();
    private List<ItemSetRequirement> requirements = new ArrayList<>();
    private List<ItemSetBonus> bonuses = new ArrayList<>();
    private Component component;
    private ChatFormatting[] chatFormatting;

    public static ItemSet create() {
        return new ItemSet();
    }

    public ItemSet component(String id) {
        this.component = TextHelper.translatable(id);

        return this;
    }

    public ItemSet format(ChatFormatting... chatFormatting) {
        this.chatFormatting = chatFormatting;

        return this;
    }

    public ItemSet require(ItemSetRequirement... requirements) {
        this.requirements = List.of(requirements);

        return this;
    }

    public ItemSet bonus(ItemSetBonus... bonuses) {
        this.bonuses = List.of(bonuses);

        return this;
    }

    public int getRequirementsSize() {
        return this.requirements.size();
    }

    public ChatFormatting[] getFormatting() {
        return this.chatFormatting;
    }

    public boolean canTrigger(ItemSetBonus bonus, LivingEntity entity) {
        return bonus.canTrigger(this.findRequirementsMet(entity));
    }

    public List<ItemSetRequirement> findRequirementsMet(LivingEntity entity) {
        return this.requirements.stream().filter(requirement -> requirement.check(entity)).toList();
    }

    private ItemSet() {
        ALL.add(this);
    }

    private Component getItemTitleComponent(List<ItemSetRequirement> requirementsMet) {
        return TextHelper.translatable("majruszsdifficulty.item_sets.item_title", this.component, requirementsMet.size(), this.requirements.size())
                .withStyle(ChatFormatting.GRAY);
    }

    private Component getBonusTitleComponent() {
        return TextHelper.translatable("majruszsdifficulty.item_sets.bonus_title", this.component)
                .withStyle(ChatFormatting.GRAY);
    }

    @OnlyIn(Dist.CLIENT)
    public static class Client {
        @Subscribe
        private static void addTooltips(ClientItemTooltipEvent data) {
            if (Side.getLocalPlayer() == null) {
                return;
            }
            ALL.stream()
                    .filter(itemSet -> itemSet.requirements.stream().anyMatch(requirement -> requirement.is(data.itemStack())))
                    .forEach(itemSet -> addTooltip(data, itemSet));
        }

        private static void addTooltip(ClientItemTooltipEvent data, ItemSet itemSet) {
            if (!ClientHelper.isShiftDown()) {
                data.add(TextHelper.empty());
                data.add(TextHelper.translatable("majruszsdifficulty.item_sets.hint").withStyle(ChatFormatting.GRAY));
                return;
            }

            List<ItemSetRequirement> requirementsMet = itemSet.findRequirementsMet(Side.getLocalPlayer());

            data.add(TextHelper.empty());
            data.add(itemSet.getItemTitleComponent(requirementsMet));
            data.components().addAll(itemSet.requirements.stream().map(requirement -> requirement.toComponent(itemSet, requirementsMet)).toList());
            data.add(TextHelper.empty());
            data.add(itemSet.getBonusTitleComponent());
            data.components().addAll(itemSet.bonuses.stream().map(bonus -> bonus.toComponent(itemSet, requirementsMet)).toList());
        }
    }
}
