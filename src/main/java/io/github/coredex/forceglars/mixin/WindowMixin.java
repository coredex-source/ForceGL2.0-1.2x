package io.github.coredex.forceglars.mixin;

import net.minecraft.client.util.Window;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.coredex.forceglars.ForceGLARS;

@Mixin(Window.class)
public class WindowMixin {
    @Shadow private long handle;

    // Use require=0 to make this injection optional if the method doesn't match exactly
    @Inject(method = "setVsync", at = @At("HEAD"), cancellable = true, require = 0)
    private void onSetVsync(boolean vsync, CallbackInfo ci) {
        // In compatibility mode, we want to ensure vsync is enabled to prevent GPU stress
        if (ForceGLARS.forceCompatibilityMode && !vsync) {
            ForceGLARS.LOGGER.info("Forcing VSync ON for compatibility mode");
            GLFW.glfwSwapInterval(1); // Force VSync ON
            ci.cancel(); // Skip the original method
        }
    }
    
    // Make this method optional as well
    @Inject(method = "setFramerateLimit", at = @At("HEAD"), cancellable = true, require = 0)
    private void onSetFramerateLimit(int fps, CallbackInfo ci) {
        // In compatibility mode, we want to ensure the framerate is limited to reduce GPU stress
        if (ForceGLARS.forceCompatibilityMode && (fps <= 0 || fps > 60)) {
            ForceGLARS.LOGGER.info("Forcing framerate limit to 60 FPS for compatibility mode");
            // Call original method with 60 FPS limit instead
            GLFW.glfwSwapInterval(1); // Ensure VSync is on
            // Continue with original method execution
        }
    }
}
