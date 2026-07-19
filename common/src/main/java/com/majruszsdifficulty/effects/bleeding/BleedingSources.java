package com.majruszsdifficulty.effects.bleeding;

import cc.sighs.oelib.event.Subscribe;
import com.majruszsdifficulty.events.OnBleedingCheck;
import com.majruszsdifficulty.events.OnBleedingTooltip;
import com.majruszsdifficulty.internal.item.EnchantmentHelper;
import com.majruszsdifficulty.internal.math.Random;
import com.majruszsdifficulty.internal.text.RegexString;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;

import java.util.List;
import java.util.Optional;

public class BleedingSources {
    public static class Arrows {
        @Subscribe
        private static void check(OnBleedingCheck data) {
            BleedingConfig.ChanceSource settings = BleedingConfig.get().sources().arrows();
            if (settings.isEnabled() && data.source.getDirectEntity() instanceof Arrow && Random.check(settings.chance())) {
                data.trigger();
            }
        }

        @Subscribe
        private static void addTooltip(OnBleedingTooltip data) {
            BleedingConfig.ChanceSource settings = BleedingConfig.get().sources().arrows();
            if (settings.isEnabled() && data.itemStack.getItem() instanceof ProjectileWeaponItem) {
                data.addItem((float) settings.chance());
            }
        }
    }

    public static class Bite {
        @Subscribe
        private static void check(OnBleedingCheck data) {
            BleedingConfig.BiteSource settings = BleedingConfig.get().sources().bite();
            if (!settings.isEnabled() || !data.source.isDirect() || data.attacker == null) {
                return;
            }
            if ((data.attacker instanceof Animal || data.attacker instanceof Zombie || data.attacker instanceof Spider)
                    && !settings.blacklistedAnimals().contains(data.attacker.getType())
                    && Random.check(settings.chance())) {
                data.trigger();
            }
        }
    }

    public static class Cactus {
        @Subscribe
        private static void check(OnBleedingCheck data) {
            BleedingConfig.ChanceSource settings = BleedingConfig.get().sources().cactus();
            if (settings.isEnabled() && data.source.is(DamageTypes.CACTUS) && Random.check(settings.chance())) {
                data.trigger();
            }
        }
    }

    public static class Tools {
        @Subscribe
        private static void check(OnBleedingCheck data) {
            if (BleedingConfig.get().sources().tools().isEnabled() && toolBasedChance(data)) {
                data.trigger();
            }
        }

        @Subscribe
        private static void addTooltip(OnBleedingTooltip data) {
            if (BleedingConfig.get().sources().tools().isEnabled()) {
                Tools.find(data.itemStack).ifPresent(def -> data.addItem(def.getChance(data.itemStack)));
            }
        }

        private static Optional<ToolDef> find(ItemStack itemStack) {
            return BleedingConfig.get().sources().tools().tools().stream()
                    .filter(toolDef -> toolDef.matches(itemStack))
                    .findFirst();
        }

        private static boolean toolBasedChance(OnBleedingCheck data) {
            if (data.attacker == null) {
                return false;
            }

            ItemStack itemStack = data.attacker.getMainHandItem();
            return Tools.find(itemStack)
                    .map(def -> Random.check(def.getChance(itemStack)))
                    .orElse(false);
        }

        public static class ToolDef {
            public static final Codec<ToolDef> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                    Codec.STRING.fieldOf("id").forGetter(value -> value.id.get()),
                    Codec.floatRange(0.0f, 1.0f).fieldOf("chance").forGetter(value -> value.chance),
                    EnchantmentDef.CODEC.listOf().fieldOf("enchantments").orElse(List.of()).forGetter(value -> value.enchantmentDefs)
            ).apply(instance, ToolDef::new));
            public RegexString id;
            public float chance;
            public List<EnchantmentDef> enchantmentDefs;

            public ToolDef(String id, float chance, List<EnchantmentDef> enchantmentDefs) {
                this.id = new RegexString(id);
                this.chance = chance;
                this.enchantmentDefs = enchantmentDefs;
            }

            public ToolDef(String id, float chance) {
                this(id, chance, List.of());
            }

            public ToolDef() {
                this("", 0.0f);
            }

            public float getChance(ItemStack itemStack) {
                return this.chance + EnchantmentHelper.read(itemStack).enchantments.stream().map(this::getExtraChance).reduce(0.0f, Float::sum);
            }

            public boolean matches(ItemStack itemStack) {
                return this.id.matches(BuiltInRegistries.ITEM.getKey(itemStack.getItem()).toString());
            }

            private float getExtraChance(EnchantmentHelper.EnchantmentDef itemEnchantment) {
                for (EnchantmentDef enchantmentDef : this.enchantmentDefs) {
                    if (enchantmentDef.id.matches(itemEnchantment.id().toString())) {
                        return itemEnchantment.level() * enchantmentDef.chance;
                    }
                }

                return 0.0f;
            }
        }

        public static class EnchantmentDef {
            public static final Codec<EnchantmentDef> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                    Codec.STRING.fieldOf("id").forGetter(value -> value.id.get()),
                    Codec.floatRange(0.0f, 1.0f).fieldOf("extra_chance_per_level").forGetter(value -> value.chance)
            ).apply(instance, EnchantmentDef::new));
            public RegexString id;
            public float chance;

            public EnchantmentDef(String id, float chance) {
                this.id = new RegexString(id);
                this.chance = chance;
            }

            public EnchantmentDef() {
                this("", 0.0f);
            }
        }
    }
}
