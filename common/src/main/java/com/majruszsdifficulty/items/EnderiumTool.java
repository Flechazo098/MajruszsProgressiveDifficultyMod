package com.majruszsdifficulty.items;

import net.minecraft.world.item.*;

public class EnderiumTool {
    public static class Axe extends AxeItem {
        public Axe() {
            super(CustomItemTier.ENDERIUM, new Properties().attributes(AxeItem.createAttributes(CustomItemTier.ENDERIUM, 6.0f, -3.1f)).rarity(Rarity.UNCOMMON).fireResistant());
        }
    }

    public static class Hoe extends HoeItem {
        public Hoe() {
            super(CustomItemTier.ENDERIUM, new Properties().attributes(HoeItem.createAttributes(CustomItemTier.ENDERIUM, -5.0f, 0.0f)).rarity(Rarity.UNCOMMON).fireResistant());
        }

        // TODO: till 3x3 area
    }

    public static class Pickaxe extends PickaxeItem {
        public Pickaxe() {
            super(CustomItemTier.ENDERIUM, new Properties().attributes(PickaxeItem.createAttributes(CustomItemTier.ENDERIUM, 1.0f, -2.8f)).rarity(Rarity.UNCOMMON).fireResistant());
        }
    }

    public static class Shovel extends ShovelItem {
        public Shovel() {
            super(CustomItemTier.ENDERIUM, new Properties().attributes(ShovelItem.createAttributes(CustomItemTier.ENDERIUM, 1.5f, -3.0f)).rarity(Rarity.UNCOMMON).fireResistant());
        }
    }

    public static class Sword extends SwordItem {
        public Sword() {
            super(CustomItemTier.ENDERIUM, new Properties().attributes(SwordItem.createAttributes(CustomItemTier.ENDERIUM, 4, -2.6f)).rarity(Rarity.UNCOMMON).fireResistant());
        }
    }
}
