package io.github.coredex.forcegl20.config;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.text.Text;

public class ForceGLOptionsScreen extends Screen {
    private int openGLVersion = ForceGL20Config.CONFIG.instance().contextVersionMajor;

    protected ForceGLOptionsScreen(Text title) {
        super(title);
    }

    @Override
    protected void init() {
        // Slider for OpenGL Version
        this.addDrawableChild(new SliderWidget(this.width / 2 - 100, this.height / 2 - 20, 200, 20,
            Text.literal("OpenGL Version: " + openGLVersion), openGLVersion / 4.0) {
            @Override
            protected void updateMessage() {
                this.setMessage(Text.literal("OpenGL Version: " + openGLVersion));
            }

            @Override
            protected void applyValue() {
                openGLVersion = (int) Math.round(this.value * 4); // Convert to range 1-4
            }
        });

        // Save Button
        this.addDrawableChild(ButtonWidget.builder(
            Text.literal("Save"),
            button -> {
                ForceGL20Config.CONFIG.instance().contextVersionMajor = openGLVersion;
                ForceGL20Config.CONFIG.save(); // Save to file using YACL
                if (this.client != null) this.client.setScreen(null); // Return to the game
            }
        ).dimensions(this.width / 2 - 155, this.height / 2 + 40, 150, 20).build());

        // Back Button
        this.addDrawableChild(ButtonWidget.builder(
            Text.literal("Back"),
            button -> {
                if (this.client != null) this.client.setScreen(null); // Go back to the previous screen
            }
        ).dimensions(this.width / 2 + 5, this.height / 2 + 40, 150, 20).build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta); // Render other elements
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 15, 0xFFFFFF);

        // Draw the note below the slider
        String note = "Note: Changing the GL version will require a restart. (Mod default = 2)";
        context.drawCenteredTextWithShadow(
            this.textRenderer,
            Text.literal(note),
            this.width / 2,
            this.height / 2 + 5,
            0xAAAAAA // Light gray color for the note
        );

        String compatability_note = "For best compatability use 3 or 4 if your graphics card supports it.";
        context.drawCenteredTextWithShadow(
            this.textRenderer,
            Text.literal(compatability_note),
            this.width / 2,
            this.height / 2 + 25,
            0xAAAAAA // Light gray color for the note
        );
    }
}
