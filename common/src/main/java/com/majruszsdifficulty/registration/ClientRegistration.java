package com.majruszsdifficulty.registration;

import cc.sighs.oelib.config.ConfigManager;
import cc.sighs.oelib.registry.extra.EntityModelLayerRegister;
import cc.sighs.oelib.registry.extra.EntityRendererRegister;
import com.majruszsdifficulty.config.ClientVisualConfig;
import com.majruszsdifficulty.entity.*;
import com.majruszsdifficulty.registry.ModEntities;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.LayerDefinition;

public final class ClientRegistration {
    public static void register() {
        ConfigManager.registerClient(ClientVisualConfig.UNIT);

        EntityModelLayerRegister.register(CerberusRenderer.LAYER, () -> CerberusModel.MODEL.get().toLayerDefinition());
        EntityModelLayerRegister.register(CursedArmorRenderer.LAYER, () -> CursedArmorModel.MODEL.get().toLayerDefinition());
        EntityModelLayerRegister.register(CursedArmorRenderer.INNER_ARMOR_LAYER, () -> LayerDefinition.create(CursedArmorModel.createMesh(new CubeDeformation(0.5f), 0.0f), 64, 32));
        EntityModelLayerRegister.register(CursedArmorRenderer.OUTER_ARMOR_LAYER, () -> LayerDefinition.create(CursedArmorModel.createMesh(new CubeDeformation(1.0f), 0.0f), 64, 32));
        EntityModelLayerRegister.register(CreeperlingRenderer.LAYER, () -> CreeperlingModel.MODEL.get().toLayerDefinition());
        EntityModelLayerRegister.register(GiantRenderer.LAYER, () -> LayerDefinition.create(GiantModel.createMesh(CubeDeformation.NONE, 0.0f), 64, 64));
        EntityModelLayerRegister.register(TankRenderer.LAYER, () -> TankModel.MODEL.get().toLayerDefinition());

        EntityRendererRegister.register(ModEntities.CERBERUS_ENTITY, CerberusRenderer::new);
        EntityRendererRegister.register(ModEntities.CURSED_ARMOR_ENTITY, CursedArmorRenderer::new);
        EntityRendererRegister.register(ModEntities.CREEPERLING_ENTITY, CreeperlingRenderer::new);
        EntityRendererRegister.register(ModEntities.GIANT_ENTITY, GiantRenderer::new);
        EntityRendererRegister.register(ModEntities.ILLUSIONER_ENTITY, IllusionerRenderer::new);
        EntityRendererRegister.register(ModEntities.TANK_ENTITY, TankRenderer::new);
    }

    private ClientRegistration() {
    }
}
