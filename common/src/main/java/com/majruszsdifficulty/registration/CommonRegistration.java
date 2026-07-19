package com.majruszsdifficulty.registration;

import cc.sighs.oelib.registry.extra.EntityAttributeRegister;
import com.majruszsdifficulty.bloodmoon.BloodMoonCommand;
import com.majruszsdifficulty.config.Configs;
import com.majruszsdifficulty.entity.*;
import com.majruszsdifficulty.features.MobGroups;
import com.majruszsdifficulty.gamestage.GameStageCommand;
import com.majruszsdifficulty.registry.*;
import com.majruszsdifficulty.treasurebag.TreasureBagCommands;
import com.majruszsdifficulty.undeadarmy.UndeadArmyCommands;

public final class CommonRegistration {
    public static void register() {
        Configs.register();
        BloodMoonCommand.register();
        GameStageCommand.register();
        TreasureBagCommands.register();
        UndeadArmyCommands.register();
        MobGroups.registerCommand();

        ModDataComponents.register();
        ModBlocks.register();
        ModArmorMaterials.register();
        ModEntities.register();
        ModEffects.register();
        ModParticles.register();
        ModPotions.register();
        ModItems.register();
        ModLootFunctions.register();
        ModRecipes.register();
        ModSounds.register();
        ModCreativeTabs.register();
        ModAdvancements.register();

        EntityAttributeRegister.register(ModEntities.CERBERUS_ENTITY, Cerberus::createAttributes);
        EntityAttributeRegister.register(ModEntities.CURSED_ARMOR_ENTITY, CursedArmor::createAttributes);
        EntityAttributeRegister.register(ModEntities.CREEPERLING_ENTITY, Creeperling::createChildAttributes);
        EntityAttributeRegister.register(ModEntities.GIANT_ENTITY, Giant::createAttributes);
        EntityAttributeRegister.register(ModEntities.ILLUSIONER_ENTITY, Illusioner::createAttributes);
        EntityAttributeRegister.register(ModEntities.TANK_ENTITY, Tank::createAttributes);
        SpawnPlacementRegistration.register();
    }

    private CommonRegistration() {
    }
}
