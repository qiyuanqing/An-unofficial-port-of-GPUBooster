package com.rapidyne;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

// Shows a one-time toast on first world join, reminding the player that Rapidyne's
// settings (fast-math on/off) are adjustable in the mod's config screen.
@EventBusSubscriber(modid = Rapidyne.MODID, value = Dist.CLIENT)
public final class WelcomeNotice {

    private static final SystemToast.SystemToastId TOAST_ID = new SystemToast.SystemToastId();
    private static boolean shownThisSession = false;

    @SubscribeEvent
    public static void onLoggingIn(ClientPlayerNetworkEvent.LoggingIn event) {
        if (shownThisSession) {
            return;
        }
        shownThisSession = true;

        Path marker = Minecraft.getInstance().gameDirectory.toPath().resolve("config").resolve("rapidyne_welcome_shown.txt");
        if (Files.exists(marker)) {
            return;
        }

        try {
            Files.createDirectories(marker.getParent());
            Files.createFile(marker);
        } catch (IOException e) {
            Rapidyne.LOGGER.warn("Could not write welcome-notice marker file, will show the toast again next time.", e);
        }

        SystemToast.add(
                Minecraft.getInstance().getToastManager(),
                TOAST_ID,
                Component.translatable("rapidyne.toast.welcome.title"),
                Component.translatable("rapidyne.toast.welcome.message")
        );
    }

    private WelcomeNotice() {}
}
