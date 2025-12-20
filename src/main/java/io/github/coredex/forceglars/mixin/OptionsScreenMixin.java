package io.github.coredex.forceglars.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.coredex.forceglars.config.ForceGLOptionsScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.OptionsScreen;
import net.minecraft.network.chat.Component;

@Mixin(OptionsScreen.class)
public abstract class OptionsScreenMixin extends Screen {

    private Button forceGLOptionsButton;

    protected OptionsScreenMixin(Component title) {
        super(title);
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void addForceGLOptionsButton(CallbackInfo info) {
        // Iterate over children to find the "Skin Customization" button
        for (var child : this.children()) {
            if (child instanceof Button buttonWidget && buttonWidget.getMessage().getString().equals("Skin Customization...")) {
                // Add the "ForceGL Options" button above it
                forceGLOptionsButton = Button.builder(
                    Component.nullToEmpty("ForceGL Options"),
                    button -> {
                        try {
                            // Pass the current OptionsScreen instance as the parent
                            var constructor = ForceGLOptionsScreen.class.getDeclaredConstructor(Screen.class, Component.class);
                            constructor.setAccessible(true);
                            Minecraft.getInstance().setScreen(constructor.newInstance(this, Component.nullToEmpty("ForceGL Options")));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                ).bounds(
                    buttonWidget.getX(), // Get x position
                    buttonWidget.getY() - 24, // Place above
                    buttonWidget.getWidth(),
                    buttonWidget.getHeight()
                ).build();

                this.addRenderableWidget(forceGLOptionsButton);
                break;
            }
        }
    }

    @Override
    public void render(GuiGraphics drawContext, int mouseX, int mouseY, float delta) {
        if (forceGLOptionsButton != null) {
            for (var child : this.children()) {
                if (child instanceof Button buttonWidget && buttonWidget.getMessage().getString().equals("Skin Customization...")) {
                    forceGLOptionsButton.setX(buttonWidget.getX());
                    forceGLOptionsButton.setY(buttonWidget.getY() - 24);
                    break;
                }
            }
        }
        super.render(drawContext, mouseX, mouseY, delta);
    }
}
