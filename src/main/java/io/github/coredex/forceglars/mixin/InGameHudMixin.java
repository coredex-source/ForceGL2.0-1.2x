package io.github.coredex.forceglars.mixin;

import io.github.coredex.forceglars.hud.InteractiveHUD;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class InGameHudMixin {
    
    @Inject(method = "render", at = @At("TAIL"), require = 0)
    private void onRender(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        // Use tickDelta field directly from the RenderTickCounter
        InteractiveHUD.render(context, tickCounter.getDynamicDeltaTicks());
    }
}
