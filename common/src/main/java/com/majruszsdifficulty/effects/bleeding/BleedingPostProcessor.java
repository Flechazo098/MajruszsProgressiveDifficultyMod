package com.majruszsdifficulty.effects.bleeding;

import com.majruszsdifficulty.MajruszsDifficulty;
import com.majruszsdifficulty.config.ClientVisualConfig;
import com.majruszsdifficulty.internal.annotation.Dist;
import com.majruszsdifficulty.internal.annotation.OnlyIn;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.client.renderer.PostPass;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.ResourceLocation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

@OnlyIn(Dist.CLIENT)
public final class BleedingPostProcessor {
    private static final ResourceLocation POST_CHAIN = MajruszsDifficulty.id("shaders/post/blood.json");
    private static final ResourceLocation MASK_TEXTURE = MajruszsDifficulty.id("dynamic/blood_material");
    private static final String COMPOSITE_PROGRAM = "majruszsdifficulty/blood_composite";
    private static final String SWAP_TARGET = "blood_swap";
    private static final int MASK_HEIGHT = 288;
    private static final int MIN_MASK_WIDTH = 192;
    private static final int MAX_MASK_WIDTH = 1024;
    private static final int LIFETIME = 20 * 9;
    private static final int MAX_SPLATS = 64;
    private static final List<BloodSplat> SPLATS = new ArrayList<>();
    private static final AtomicInteger GENERATION = new AtomicInteger();

    private static PostChain postChain;
    private static PostPass compositePass;
    private static DynamicTexture maskTexture;
    private static long clientTick;
    private static long maskSnapshotTick;
    private static int screenWidth = -1;
    private static int screenHeight = -1;
    private static int maskWidth = 1;
    private static int maskHeight = 1;
    private static boolean maskDirty;
    private static boolean maskReady;
    private static volatile boolean reloadRequested = true;
    private static volatile boolean shaderFailed;

    public static void addBloodOnScreen(int count) {
        int splatCount = Math.clamp(count, 0, 24);
        if (splatCount == 0) {
            return;
        }

        SPLATS.removeIf(BleedingPostProcessor::hasExpired);
        for (int index = 0; index < splatCount; ++index) {
            SPLATS.add(BloodSplat.create(clientTick));
        }
        while (SPLATS.size() > MAX_SPLATS) {
            SPLATS.removeFirst();
        }
        maskDirty = true;
    }

    public static void tick() {
        Minecraft minecraft = Minecraft.getInstance();
        if (!minecraft.isPaused()) {
            ++clientTick;
            SPLATS.removeIf(BleedingPostProcessor::hasExpired);
        }
    }

    public static boolean shouldUsePostProcessing() {
        return ClientVisualConfig.get().usePostProcessingBlood() && !shaderFailed;
    }

    public static void process(float deltaTick, float partialTick) {
        if (!shouldUsePostProcessing() || SPLATS.isEmpty()) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null || !ensureResources(minecraft)) {
            return;
        }

        if (maskDirty) {
            scheduleMaskGeneration(minecraft);
        }
        if (!maskReady || postChain == null) {
            return;
        }

        float ageOffset = Math.max(0.0f, (clientTick + partialTick - maskSnapshotTick) / LIFETIME);
        compositePass.getEffect().safeGetUniform("AgeOffset").set(ageOffset);
        compositePass.getEffect().safeGetUniform("MaskSize").set(1.0f / maskWidth, 1.0f / maskHeight);

        try {
            RenderSystem.disableBlend();
            RenderSystem.disableDepthTest();
            RenderSystem.resetTextureMatrix();
            postChain.process(deltaTick);
        } catch (RuntimeException exception) {
            fail("Failed to process the bleeding post effect", exception);
        }
    }

    public static void requestReload() {
        reloadRequested = true;
        shaderFailed = false;
        GENERATION.incrementAndGet();
    }

    public static void close() {
        GENERATION.incrementAndGet();
        closePostChain();
        Minecraft minecraft = Minecraft.getInstance();
        if (maskTexture != null) {
            minecraft.getTextureManager().release(MASK_TEXTURE);
            maskTexture = null;
        }
        SPLATS.clear();
        maskReady = false;
    }

    private static boolean ensureResources(Minecraft minecraft) {
        RenderTarget mainTarget = minecraft.getMainRenderTarget();
        boolean resized = screenWidth != mainTarget.viewWidth || screenHeight != mainTarget.viewHeight;
        if (!reloadRequested && !resized && postChain != null) {
            return true;
        }

        reloadRequested = false;
        shaderFailed = false;
        screenWidth = mainTarget.viewWidth;
        screenHeight = mainTarget.viewHeight;
        closePostChain();
        ensureBlankMask(minecraft);

        PostChain chain = null;
        try {
            chain = new PostChain(
                    minecraft.getTextureManager(),
                    minecraft.getResourceManager(),
                    mainTarget,
                    POST_CHAIN
            );
            RenderTarget swapTarget = chain.getTempTarget(SWAP_TARGET);

            PostPass composite = chain.addPass(COMPOSITE_PROGRAM, mainTarget, swapTarget, false);
            composite.addAuxAsset("BloodSampler", BleedingPostProcessor::getMaskTextureId, 1, 1);
            chain.addPass("blit", swapTarget, mainTarget, false);
            chain.resize(screenWidth, screenHeight);
            postChain = chain;
            compositePass = composite;
            maskDirty = true;
            maskReady = false;
            return true;
        } catch (IOException | RuntimeException exception) {
            if (chain != null) {
                chain.close();
            }
            fail("Failed to load the bleeding post effect", exception);
            return false;
        }
    }

    private static void ensureBlankMask(Minecraft minecraft) {
        if (maskTexture != null) {
            return;
        }

        NativeImage image = new NativeImage(1, 1, true);
        DynamicTexture texture = new DynamicTexture(image);
        minecraft.getTextureManager().register(MASK_TEXTURE, texture);
        texture.setFilter(true, false);
        maskTexture = texture;
    }

    private static int getMaskTextureId() {
        return maskTexture != null ? maskTexture.getId() : 0;
    }

    private static void scheduleMaskGeneration(Minecraft minecraft) {
        maskDirty = false;
        maskReady = false;
        int generation = GENERATION.incrementAndGet();
        long snapshotTick = clientTick;
        List<BloodSplat> snapshot = List.copyOf(SPLATS);
        int targetHeight = MASK_HEIGHT;
        int targetWidth = Math.clamp(
                Math.round((float)screenWidth / Math.max(1, screenHeight) * targetHeight),
                MIN_MASK_WIDTH,
                MAX_MASK_WIDTH
        );

        CompletableFuture
                .supplyAsync(
                        () -> new MaskData(
                                BloodMaskGenerator.generate(targetWidth, targetHeight, snapshotTick, LIFETIME, snapshot),
                                targetWidth,
                                targetHeight,
                                snapshotTick
                        ),
                        Util.backgroundExecutor()
                )
                .whenComplete((data, throwable) -> minecraft.execute(() -> {
                    if (throwable != null) {
                        if (generation == GENERATION.get()) {
                            fail("Failed to generate the bleeding material mask", throwable);
                        }
                        return;
                    }
                    if (generation != GENERATION.get()) {
                        data.image().close();
                        return;
                    }
                    uploadMask(minecraft, data);
                }));
    }

    private static void uploadMask(Minecraft minecraft, MaskData data) {
        DynamicTexture texture = new DynamicTexture(data.image());
        minecraft.getTextureManager().register(MASK_TEXTURE, texture);
        texture.setFilter(true, false);
        maskTexture = texture;
        maskWidth = data.width();
        maskHeight = data.height();
        maskSnapshotTick = data.snapshotTick();
        maskReady = true;
    }

    private static boolean hasExpired(BloodSplat splat) {
        return clientTick - splat.bornTick() >= LIFETIME;
    }

    private static void fail(String message, Throwable throwable) {
        if (!shaderFailed) {
            MajruszsDifficulty.LOGGER.error(message, throwable);
        }
        shaderFailed = true;
        closePostChain();
    }

    private static void closePostChain() {
        if (postChain != null) {
            postChain.close();
            postChain = null;
        }
        compositePass = null;
    }

    private record MaskData(NativeImage image, int width, int height, long snapshotTick) {
    }
}
