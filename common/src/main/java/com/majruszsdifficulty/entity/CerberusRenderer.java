package com.majruszsdifficulty.entity;

import com.majruszsdifficulty.MajruszsDifficulty;
import com.majruszsdifficulty.internal.annotation.Dist;
import com.majruszsdifficulty.internal.annotation.OnlyIn;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.resources.ResourceLocation;

@OnlyIn(Dist.CLIENT)
public class CerberusRenderer extends MobRenderer<Cerberus, CerberusModel<Cerberus>> {
    public static final ModelLayerLocation LAYER = MajruszsDifficulty.layer("cerberus");
    public static final ResourceLocation TEXTURE = MajruszsDifficulty.id("textures/entity/cerberus.png");

    public CerberusRenderer(EntityRendererProvider.Context context) {
        super(context, new CerberusModel<>(context.bakeLayer(LAYER)), 0.75f);

        this.addLayer(new CerberusEyesLayer(this));
    }

    @Override
    public ResourceLocation getTextureLocation(Cerberus cerberus) {
        return TEXTURE;
    }

    @Override
    public void render(Cerberus cerberus, float p_114209_, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        this.model.prepareMobModel(cerberus, 0.0f, 0.0f, partialTicks);

        super.render(cerberus, p_114209_, partialTicks, poseStack, bufferSource, packedLight);
    }

    @OnlyIn(Dist.CLIENT)
    public static class CerberusEyesLayer extends EyesLayer<Cerberus, CerberusModel<Cerberus>> {
        static final RenderType EYES = RenderType.eyes(MajruszsDifficulty.id("textures/entity/cerberus_eyes.png"));

        public CerberusEyesLayer(RenderLayerParent<Cerberus, CerberusModel<Cerberus>> layer) {
            super(layer);
        }

        @Override
        public RenderType renderType() {
            return EYES;
        }
    }
}
