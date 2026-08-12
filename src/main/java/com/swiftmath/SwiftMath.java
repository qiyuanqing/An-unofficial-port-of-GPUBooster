package com.swiftmath;

import com.mojang.logging.LogUtils;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import org.slf4j.Logger;

@Mod(value = SwiftMath.MODID, dist = Dist.CLIENT)
public class SwiftMath {

    public static final String MODID = "swiftmath";
    public static final Logger LOGGER = LogUtils.getLogger();

    public SwiftMath(IEventBus modEventBus, ModContainer modContainer) {
        modContainer.registerConfig(net.neoforged.fml.config.ModConfig.Type.CLIENT, SwiftMathConfig.SPEC);
        modContainer.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
        LOGGER.info("SwiftMath (unofficial GPUBooster port) initialized.");
    }
}
