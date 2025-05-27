package io.github.coredex.forceglars.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.DebugHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import io.github.coredex.forceglars.ForceGLARS;
import io.github.coredex.forceglars.config.ForceGLARSConfig;
import io.github.coredex.forceglars.config.ForceGLOptionsScreen;

import java.util.List;

@Mixin(DebugHud.class)
public class DebugHudMixin {
    
    @Inject(method = "getRightText", at = @At("RETURN"), require = 0)
    private void onGetRightText(CallbackInfoReturnable<List<String>> cir) {
        List<String> debugInfo = cir.getReturnValue();
        
        // Add mod version and status info
        debugInfo.add("");
        debugInfo.add("§6§lForceGL2.0-ARS §r§7(" + ForceGLARS.MOD_VERSION + ")");
        debugInfo.add("§7Status: " + (ForceGLARSConfig.CONFIG.instance().modEnabled ? "§aEnabled" : "§cDisabled"));
        
        if (ForceGLARSConfig.CONFIG.instance().modEnabled) {
            // Add OpenGL info
            int contextVersionMajor = ForceGLARSConfig.CONFIG.instance().contextVersionMajor;
            debugInfo.add("§7OpenGL Version: §f" + 
                (ForceGLARSConfig.CONFIG.instance().forceCompatibilityMode ? "2.0 (Forced)" : contextVersionMajor + ".0"));
            
            if (ForceGLARSConfig.CONFIG.instance().forceCompatibilityMode) {
                debugInfo.add("§7Compatibility Mode: §aActive");
                debugInfo.add("§7VBO Disabled: " + 
                    (ForceGLARSConfig.CONFIG.instance().disableVBO ? "§aYes" : "§cNo"));
            }
        }
        
        // Add ARS info if enabled
        if (ForceGLOptionsScreen.ARScalingEnabled) {
            debugInfo.add("");
            debugInfo.add("§6ARS Information:");
            
            int currentDistance = MinecraftClient.getInstance().options.getViewDistance().getValue();
            debugInfo.add("§7Current Render Distance: §f" + currentDistance);
            debugInfo.add("§7Range: §f" + ForceGLARSConfig.CONFIG.instance().minRenderDistance + 
                          "§7 to §f" + ForceGLARSConfig.CONFIG.instance().maxRenderDistance);
            debugInfo.add("§7Default: §f" + ForceGLARSConfig.CONFIG.instance().defaultRenderDistance);
            
            // Show thresholds
            debugInfo.add("§7FPS Thresholds: §f" + ForceGLARSConfig.CONFIG.instance().minFpsThreshold + 
                          "§7/§f" + ForceGLARSConfig.CONFIG.instance().maxFpsThreshold);
        }
    }
}
