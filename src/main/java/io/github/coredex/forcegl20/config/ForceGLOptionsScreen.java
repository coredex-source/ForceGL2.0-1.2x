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
    private boolean irisIFOverride = ForceGL20Config.CONFIG.instance().irisIFOverride;

    // Do not touch the line below as it's required in a different class.
    public static boolean ARScalingEnabled = ForceGL20Config.CONFIG.instance().adaptiveRenderScalingEnabled;
    private int minFpsThreshold = ForceGL20Config.CONFIG.instance().minFpsThreshold;
    private int maxFpsThreshold = ForceGL20Config.CONFIG.instance().maxFpsThreshold;
    private int minRenderDistance = ForceGL20Config.CONFIG.instance().minRenderDistance;
    private int maxRenderDistance = ForceGL20Config.CONFIG.instance().maxRenderDistance;
    private int defaultRenderDistance = ForceGL20Config.CONFIG.instance().defaultRenderDistance;
    private int checkInterval = ForceGL20Config.CONFIG.instance().checkInterval;
    private int updateInterval = ForceGL20Config.CONFIG.instance().updateInterval;

    private boolean showOpenGLConfig = true; // Track which config is currently shown

    public ForceGLOptionsScreen(Screen parent, Text title) {
        super(title);
        this.parent = parent;
    }

    @Override
    protected void init() {
        this.clearChildren();

        int topLeftY = 30;
        int buttonWidth = 150;
        int buttonHeight = 20;
        int spacing = 25;

        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("OpenGL Config"),
                button -> {
                    showOpenGLConfig = true;
                    this.init();
                }
        ).dimensions(10, topLeftY, buttonWidth, buttonHeight).build());

        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("ARS Config"),
                button -> {
                    showOpenGLConfig = false;
                    this.init(); // Reinitialize to refresh the buttons
                }
        ).dimensions(20 + buttonWidth, topLeftY, buttonWidth, buttonHeight).build());

        int centerX = this.width / 2 - 100;
        int centerY = this.height / 2 - 60;

        if (showOpenGLConfig) {
            // OpenGL Config buttons
            this.addDrawableChild(ButtonWidget.builder(
                    Text.literal("Enable GL Mod: " + (modEnabled ? "ON" : "OFF")),
                    button -> {
                        modEnabled = !modEnabled;
                        button.setMessage(Text.literal("Enable GL Mod: " + (modEnabled ? "ON" : "OFF")));
                    }
            ).dimensions(centerX, centerY, 200, buttonHeight).build());

            this.addDrawableChild(new SliderWidget(centerX, centerY + spacing, 200, buttonHeight,
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
                    Text.literal("Iris IF Override: " + (irisIFOverride ? "ON" : "OFF")),
                    button -> {
                        irisIFOverride = !irisIFOverride;
                        button.setMessage(Text.literal("Iris IF Override: " + (irisIFOverride ? "ON" : "OFF")));
                    }
            ).dimensions(centerX, centerY + 2 * spacing, 200, buttonHeight).build());
        } else {
            this.addDrawableChild(new SliderWidget(centerX, centerY, 200, buttonHeight,
                    Text.literal("Min FPS Threshold: " + minFpsThreshold), (minFpsThreshold - 20) / 100.0) {
                @Override
                protected void updateMessage() {
                    this.setMessage(Text.literal("Min FPS Threshold: " + minFpsThreshold));
                }

                @Override
                protected void applyValue() {
                    minFpsThreshold = 20 + (int) Math.round(this.value * 100 / 5) * 5;
                }
            });

            this.addDrawableChild(new SliderWidget(centerX, centerY + spacing, 200, buttonHeight,
                    Text.literal("Max FPS Threshold: " + maxFpsThreshold), (maxFpsThreshold - 30) / 330.0) {
                @Override
                protected void updateMessage() {
                    this.setMessage(Text.literal("Max FPS Threshold: " + maxFpsThreshold));
                }

                @Override
                protected void applyValue() {
                    maxFpsThreshold = 30 + (int) Math.round(this.value * 330 / 5) * 5;
                }
            });

            this.addDrawableChild(new SliderWidget(centerX, centerY + 2 * spacing, 200, buttonHeight,
                    Text.literal("Min Render Distance: " + minRenderDistance), (minRenderDistance - 2) / 18.0) {
                @Override
                protected void updateMessage() {
                    this.setMessage(Text.literal("Min Render Distance: " + minRenderDistance));
                }

                @Override
                protected void applyValue() {
                    minRenderDistance = 2 + (int) Math.round(this.value * 18);
                }
            });

            this.addDrawableChild(new SliderWidget(centerX, centerY + 3 * spacing, 200, buttonHeight,
                    Text.literal("Max Render Distance: " + maxRenderDistance), (maxRenderDistance - 4) / 28.0) {
                @Override
                protected void updateMessage() {
                    this.setMessage(Text.literal("Max Render Distance: " + maxRenderDistance));
                }

                @Override
                protected void applyValue() {
                    maxRenderDistance = 4 + (int) Math.round(this.value * 28);
                }
            });

            this.addDrawableChild(new SliderWidget(centerX, centerY + 4 * spacing, 200, buttonHeight,
                    Text.literal("Default Render Distance: " + defaultRenderDistance), (defaultRenderDistance - 4) / 28.0) {
                @Override
                protected void updateMessage() {
                    this.setMessage(Text.literal("Default Render Distance: " + defaultRenderDistance));
                }

                @Override
                protected void applyValue() {
                    defaultRenderDistance = 4 + (int) Math.round(this.value * 28);
                }
            });

            this.addDrawableChild(new SliderWidget(centerX, centerY + 5 * spacing, 200, buttonHeight,
                    Text.literal("Check Interval: " + checkInterval + " ms"), (checkInterval - 500) / 1500.0) {
                @Override
                protected void updateMessage() {
                    this.setMessage(Text.literal("Check Interval: " + checkInterval + " ms"));
                }

                @Override
                protected void applyValue() {
                    checkInterval = 500 + (int) Math.round(this.value * 1500 / 500) * 500;
                }
            });

            this.addDrawableChild(new SliderWidget(centerX, centerY + 6 * spacing, 200, buttonHeight,
                    Text.literal("Update Interval: " + updateInterval + " ms"), (updateInterval - 1000) / 29000.0) {
                @Override
                protected void updateMessage() {
                    this.setMessage(Text.literal("Update Interval: " + updateInterval + " ms"));
                }

                @Override
                protected void applyValue() {
                    updateInterval = 1000 + (int) Math.round(this.value * 29000 / 1000) * 1000;
                }
            });
        }

        // Common buttons (Save and Back)
        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("Save"),
                button -> {
                    ForceGL20Config.CONFIG.instance().contextVersionMajor = openGLVersion;
                    ForceGL20Config.CONFIG.instance().modEnabled = modEnabled;
                    ForceGL20Config.CONFIG.instance().irisIFOverride = irisIFOverride;
                    ForceGL20Config.CONFIG.instance().adaptiveRenderScalingEnabled = ARScalingEnabled;
                    ForceGL20Config.CONFIG.instance().minFpsThreshold = minFpsThreshold;
                    ForceGL20Config.CONFIG.instance().maxFpsThreshold = maxFpsThreshold;
                    ForceGL20Config.CONFIG.instance().minRenderDistance = minRenderDistance;
                    ForceGL20Config.CONFIG.instance().maxRenderDistance = maxRenderDistance;
                    ForceGL20Config.CONFIG.instance().defaultRenderDistance = defaultRenderDistance;
                    ForceGL20Config.CONFIG.instance().checkInterval = checkInterval;
                    ForceGL20Config.CONFIG.instance().updateInterval = updateInterval;
                    ForceGL20Config.CONFIG.save();
                    DynamicConfigUpdates.applyDynamicChanges();
                    if (this.client != null) this.client.setScreen(parent);
                }
        ).dimensions(this.width - 325, this.height - 40, buttonWidth, buttonHeight).build());

        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("Back"),
                button -> {
                    if (this.client != null) this.client.setScreen(parent);
                }
        ).dimensions(this.width - 160, this.height - 40, buttonWidth, buttonHeight).build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 15, 0xFFFFFF);

        if (showOpenGLConfig) {
            String note = "Note: Changing the GL version will require a restart. (Mod default = 2)";
            context.drawCenteredTextWithShadow(
                this.textRenderer,
                Text.literal(note),
                this.width / 2,
                this.height / 2 + 20,
                0xAAAAAA
            );

            String compatibilityNote = "For best compatibility use 3 or 4 if your graphics card supports it.";
            context.drawCenteredTextWithShadow(
                this.textRenderer,
                Text.literal(compatibilityNote),
                this.width / 2,
                this.height / 2 + 35,
                0xAAAAAA
            );
        }
    }
}
