package com.majruszsdifficulty.internal.platform;

import cc.sighs.oelib.platform.Platform;
import com.majruszsdifficulty.internal.annotation.Dist;
import com.majruszsdifficulty.internal.annotation.OnlyIn;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.server.MinecraftServer;

import java.util.function.Supplier;

public class Side {
    public static void run(Supplier<Runnable> logicalClient, Supplier<Runnable> logicalServer) {
        (Side.isLogicalClient() ? logicalClient : logicalServer).get().run();
    }

    public static <Type> Type get(Supplier<Supplier<Type>> logicalClient, Supplier<Supplier<Type>> logicalServer) {
        return (Side.isLogicalClient() ? logicalClient : logicalServer).get().get();
    }

    public static void runOnClient(Supplier<Runnable> supplier) {
        if (Side.isClient()) {
            supplier.get().run();
        }
    }

    public static boolean isLogicalClient() {
        return Side.isClient()
                && Side.getMinecraft().isSameThread();
    }

    public static boolean isLogicalServer() {
        return !Side.isLogicalClient();
    }

    public static boolean isDevBuild() {
        return Platform.isDevelopmentEnv();
    }

    public static boolean isDedicatedServer() {
        return Platform.isServer();
    }

    public static boolean isClient() {
        return Platform.isClient();
    }

    public static boolean canLoadClassOnServer(String annotations) {
        return (!annotations.contains("Lcom/majruszsdifficulty/internal/annotation/Dist;")
                && !annotations.contains("Lnet/fabricmc/api/EnvType;")
                && !annotations.contains("Lnet/neoforged/api/distmarker/Dist;"))
                || !annotations.contains("CLIENT");
    }

    public static MinecraftServer getServer() {
        return Platform.getCurrentServer();
    }

    @OnlyIn(Dist.CLIENT)
    public static Minecraft getMinecraft() {
        return Minecraft.getInstance();
    }

    @OnlyIn(Dist.CLIENT)
    public static ClientLevel getLocalLevel() {
        return Minecraft.getInstance().level;
    }

    @OnlyIn(Dist.CLIENT)
    public static LocalPlayer getLocalPlayer() {
        return Minecraft.getInstance().player;
    }
}
