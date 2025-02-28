package io.github.coredex.forcegl20;

import io.github.coredex.forcegl20.config.ForceGL20Config;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ForceGL20Mod implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("ForceGL");

    @Override
    public void onInitialize() {
        // Load the configuration on startup
        ForceGL20Config.CONFIG.load();
        LOGGER.info("ForceGL configuration loaded.");
    }
}
