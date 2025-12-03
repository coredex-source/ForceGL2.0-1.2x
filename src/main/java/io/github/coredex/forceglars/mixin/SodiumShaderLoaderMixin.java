package io.github.coredex.forceglars.mixin;

import io.github.coredex.forceglars.sodium.SodiumShaderPatcher;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/**
 * Mixin to intercept Sodium's shader loading and patch the source code
 * Uses @Pseudo to allow compilation without Sodium on classpath
 */
@Pseudo
@Mixin(targets = "net.caffeinemc.mods.sodium.client.gl.shader.ShaderLoader", remap = false)
public class SodiumShaderLoaderMixin {
    
    /**
     * Intercept the shader source string after it's loaded from resources
     * and before it's compiled
     */
    @ModifyVariable(
        method = "getShaderSource",
        at = @At("RETURN"),
        ordinal = 0,
        remap = false,
        require = 0
    )
    private static String patchShaderSource(String source) {
        return SodiumShaderPatcher.patchShaderSource(source);
    }
}
