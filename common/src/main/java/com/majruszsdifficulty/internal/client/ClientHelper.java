package com.majruszsdifficulty.internal.client;

import com.majruszsdifficulty.internal.annotation.Dist;
import com.majruszsdifficulty.internal.annotation.OnlyIn;
import net.minecraft.client.gui.screens.Screen;

@OnlyIn(Dist.CLIENT)
public class ClientHelper {
    public static boolean wasShiftPressed() {
        return Screen.hasShiftDown();
    }

    public static boolean isShiftDown() {
        return Screen.hasShiftDown();
    }

    public static boolean wasCtrlPressed() {
        return Screen.hasControlDown();
    }

    public static boolean isCtrlDown() {
        return Screen.hasControlDown();
    }

    public static boolean wasLeftAltPressed() {
        return Screen.hasAltDown();
    }

    public static boolean isLeftAltDown() {
        return Screen.hasAltDown();
    }

}
