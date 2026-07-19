package com.majruszsdifficulty.internal.entity;

import com.majruszsdifficulty.MajruszsDifficulty;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import java.util.function.Supplier;

public class AttributeHandler {
    final ResourceLocation id;
    final Supplier<Attribute> attribute;
    final AttributeModifier.Operation operation;
    double value = 0.0;

    public AttributeHandler(String uuid, String name, Supplier<Attribute> attribute, AttributeModifier.Operation operation) {
        this.id = ResourceLocation.fromNamespaceAndPath(MajruszsDifficulty.MOD_ID, name);
        this.attribute = attribute;
        this.operation = operation;
    }

    public AttributeHandler(String name, Supplier<Attribute> attribute, AttributeModifier.Operation operation) {
        this.id = ResourceLocation.fromNamespaceAndPath(MajruszsDifficulty.MOD_ID, name);
        this.attribute = attribute;
        this.operation = operation;
    }

    public boolean hasAttribute(LivingEntity entity) {
        return entity.getAttributes().hasAttribute(this.holder());
    }

    public boolean hasValueChanged(AttributeInstance attributeInstance) {
        AttributeModifier modifier = attributeInstance.getModifier(this.id);

        return modifier == null || modifier.amount() != this.value;
    }

    public double getValue() {
        return this.value;
    }

    public AttributeHandler setValue(double value) {
        this.value = value;

        return this;
    }

    public AttributeHandler apply(LivingEntity entity) {
        AttributeInstance attributeInstance = entity.getAttribute(this.holder());
        if (attributeInstance != null && this.hasValueChanged(attributeInstance)) {
            attributeInstance.removeModifier(this.id);
            attributeInstance.addTransientModifier(this.createAttribute());
        }

        return this;
    }

    public AttributeHandler remove(LivingEntity entity) {
        AttributeInstance attributeInstance = entity.getAttribute(this.holder());
        if (attributeInstance != null) {
            attributeInstance.removeModifier(this.id);
        }

        return this;
    }

    public AttributeModifier createAttribute() {
        return new AttributeModifier(this.id, this.value, this.operation);
    }

    private Holder<Attribute> holder() {
        return BuiltInRegistries.ATTRIBUTE.wrapAsHolder(this.attribute.get());
    }
}
