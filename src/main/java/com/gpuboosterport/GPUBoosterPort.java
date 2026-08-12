package com.gpuboosterport;

import com.mojang.logging.LogUtils;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import org.slf4j.Logger;

@Mod(value = GPUBoosterPort.MODID, dist = Dist.CLIENT)
public class GPUBoosterPort {

    public static final String MODID = "gpuboosterport";
    public static final Logger LOGGER = LogUtils.getLogger();

    public GPUBoosterPort(IEventBus modEventBus, ModContainer modContainer) {
        modContainer.registerConfig(net.neoforged.fml.config.ModConfig.Type.CLIENT, GPUBoosterConfig.SPEC);
        modContainer.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
        LOGGER.info("GPUBoosterPort initialized.");
    }
}
