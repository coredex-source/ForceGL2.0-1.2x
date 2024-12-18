package io.github.coredex.forcegl20.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import io.github.coredex.forcegl20.config.ForceGLOptionsScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(OptionsScreen.class)
public abstract class OptionsScreenMixin extends Screen {

    private ButtonWidget forceGLOptionsButton;

    protected OptionsScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void addForceGLOptionsButton(CallbackInfo info) {
        // Iterate over children to find the "Skin Customization" button
        for (var child : this.children()) {
            if (child instanceof ButtonWidget buttonWidget && buttonWidget.getMessage().getString().equals("Skin Customization...")) {
                // Add the "ForceGL Options" button above it
                forceGLOptionsButton = ButtonWidget.builder(
                    Text.of("ForceGL Options"),
                    button -> {
                        try {
                            // Pass the current OptionsScreen instance as the parent
                            var constructor = ForceGLOptionsScreen.class.getDeclaredConstructor(Screen.class, Text.class);
                            constructor.setAccessible(true);
                            MinecraftClient.getInstance().setScreen(constructor.newInstance(this, Text.of("ForceGL Options")));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                ).dimensions(
                    buttonWidget.getX(), // Get x position
                    buttonWidget.getY() - 24, // Place above
                    buttonWidget.getWidth(),
                    buttonWidget.getHeight()
                ).build();

                this.addDrawableChild(forceGLOptionsButton);
                break;
            }
        }
    }

    @Override
    public void render(DrawContext drawContext, int mouseX, int mouseY, float delta) {
        if (forceGLOptionsButton != null) {
            for (var child : this.children()) {
                if (child instanceof ButtonWidget buttonWidget && buttonWidget.getMessage().getString().equals("Skin Customization...")) {
                    forceGLOptionsButton.setX(buttonWidget.getX());
                    forceGLOptionsButton.setY(buttonWidget.getY() - 24);
                    break;
                }
            }
        }
        super.render(drawContext, mouseX, mouseY, delta);
    }
}
