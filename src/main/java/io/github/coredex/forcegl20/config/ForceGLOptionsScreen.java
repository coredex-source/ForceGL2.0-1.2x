package io.github.coredex.forcegl20.config;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.text.Text;

public class ForceGLOptionsScreen extends Screen {
    private final Screen parent;
    private int openGLVersion = ForceGL20Config.CONFIG.instance().contextVersionMajor;
    private boolean modEnabled = ForceGL20Config.CONFIG.instance().modEnabled; 
    public static boolean ARScalingEnabled = ForceGL20Config.CONFIG.instance().adaptiveRenderScalingEnabled;

    public ForceGLOptionsScreen(Screen parent, Text title) {
        super(title);
        this.parent = parent; 
    }

    @Override
    protected void init() {
        ButtonWidget toggleModButton = ButtonWidget.builder(
            Text.literal("Enable GL Mod: " + (modEnabled ? "ON" : "OFF")),
            button -> {
                modEnabled = !modEnabled;
                button.setMessage(Text.literal("Enable GL Mod: " + (modEnabled ? "ON" : "OFF")));
            }
        ).dimensions(
            this.width / 2 - 100,
            this.height / 2 - 60,
            200,
            20
        ).build();

        this.addDrawableChild(toggleModButton);

        this.addDrawableChild(new SliderWidget(this.width / 2 - 100, this.height / 2 - 20, 200, 20,
                Text.literal("OpenGL Version: " + openGLVersion), openGLVersion / 4.0) {
            @Override
            protected void updateMessage() {
                this.setMessage(Text.literal("OpenGL Version: " + openGLVersion));
            }

            @Override
            protected void applyValue() {
                openGLVersion = (int) Math.round(this.value * 4);
            }
        });

        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("Save"),
                button -> {
                    ForceGL20Config.CONFIG.instance().contextVersionMajor = openGLVersion;
                    ForceGL20Config.CONFIG.instance().modEnabled = modEnabled;
                    ForceGL20Config.CONFIG.save();
                    if (this.client != null) this.client.setScreen(parent);
                }
        ).dimensions(this.width / 2 - 155, this.height / 2 + 40, 150, 20).build());

        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("Back"),
                button -> {
                    if (this.client != null) this.client.setScreen(parent);
                }
        ).dimensions(this.width / 2 + 5, this.height / 2 + 40, 150, 20).build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 15, 0xFFFFFF);

        String note = "Note: Changing the GL version will require a restart. (Mod default = 2)";
        context.drawCenteredTextWithShadow(
            this.textRenderer,
            Text.literal(note),
            this.width / 2,
            this.height / 2 + 5,
            0xAAAAAA
        );

        String compatibilityNote = "For best compatibility use 3 or 4 if your graphics card supports it.";
        context.drawCenteredTextWithShadow(
            this.textRenderer,
            Text.literal(compatibilityNote),
            this.width / 2,
            this.height / 2 + 25,
            0xAAAAAA
        );
    }
}
