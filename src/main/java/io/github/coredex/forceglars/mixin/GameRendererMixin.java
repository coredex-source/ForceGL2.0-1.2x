package io.github.coredex.forceglars.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.coredex.forceglars.ForceGLARS;
import net.minecraft.client.renderer.GameRenderer;

@Mixin(GameRenderer.class)
public class GameRendererMixin {

    // Target all methods related to shader loading with require=0 to make it optional
    @Inject(method = "loadPrograms", at = @At("HEAD"), cancellable = true, require = 0)
    private void onLoadPrograms(CallbackInfo ci) {
        // If in compatibility mode, prevent shader program loading to avoid crashes on older GPUs
        if (ForceGLARS.isCompatibilityFlagEnabled("DISABLE_SHADER_COMPILATIONS")) {
            ForceGLARS.LOGGER.info("Bypassing shader program loading due to compatibility mode");
            ci.cancel();
        }
    }
    
    // Target another potential shader loading method with require=0
    @Inject(method = "preloadShaders", at = @At("HEAD"), cancellable = true, require = 0)
    private void onPreloadShaders(CallbackInfo ci) {
        // If in compatibility mode, prevent shader preloading to avoid crashes on older GPUs
        if (ForceGLARS.isCompatibilityFlagEnabled("DISABLE_SHADER_COMPILATIONS")) {
            ForceGLARS.LOGGER.info("Bypassing shader preloading due to compatibility mode");
            ci.cancel();
        }
    }
    
    // Target the shader setup method with require=0
    @Inject(method = "setupShaders", at = @At("HEAD"), cancellable = true, require = 0)
    private void onSetupShaders(CallbackInfo ci) {
        // If in compatibility mode, prevent shader setup to avoid crashes on older GPUs
        if (ForceGLARS.isCompatibilityFlagEnabled("DISABLE_SHADER_COMPILATIONS")) {
            ForceGLARS.LOGGER.info("Bypassing shader setup due to compatibility mode");
            ci.cancel();
        }
    }
}
