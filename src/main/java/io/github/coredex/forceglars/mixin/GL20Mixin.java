package io.github.coredex.forceglars.mixin;

import net.minecraft.client.util.Window;

import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import io.github.coredex.forceglars.ForceGLARS;
import io.github.coredex.forceglars.override.HintOverride;
import io.github.coredex.forceglars.override.OverrideType;

@Mixin(Window.class)
public class GL20Mixin {

    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Lorg/lwjgl/glfw/GLFW;glfwWindowHint(II)V", remap = false))
    private void windowHintOverride(int hint, int value) {
        if (ForceGLARS.GLFW_OVERRIDE_VALUES.containsKey(hint)) {
            int original = value;

            String hintName = "Unknown";
            if (ForceGLARS.GLFW_HINT_NAMES.containsKey(hint)) {
                hintName = ForceGLARS.GLFW_HINT_NAMES.get(hint);
            }

            HintOverride override = ForceGLARS.GLFW_OVERRIDE_VALUES.get(hint);
            OverrideType type = override.getOverrideType();
            value = override.getValue();

            ForceGLARS.LOGGER.info("Overriding " + hintName + ": " + original + " -> " + (type == OverrideType.DO_NOT_SET ? "None" : value));
            if (type == OverrideType.DO_NOT_SET) {
                return;
            }
        }
        GLFW.glfwWindowHint(hint, value);
    }
}
