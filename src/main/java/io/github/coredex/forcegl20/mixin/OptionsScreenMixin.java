package io.github.coredex.forcegl20.mixin;

import net.minecraft.client.MinecraftClient;
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

    protected OptionsScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void addForceGLOptionsButton(CallbackInfo info) {
        boolean buttonAdded = false;

        // Iterate over children to find the "Skin Customisation" button
        for (var child : this.children()) {
            if (child instanceof ButtonWidget buttonWidget && buttonWidget.getMessage().getString().equals("Skin Customization...")) {
                // Log the position of the Skin Customisation button
                System.out.println("Skin Customization button found at: X=" + buttonWidget.getX() + ", Y=" + buttonWidget.getY());

                // Add the "ForceGL Options" button above it
                ButtonWidget forceGLOptionsButton = ButtonWidget.builder(
                    Text.of("ForceGL Options"),
                    button -> {
                        try {
                            var constructor = ForceGLOptionsScreen.class.getDeclaredConstructor(Text.class);
                            constructor.setAccessible(true);
                            MinecraftClient.getInstance().setScreen(constructor.newInstance(Text.of("ForceGL Options")));
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
                buttonAdded = true;
                System.out.println("ForceGL Options button added at: X=" + forceGLOptionsButton.getX() + ", Y=" + forceGLOptionsButton.getY());
                break;
            }
        }

        if (!buttonAdded) {
            System.err.println("Skin Customization button not found, ForceGL Options button was not added.");
        }
    }
}
