package com.majruszsdifficulty.mixin.fabric;

import com.majruszsdifficulty.items.ScrollItem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemInHandRenderer.class)
public abstract class MixinItemInHandRenderer {
    @Inject(
            method = "renderArmWithItem",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/AbstractClientPlayer;isUsingItem()Z", ordinal = 1)
    )
    private void majruszsdifficulty$scrollFirstPerson(AbstractClientPlayer player, float partialTick, float pitch,
                                                      InteractionHand hand, float swing, ItemStack stack, float equip, PoseStack poseStack,
                                                      MultiBufferSource buffers, int light, CallbackInfo callback) {
        ScrollItem.Client.modifyFirstPerson(stack, poseStack);
    }
}
