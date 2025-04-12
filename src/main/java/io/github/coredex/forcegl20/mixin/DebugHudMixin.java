package io.github.coredex.forcegl20.mixin;

import io.github.coredex.forcegl20.ForceGL20;
import io.github.coredex.forcegl20.config.ForceGL20Config;
import io.github.coredex.forcegl20.config.ForceGLOptionsScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.DebugHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(DebugHud.class)
public class DebugHudMixin {
    
    @Inject(method = "getRightText", at = @At("RETURN"), require = 0)
    private void onGetRightText(CallbackInfoReturnable<List<String>> cir) {
        List<String> debugInfo = cir.getReturnValue();
        
        // Add mod version and status info
        debugInfo.add("");
        debugInfo.add("§6§lForceGL2.0-ARS §r§7(" + ForceGL20.MOD_VERSION + ")");
        debugInfo.add("§7Status: " + (ForceGL20Config.CONFIG.instance().modEnabled ? "§aEnabled" : "§cDisabled"));
        
        if (ForceGL20Config.CONFIG.instance().modEnabled) {
            // Add OpenGL info
            int contextVersionMajor = ForceGL20Config.CONFIG.instance().contextVersionMajor;
            debugInfo.add("§7OpenGL Version: §f" + 
                (ForceGL20Config.CONFIG.instance().forceCompatibilityMode ? "2.0 (Forced)" : contextVersionMajor + ".0"));
            
            if (ForceGL20Config.CONFIG.instance().forceCompatibilityMode) {
                debugInfo.add("§7Compatibility Mode: §aActive");
                debugInfo.add("§7VBO Disabled: " + 
                    (ForceGL20Config.CONFIG.instance().disableVBO ? "§aYes" : "§cNo"));
            }
        }
        
        // Add ARS info if enabled
        if (ForceGLOptionsScreen.ARScalingEnabled) {
            debugInfo.add("");
            debugInfo.add("§6ARS Information:");
            
            int currentDistance = MinecraftClient.getInstance().options.getViewDistance().getValue();
            debugInfo.add("§7Current Render Distance: §f" + currentDistance);
            debugInfo.add("§7Range: §f" + ForceGL20Config.CONFIG.instance().minRenderDistance + 
                          "§7 to §f" + ForceGL20Config.CONFIG.instance().maxRenderDistance);
            debugInfo.add("§7Default: §f" + ForceGL20Config.CONFIG.instance().defaultRenderDistance);
            
            // Show thresholds
            debugInfo.add("§7FPS Thresholds: §f" + ForceGL20Config.CONFIG.instance().minFpsThreshold + 
                          "§7/§f" + ForceGL20Config.CONFIG.instance().maxFpsThreshold);
        }
    }
}
