package io.github.coredex.forceglars.mixin;

import io.github.coredex.forceglars.ForceGLARS;
import io.github.coredex.forceglars.config.ForceGLARSConfig;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30C;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.nio.ByteBuffer;

/**
 * Mixin to intercept OpenGL 3.0+ calls that don't exist in OpenGL 2.0
 * This prevents crashes when mods like Sodium try to use GL30 functions
 */
@Mixin(value = GL30C.class, remap = false)
public class GL30CMixin {
    
    private static boolean hasLoggedFragDataWarning = false;
    private static boolean hasLoggedVertexAttribIWarning = false;
    
    /**
     * Intercept glBindFragDataLocation calls
     * This function doesn't exist in OpenGL 2.0, so we skip it entirely
     * The default fragment data binding (colorNumber = 0) is usually sufficient
     */
    @Inject(
        method = "glBindFragDataLocation(IILjava/lang/CharSequence;)V",
        at = @At("HEAD"),
        cancellable = true,
        remap = false
    )
    private static void onBindFragDataLocation(int program, int colorNumber, CharSequence name, CallbackInfo ci) {
        // Only intercept if we're forcing OpenGL 2.x
        if (ForceGLARSConfig.CONFIG.instance().contextVersionMajor <= 2) {
            if (!hasLoggedFragDataWarning) {
                ForceGLARS.LOGGER.warn("Intercepted glBindFragDataLocation call - this function doesn't exist in OpenGL 2.0");
                ForceGLARS.LOGGER.warn("Using default fragment data location binding (should work for most shaders)");
                hasLoggedFragDataWarning = true;
            }
            
            // Cancel the original call to prevent the native crash
            ci.cancel();
        }
    }
    
    /**
     * Intercept glVertexAttribIPointer calls
     * This function is for integer vertex attributes (GL30+)
     * We'll fall back to regular glVertexAttribPointer from GL20
     */
    @Inject(
        method = "glVertexAttribIPointer(IIIILjava/nio/ByteBuffer;)V",
        at = @At("HEAD"),
        cancellable = true,
        remap = false
    )
    private static void onVertexAttribIPointerBuffer(int index, int size, int type, int stride, ByteBuffer pointer, CallbackInfo ci) {
        if (ForceGLARSConfig.CONFIG.instance().contextVersionMajor <= 2) {
            if (!hasLoggedVertexAttribIWarning) {
                ForceGLARS.LOGGER.warn("Intercepted glVertexAttribIPointer call - falling back to glVertexAttribPointer (GL2.0)");
                ForceGLARS.LOGGER.warn("Integer vertex attributes will be converted to floats (may cause visual issues)");
                hasLoggedVertexAttribIWarning = true;
            }
            
            // Use GL20 version instead (converts integers to normalized floats)
            GL20.glVertexAttribPointer(index, size, type, false, stride, pointer);
            ci.cancel();
        }
    }
    
    /**
     * Intercept glVertexAttribIPointer calls (long pointer version)
     */
    @Inject(
        method = "glVertexAttribIPointer(IIIIJ)V",
        at = @At("HEAD"),
        cancellable = true,
        remap = false
    )
    private static void onVertexAttribIPointerLong(int index, int size, int type, int stride, long pointer, CallbackInfo ci) {
        if (ForceGLARSConfig.CONFIG.instance().contextVersionMajor <= 2) {
            if (!hasLoggedVertexAttribIWarning) {
                ForceGLARS.LOGGER.warn("Intercepted glVertexAttribIPointer call - falling back to glVertexAttribPointer (GL2.0)");
                ForceGLARS.LOGGER.warn("Integer vertex attributes will be converted to floats (may cause visual issues)");
                hasLoggedVertexAttribIWarning = true;
            }
            
            // Use GL20 version instead (converts integers to normalized floats)
            GL20.glVertexAttribPointer(index, size, type, false, stride, pointer);
            ci.cancel();
        }
    }
}
