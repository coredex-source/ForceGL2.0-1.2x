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
        
        // Update ARS settings
        if (ForceGLARSConfig.CONFIG.instance().adaptiveRenderScalingEnabled) {
            PerformanceMonitor.MIN_FPS_THRESHOLD = ForceGLARSConfig.CONFIG.instance().minFpsThreshold;
            PerformanceMonitor.MAX_FPS_THRESHOLD = ForceGLARSConfig.CONFIG.instance().maxFpsThreshold;
            PerformanceMonitor.FPS_UPDATE_INTERVAL_MS = ForceGLARSConfig.CONFIG.instance().updateInterval;
            PerformanceMonitor.FPS_CHECK_INTERVAL_MS = ForceGLARSConfig.CONFIG.instance().checkInterval;
            AdaptiveChunkScaling.MAX_RENDER_DISTANCE = ForceGLARSConfig.CONFIG.instance().maxRenderDistance;
            AdaptiveChunkScaling.MIN_RENDER_DISTANCE = ForceGLARSConfig.CONFIG.instance().minRenderDistance;
        } else if (ForceGLOptionsScreen.ARScalingEnabled && !ForceGLARSConfig.CONFIG.instance().adaptiveRenderScalingEnabled){
            ForceGLOptionsScreen.ARScalingEnabled = ForceGLARSConfig.CONFIG.instance().adaptiveRenderScalingEnabled;
            ForceGLARS.isListenerActive = false;
        }
        
        // Apply render compatibility changes
        if (ForceGLARSConfig.CONFIG.instance().renderCompatibilityEnabled) {
            RenderCompatibilityManager.applySodiumCompatibility();
        }
    }
}
