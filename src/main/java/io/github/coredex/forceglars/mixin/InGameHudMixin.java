package io.github.coredex.forceglars.mixin;

import io.github.coredex.forceglars.hud.InteractiveHUD;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public class InGameHudMixin {
    
    @Inject(method = "render", at = @At("TAIL"), require = 0)
    private void onRender(GuiGraphics context, DeltaTracker tickCounter, CallbackInfo ci) {
        // Use tickDelta field directly from the RenderTickCounter
        InteractiveHUD.render(context, tickCounter.getGameTimeDeltaTicks());
    }
}
