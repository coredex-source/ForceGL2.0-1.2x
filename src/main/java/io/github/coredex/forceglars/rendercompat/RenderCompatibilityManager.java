package io.github.coredex.forceglars.rendercompat;

import io.github.coredex.forceglars.ForceGLARS;
import io.github.coredex.forceglars.config.ForceGLARSConfig;
import net.fabricmc.loader.api.FabricLoader;

public class RenderCompatibilityManager {
    private static boolean sodiumDetected = false;
    private static boolean initialized = false;

    public static void initialize() {
        if (initialized) return;
        
        // Detect Sodium mod
        sodiumDetected = FabricLoader.getInstance().isModLoaded("sodium");
        
        if (sodiumDetected) {
            ForceGLARS.LOGGER.info("Sodium mod detected - render compatibility features available");
            
            // Apply early compatibility patches for Sodium
            if (isRenderCompatibilityEnabled() && ForceGLARSConfig.CONFIG.instance().sodiumCompatibilityMode) {
                applySodiumGL2Compatibility();
            }
        }
        
        initialized = true;
    }

    public static boolean isSodiumDetected() {
        return sodiumDetected;
    }

    public static boolean isRenderCompatibilityEnabled() {
        return ForceGLARSConfig.CONFIG.instance().renderCompatibilityEnabled;
    }

    // Apply Sodium compatibility for OpenGL 2.x cards like HD 2000
    public static void applySodiumCompatibility() {
        if (!isRenderCompatibilityEnabled() || !sodiumDetected) {
            return;
        }
        
        ForceGLARS.LOGGER.info("Applying Sodium render compatibility settings...");
        
        if (ForceGLARSConfig.CONFIG.instance().sodiumCompatibilityMode) {
            applySodiumGL2Compatibility();
        }
    }
    
    private static void applySodiumGL2Compatibility() {
        ForceGLARS.LOGGER.info("Applying Sodium OpenGL 2.x compatibility for legacy GPUs...");
        
        // Set system properties to force Sodium into compatibility mode
        if (ForceGLARSConfig.CONFIG.instance().sodiumDisableVertexArrayObjects) {
            System.setProperty("sodium.renderer.vertex_array_objects", "false");
            ForceGLARS.LOGGER.info("Disabled Vertex Array Objects for Sodium");
        }
        
        if (ForceGLARSConfig.CONFIG.instance().sodiumDisableVertexBufferObjects) {
            System.setProperty("sodium.renderer.vertex_buffer_objects", "false");
            ForceGLARS.LOGGER.info("Disabled Vertex Buffer Objects for Sodium");
        }
        
        if (ForceGLARSConfig.CONFIG.instance().sodiumDisableInstancedRendering) {
            System.setProperty("sodium.renderer.instanced_rendering", "false");
            ForceGLARS.LOGGER.info("Disabled Instanced Rendering for Sodium");
        }
        
        if (ForceGLARSConfig.CONFIG.instance().sodiumForceFixedFunction) {
            System.setProperty("sodium.renderer.fixed_function", "true");
            System.setProperty("sodium.renderer.shaders", "false");
            ForceGLARS.LOGGER.info("Forced Fixed Function Pipeline for Sodium");
        }
        
        if (ForceGLARSConfig.CONFIG.instance().sodiumDisableGeometryShaders) {
            System.setProperty("sodium.renderer.geometry_shaders", "false");
            ForceGLARS.LOGGER.info("Disabled Geometry Shaders for Sodium");
        }
        
        if (ForceGLARSConfig.CONFIG.instance().sodiumDisableComputeShaders) {
            System.setProperty("sodium.renderer.compute_shaders", "false");
            ForceGLARS.LOGGER.info("Disabled Compute Shaders for Sodium");
        }
        
        if (ForceGLARSConfig.CONFIG.instance().sodiumUseLegacyChunkRenderer) {
            System.setProperty("sodium.renderer.chunk_renderer", "legacy");
            ForceGLARS.LOGGER.info("Using Legacy Chunk Renderer for Sodium");
        }
        
        // Force OpenGL 2.0 context for Sodium
        System.setProperty("sodium.renderer.gl_version", "2.0");
        System.setProperty("sodium.renderer.gl_legacy", "true");
        
        ForceGLARS.LOGGER.info("Sodium OpenGL 2.x compatibility mode applied - should work on HD 2000 and similar cards");
    }
    
    public static boolean shouldUseSodiumLegacyMode() {
        return isRenderCompatibilityEnabled() && 
               sodiumDetected && 
               ForceGLARSConfig.CONFIG.instance().sodiumCompatibilityMode &&
               (ForceGLARS.forceCompatibilityMode || ForceGLARSConfig.CONFIG.instance().contextVersionMajor <= 2);
    }
}
