package com.majruszsdifficulty.entity;

import com.majruszsdifficulty.MajruszsDifficulty;
import com.majruszsdifficulty.internal.annotation.Dist;
import com.majruszsdifficulty.internal.annotation.OnlyIn;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

@OnlyIn(Dist.CLIENT)
public class TankRenderer extends MobRenderer<Tank, TankModel<Tank>> {
    public static final ModelLayerLocation LAYER = MajruszsDifficulty.layer("tank");
    public static final ResourceLocation TEXTURE = MajruszsDifficulty.id("textures/entity/tank.png");

    public TankRenderer(EntityRendererProvider.Context context) {
        super(context, new TankModel<>(context.bakeLayer(LAYER)), 0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(Tank tank) {
        return TEXTURE;
    }

    @Override
    public void render(Tank tank, float p_114209_, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        this.model.prepareMobModel(tank, 0.0f, 0.0f, partialTicks);

        super.render(tank, p_114209_, partialTicks, poseStack, bufferSource, packedLight);
    }
}
