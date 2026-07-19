package com.majruszsdifficulty;

import cc.sighs.oelib.event.EventAutoRegistration;
import cc.sighs.oelib.network.api.NetworkManager;
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;

public final class NetworkBootstrap implements PreLaunchEntrypoint {
    @Override
    public void onPreLaunch() {
        NetworkManager.registerPacketScanPackage("com.majruszsdifficulty.network");
        EventAutoRegistration.registerBasePackage("com.majruszsdifficulty");
    }
}
