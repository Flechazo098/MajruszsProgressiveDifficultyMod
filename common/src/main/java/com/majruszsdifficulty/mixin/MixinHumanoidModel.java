package com.majruszsdifficulty.mixin;

import com.majruszsdifficulty.items.ScrollItem;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HumanoidModel.class)
public abstract class MixinHumanoidModel {
    @Inject(method = "setupAnim(Lnet/minecraft/world/entity/LivingEntity;FFFFF)V", at = @At("TAIL"))
    private void majruszsdifficulty$scrollCastingPose(LivingEntity entity, float limbSwing, float limbSwingAmount,
                                                      float ageInTicks, float headYaw, float headPitch, CallbackInfo callback) {
        ScrollItem.Client.modifyModel(entity, (HumanoidModel<?>) (Object) this);
    }
}
