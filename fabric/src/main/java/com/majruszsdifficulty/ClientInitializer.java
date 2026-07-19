package com.majruszsdifficulty;

import cc.sighs.oelib.event.EventBus;
import com.majruszsdifficulty.effects.bleeding.BleedingGui;
import com.majruszsdifficulty.events.ClientItemTooltipEvent;
import com.majruszsdifficulty.items.EnderiumShardLocator;
import com.majruszsdifficulty.items.SoulJar;
import com.majruszsdifficulty.items.TreasureBag;
import com.majruszsdifficulty.particles.BloodParticle;
import com.majruszsdifficulty.registration.ClientRegistration;
import com.majruszsdifficulty.registry.ModItems;
import com.majruszsdifficulty.registry.ModParticles;
import com.majruszsdifficulty.resource.ModResources;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents;
import net.fabricmc.fabric.api.object.builder.v1.client.model.FabricModelPredicateProviderRegistry;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;

public final class ClientInitializer implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientRegistration.register();
        FabricModelPredicateProviderRegistry.register(
                ModItems.ENDERIUM_SHARD_LOCATOR_ITEM.get(),
                ResourceLocation.parse("shard_distance"),
                EnderiumShardLocator.Client::getShardDistance
        );
        ParticleFactoryRegistry.getInstance().register(ModParticles.BLOOD_PARTICLE.get(), BloodParticle.Factory::new);
        ItemTooltipCallback.EVENT.register((stack, context, flags, lines) -> EventBus.post(
                new ClientItemTooltipEvent(stack, context, flags, lines, Minecraft.getInstance().player)
        ));
        ColorProviderRegistry.ITEM.register(SoulJar.Client::getColor, ModItems.SOUL_JAR_ITEM.get());
        HudRenderCallback.EVENT.register((graphics, tickCounter) -> BleedingGui.render(graphics, tickCounter.getGameTimeDeltaPartialTick(true)));
        ScreenEvents.AFTER_INIT.register((client, screen, width, height) ->
                ScreenMouseEvents.allowMouseClick(screen).register((current, mouseX, mouseY, button) ->
                        !TreasureBag.Client.handleInventoryClick(current, button)
                )
        );
        ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
            @Override
            public ResourceLocation getFabricId() {
                return MajruszsDifficulty.id("client_resources");
            }

            @Override
            public void onResourceManagerReload(ResourceManager manager) {
                ModResources.reloadClient(manager);
            }
        });
    }
}
