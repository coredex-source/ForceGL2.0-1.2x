package io.github.coredex.forceglars.config;

import io.github.coredex.forceglars.ForceGLARS;
import io.github.coredex.forceglars.AdaptiveRenderScaling.AdaptiveChunkScaling;
import io.github.coredex.forceglars.AdaptiveRenderScaling.PerformanceMonitor;
import io.github.coredex.forceglars.rendercompat.RenderCompatibilityManager;

public class DynamicConfigUpdates {
    public static void applyDynamicChanges() {
        // Update ForceGL20 compatibility flags
        ForceGLARS.forceCompatibilityMode = ForceGLARSConfig.CONFIG.instance().forceCompatibilityMode;
        ForceGLARS.COMPATIBILITY_FLAGS.put("DISABLE_SHADER_COMPILATIONS", ForceGLARS.forceCompatibilityMode);
        ForceGLARS.COMPATIBILITY_FLAGS.put("FORCE_LEGACY_RENDERING", ForceGLARS.forceCompatibilityMode);
        ForceGLARS.COMPATIBILITY_FLAGS.put("DISABLE_VBO", ForceGLARS.forceCompatibilityMode && 
                                                       ForceGLARSConfig.CONFIG.instance().disableVBO);
        
        // Update ARS settings - apply immediately
        boolean arsEnabled = ForceGLARSConfig.CONFIG.instance().adaptiveRenderScalingEnabled;
        if (arsEnabled) {
            // Enable ARS and update all parameters
            ForceGLOptionsScreen.ARScalingEnabled = true;
            ForceGLARS.isListenerActive = true;
            
            // Update performance monitor thresholds
            PerformanceMonitor.MIN_FPS_THRESHOLD = ForceGLARSConfig.CONFIG.instance().minFpsThreshold;
            PerformanceMonitor.MAX_FPS_THRESHOLD = ForceGLARSConfig.CONFIG.instance().maxFpsThreshold;
            PerformanceMonitor.FPS_UPDATE_INTERVAL_MS = ForceGLARSConfig.CONFIG.instance().updateInterval;
            PerformanceMonitor.FPS_CHECK_INTERVAL_MS = ForceGLARSConfig.CONFIG.instance().checkInterval;
            
            // Update chunk scaling limits
            AdaptiveChunkScaling.MAX_RENDER_DISTANCE = ForceGLARSConfig.CONFIG.instance().maxRenderDistance;
            AdaptiveChunkScaling.MIN_RENDER_DISTANCE = ForceGLARSConfig.CONFIG.instance().minRenderDistance;
            
            // Set default render distance if it's not already set correctly
            AdaptiveChunkScaling.setDefaultRenderDistance();
            
            ForceGLARS.LOGGER.info("ARS settings updated dynamically - Min FPS: {}, Max FPS: {}, Min RD: {}, Max RD: {}", 
                PerformanceMonitor.MIN_FPS_THRESHOLD, PerformanceMonitor.MAX_FPS_THRESHOLD,
                AdaptiveChunkScaling.MIN_RENDER_DISTANCE, AdaptiveChunkScaling.MAX_RENDER_DISTANCE);
        } else {
            // Disable ARS
            ForceGLOptionsScreen.ARScalingEnabled = false;
            ForceGLARS.isListenerActive = false;
            ForceGLARS.LOGGER.info("ARS disabled dynamically");
        }
        
        // Apply render compatibility changes
        if (ForceGLARSConfig.CONFIG.instance().renderCompatibilityEnabled) {
            RenderCompatibilityManager.applySodiumCompatibility();
        }
        
        ForceGLARS.LOGGER.info("Dynamic configuration changes applied successfully");
    }
}
