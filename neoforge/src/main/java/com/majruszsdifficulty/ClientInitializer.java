package com.majruszsdifficulty;

import cc.sighs.oelib.event.EventBus;
import com.majruszsdifficulty.effects.bleeding.BleedingGui;
import com.majruszsdifficulty.events.ClientItemTooltipEvent;
import com.majruszsdifficulty.items.EnderiumShardLocator;
import com.majruszsdifficulty.items.ScrollItem;
import com.majruszsdifficulty.items.SoulJar;
import com.majruszsdifficulty.items.TreasureBag;
import com.majruszsdifficulty.particles.BloodParticle;
import com.majruszsdifficulty.registry.ModItems;
import com.majruszsdifficulty.registry.ModParticles;
import com.majruszsdifficulty.resource.ModResources;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

import java.util.concurrent.CompletableFuture;

final class ClientInitializer {
    static void onItemTooltip(ItemTooltipEvent event) {
        EventBus.post(new ClientItemTooltipEvent(
                event.getItemStack(), event.getContext(), event.getFlags(), event.getToolTip(), event.getEntity()
        ));
    }

    static void onScreenMousePressed(ScreenEvent.MouseButtonPressed.Pre event) {
        if (TreasureBag.Client.handleInventoryClick(event.getScreen(), event.getButton())) {
            event.setCanceled(true);
        }
    }

    static void onRenderHand(RenderHandEvent event) {
        ScrollItem.Client.modifyFirstPerson(event.getItemStack(), event.getPoseStack());
    }

    static void registerItemColors(RegisterColorHandlersEvent.Item event) {
        event.register(SoulJar.Client::getColor, ModItems.SOUL_JAR_ITEM.get());
    }

    static void registerGuiLayers(RegisterGuiLayersEvent event) {
        event.registerAbove(VanillaGuiLayers.CAMERA_OVERLAYS, MajruszsDifficulty.id("bleeding"),
                (graphics, tickCounter) -> BleedingGui.render(graphics, tickCounter.getGameTimeDeltaPartialTick(true)));
    }

    static void setup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> ItemProperties.register(
                ModItems.ENDERIUM_SHARD_LOCATOR_ITEM.get(),
                ResourceLocation.parse("shard_distance"),
                EnderiumShardLocator.Client::getShardDistance
        ));
    }

    static void registerParticles(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(ModParticles.BLOOD_PARTICLE.get(), BloodParticle.Factory::new);
    }

    static void registerReloadListeners(RegisterClientReloadListenersEvent event) {
        event.registerReloadListener((barrier, manager, preparationProfiler, reloadProfiler, backgroundExecutor, gameExecutor) ->
                CompletableFuture.runAsync(() -> ModResources.reloadClient(manager), backgroundExecutor)
                        .thenCompose(barrier::wait)
        );
    }

    private ClientInitializer() {
    }
}
