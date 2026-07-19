package com.majruszsdifficulty.entity;

import com.majruszsdifficulty.MajruszsDifficulty;
import com.majruszsdifficulty.internal.animations.ModelParts;
import com.majruszsdifficulty.internal.annotation.Dist;
import com.majruszsdifficulty.internal.annotation.OnlyIn;
import com.majruszsdifficulty.resource.ClientModelResource;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;

@OnlyIn(Dist.CLIENT)
public class CursedArmorModel<Type extends CursedArmor> extends HumanoidModel<Type> {
    public static final ClientModelResource MODEL = new ClientModelResource(MajruszsDifficulty.id("custom/cursed_armor_model.json"));
    public final ModelParts modelParts;

    public CursedArmorModel(ModelPart modelPart) {
        super(modelPart);

        this.modelParts = new ModelParts(modelPart, MODEL.get());
    }

    @Override
    public void setupAnim(Type cursedArmor, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.modelParts.reset();

        super.setupAnim(cursedArmor, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);

        cursedArmor.getAnimations().forEach(animation -> animation.apply(this.modelParts, ageInTicks));
    }
}

