package io.github.coredex.forcegl20;

import com.google.common.collect.ImmutableMap;

import dev.isxander.yacl3.platform.YACLPlatform;
import io.github.coredex.forcegl20.AdaptiveRenderScaling.AdaptiveChunkScaling;
import io.github.coredex.forcegl20.AdaptiveRenderScaling.PerformanceMonitor;
import io.github.coredex.forcegl20.config.DynamicConfigUpdates;
import io.github.coredex.forcegl20.config.ForceGL20Config;
import io.github.coredex.forcegl20.override.HintOverride;
import io.github.coredex.forcegl20.override.OverrideType;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.loader.api.FabricLoader;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.github.coredex.forcegl20.utils.FileWatcher;
import java.nio.file.Path;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ForceGL20 {

    public static final Logger LOGGER = LoggerFactory.getLogger("ForceGL");

    public static final ImmutableMap<Integer, HintOverride> GLFW_OVERRIDE_VALUES;
    public static final ImmutableMap<Integer, String> GLFW_HINT_NAMES;
    public static final Map<String, Boolean> COMPATIBILITY_FLAGS = new HashMap<>();
    private static final PerformanceMonitor PERFORMANCE_MONITOR = new PerformanceMonitor();
    public static boolean isListenerActive = true;
    public static boolean forceCompatibilityMode = false;

    private static final Set<Integer> GLFW_HINT_CODES = Set.of(
            0x00020001, 0x00020002, 0x00020003, 0x00020004, 0x00020005, 0x00020006,
            0x00020007, 0x00020008, 0x00020009, 0x0002000A, 0x0002000B, 0x0002000C,
            0x00021001, 0x00021002, 0x00021003, 0x00021004, 0x00021005, 0x00021006,
            0x00021007, 0x00021008, 0x00021009, 0x0002100A, 0x0002100B, 0x0002100C,
            0x0002100D, 0x0002100E, 0x0002100F, 0x00021010, 0x00022001, 0x00022002,
            0x00022003, 0x00022004, 0x00022005, 0x00022006, 0x00022007, 0x00022008,
            0x00022009, 0x0002200A, 0x0002200B, 0x0002200C, 0x00023001, 0x00023002,
            0x00023003, 0x00024001, 0x00024002
    );

    static {
        boolean irisPresent = FabricLoader.getInstance().isModLoaded("iris");
        boolean immediatelyFastPresent = FabricLoader.getInstance().isModLoaded("immediatelyfast");
        boolean ARSEnabled = ForceGL20Config.CONFIG.instance().adaptiveRenderScalingEnabled;
        boolean modEnabled = ForceGL20Config.CONFIG.instance().modEnabled;
        boolean irisIFOverride = ForceGL20Config.CONFIG.instance().irisIFOverride;
        forceCompatibilityMode = ForceGL20Config.CONFIG.instance().forceCompatibilityMode;

        LOGGER.info("Initializing ForceGL20 ConfigWatcher...");

        Path configFilePath = YACLPlatform.getConfigDir().resolve("forcegl20.json");
        FileWatcher fileWatcher = new FileWatcher(configFilePath, () -> {
            try {
                LOGGER.info("Configuration file changed. Reloading...");
                ForceGL20Config.CONFIG.load();
                DynamicConfigUpdates.applyDynamicChanges();
            } catch (Exception e) {
                LOGGER.error("Failed to reload configuration: ", e);
            }
        });

        Thread watcherThread = new Thread(fileWatcher, "ForceGL20-ConfigWatcher");
        watcherThread.setDaemon(true);
        watcherThread.start();

        // Initialize compatibility flags
        COMPATIBILITY_FLAGS.put("DISABLE_SHADER_COMPILATIONS", forceCompatibilityMode);
        COMPATIBILITY_FLAGS.put("FORCE_LEGACY_RENDERING", forceCompatibilityMode);
        COMPATIBILITY_FLAGS.put("DISABLE_VBO", forceCompatibilityMode && ForceGL20Config.CONFIG.instance().disableVBO);
        COMPATIBILITY_FLAGS.put("USE_LEGACY_BUFFER_RENDERING", forceCompatibilityMode);

        if (forceCompatibilityMode) {
            LOGGER.info("ForceGL2.0 Compatibility Mode is ENABLED. Using maximum compatibility settings for OpenGL 2.0.");

            // Set system property to further assist with OpenGL 2.0 compatibility
            System.setProperty("org.lwjgl.opengl.Display.allowSoftwareOpenGL", "true");

            // These properties help with older GL drivers
            System.setProperty("org.lwjgl.opengl.Display.noinput", "true");
            System.setProperty("org.lwjgl.util.NoChecks", "true");

            // Additional flags for extreme compatibility cases
            System.setProperty("java.awt.headless", "false");
            System.setProperty("org.lwjgl.glfw.checkThread0", "false");

            // Force software rendering paths when possible
            System.setProperty("sun.java2d.d3d", "false");
            System.setProperty("sun.java2d.opengl", "false");

            // Disable advanced features that might not be supported
            if (ForceGL20Config.CONFIG.instance().disableVBO) {
                LOGGER.info("VBO/Advanced vertex features disabled for maximum compatibility");
                System.setProperty("joml.format.decimals", "3"); // Reduce precision for older GPUs
                // Disable fancy graphics and smooth lighting by default in compatibility mode
                System.setProperty("fml.ignoreOptifine", "true");
                // Set additional flags that might help with older GPUs
                System.setProperty("forge.forceNoStencil", "true");
            }

            // Enable compatibility logging
            LOGGER.info("Using OpenGL version: 2.0");
            LOGGER.info("Using legacy rendering pipeline for maximum compatibility");

            // Register a shutdown hook to perform clean exit - sometimes helps with older GPUs
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                LOGGER.info("ForceGL performing clean shutdown for compatibility mode");
            }));
        }

        if (ARSEnabled){
            ClientTickEvents.END_CLIENT_TICK.register(client -> {
                if (isListenerActive) {
                    PERFORMANCE_MONITOR.tick();
                }
            });
            ClientLifecycleEvents.CLIENT_STARTED.register(client -> {
                AdaptiveChunkScaling.setDefaultRenderDistance();
                LOGGER.info("Default render distance set to {}.", ForceGL20Config.CONFIG.instance().defaultRenderDistance);
            });
            LOGGER.warn("Adaptive Render Scaling is Enabled");
        } else {
            LOGGER.warn("Adaptive Render Scaling is Disabled");
        }
        if (irisPresent && immediatelyFastPresent && !irisIFOverride) {
            LOGGER.warn("ForceGL is disabled because it can be incompatible with Iris and ImmediatelyFast if both are used together and shaders are being used. Override this behavior by changing \"irisIFOverride\" to true in the config manually or by using ModMenu/YACL.");
            GLFW_OVERRIDE_VALUES = ImmutableMap.of();
            GLFW_HINT_NAMES = ImmutableMap.of();
        } else {
            if (irisPresent && immediatelyFastPresent && irisIFOverride) {
                LOGGER.info("Iris-ImmediatelyFast compatibility override enabled. Proceeding with ForceGL initialization.");
            }
            if (modEnabled) {
                LOGGER.info("ForceGL mod is enabled. Initializing...");
                GLFW_OVERRIDE_VALUES = createGlfwOverrideValues();
                GLFW_HINT_NAMES = createGlfwHintNames();
            } else {
                LOGGER.info("ForceGL mod is disabled. Skipping initialization.");
                GLFW_OVERRIDE_VALUES = ImmutableMap.of();
                GLFW_HINT_NAMES = ImmutableMap.of();
            }
        }
    }

    private static ImmutableMap<Integer, HintOverride> createGlfwOverrideValues() {
        ImmutableMap.Builder<Integer, HintOverride> overrideBuilder = ImmutableMap.builder();

        int contextVersionMajor = ForceGL20Config.CONFIG.instance().forceCompatibilityMode ? 
            2 : ForceGL20Config.CONFIG.instance().contextVersionMajor;
        
        overrideBuilder.put(GLFW.GLFW_CONTEXT_VERSION_MAJOR, new HintOverride(OverrideType.SET_VALUE, contextVersionMajor));
        overrideBuilder.put(GLFW.GLFW_CONTEXT_VERSION_MINOR, new HintOverride(OverrideType.SET_VALUE, 0));
        overrideBuilder.put(GLFW.GLFW_OPENGL_PROFILE, new HintOverride(OverrideType.SET_VALUE, GLFW.GLFW_OPENGL_ANY_PROFILE));
        overrideBuilder.put(GLFW.GLFW_OPENGL_FORWARD_COMPAT, new HintOverride(OverrideType.SET_VALUE, GLFW.GLFW_FALSE));
        
        if (forceCompatibilityMode) {
            overrideBuilder.put(GLFW.GLFW_CLIENT_API, new HintOverride(OverrideType.SET_VALUE, GLFW.GLFW_OPENGL_API));
            overrideBuilder.put(GLFW.GLFW_CONTEXT_CREATION_API, new HintOverride(OverrideType.SET_VALUE, GLFW.GLFW_NATIVE_CONTEXT_API));
            overrideBuilder.put(GLFW.GLFW_CONTEXT_ROBUSTNESS, new HintOverride(OverrideType.SET_VALUE, GLFW.GLFW_NO_ROBUSTNESS));
        }

        return overrideBuilder.build();
    }

    private static ImmutableMap<Integer, String> createGlfwHintNames() {
        ImmutableMap.Builder<Integer, String> nameBuilder = ImmutableMap.builder();

        Field[] fields = GLFW.class.getDeclaredFields();
        for (Field field : fields) {
            if (!Modifier.isPublic(field.getModifiers()) || !Modifier.isFinal(field.getModifiers())) {
                continue;
            }

            if (field.getType() != int.class) {
                continue;
            }

            String fieldName = field.getName();
            if (fieldName.equals("GLFW_OPENGL_DEBUG_CONTEXT")) {
                continue;
            }

            try {
                int code = field.getInt(null);
                if (GLFW_HINT_CODES.contains(code)) {
                    nameBuilder.put(code, fieldName);
                }
            } catch (IllegalAccessException e) {
                LOGGER.error("Failed to access GLFW field: {}", field.getName(), e);
            }
        }

        return nameBuilder.build();
    }
    
    // Helper method to check if a compatibility flag is enabled
    public static boolean isCompatibilityFlagEnabled(String flagName) {
        return COMPATIBILITY_FLAGS.getOrDefault(flagName, false);
    }

    /**
     * Helper method to handle shader failures gracefully
     * Used by the shader loading system to avoid crashes on older GPUs
     */
    public static void handleShaderFailure(String shaderName, Exception e) {
        if (forceCompatibilityMode) {
            LOGGER.warn("Shader '{}' failed to load but was suppressed in compatibility mode", shaderName);
        } else {
            LOGGER.error("Failed to load shader: {}", shaderName, e);
        }
    }
}
