package io.github.coredex.forceglars;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.coredex.forceglars.config.ForceGLARSConfig;

public class ForceGLARSMod implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("ForceGL");

    @Override
    public void onInitialize() {
        // Load the configuration on startup
        ForceGLARSConfig.CONFIG.load();
        LOGGER.info("ForceGL configuration loaded.");
    }
}
