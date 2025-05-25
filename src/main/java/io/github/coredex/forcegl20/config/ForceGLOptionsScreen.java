package io.github.coredex.forcegl20.config;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;

public class ForceGLOptionsScreen extends Screen {
    private final Screen parent;
    private int openGLVersion = ForceGL20Config.CONFIG.instance().contextVersionMajor;
    private int openGLVersionMinor = ForceGL20Config.CONFIG.instance().contextVersionMinor;
    private boolean modEnabled = ForceGL20Config.CONFIG.instance().modEnabled;
    private boolean irisIFOverride = ForceGL20Config.CONFIG.instance().irisIFOverride;
    private boolean forceCompatibilityMode = ForceGL20Config.CONFIG.instance().forceCompatibilityMode;
    private boolean disableVBO = ForceGL20Config.CONFIG.instance().disableVBO;

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
        int spacing = 24; // Slightly reduce spacing to prevent overlap
        
        // Screen navigation buttons at the top
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

        // Calculate more sensible layout values
        int centerX = this.width / 2 - 100;
        int centerY = this.height / 3 - 20; // Start higher up to leave room for info text
        
        // Common buttons (Save and Back) at the bottom
        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("Save"),
                button -> {
                    ForceGL20Config.CONFIG.instance().contextVersionMajor = forceCompatibilityMode ? 2 : openGLVersion;
                    ForceGL20Config.CONFIG.instance().contextVersionMinor = openGLVersionMinor;
                    ForceGL20Config.CONFIG.instance().modEnabled = modEnabled;
                    ForceGL20Config.CONFIG.instance().irisIFOverride = irisIFOverride;
                    ForceGL20Config.CONFIG.instance().forceCompatibilityMode = forceCompatibilityMode;
                    ForceGL20Config.CONFIG.instance().disableVBO = disableVBO;
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
        ).dimensions(this.width / 2 - 105, this.height - 40, 100, buttonHeight).build());

        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("Back"),
                button -> {
                    if (this.client != null) this.client.setScreen(parent);
                }
        ).dimensions(this.width / 2 + 5, this.height - 40, 100, buttonHeight).build());

        if (showOpenGLConfig) {
            // OpenGL Config buttons
            this.addDrawableChild(ButtonWidget.builder(
                    Text.literal("Enable GL Mod: " + (modEnabled ? "ON" : "OFF")),
                    button -> {
                        modEnabled = !modEnabled;
                        button.setMessage(Text.literal("Enable GL Mod: " + (modEnabled ? "ON" : "OFF")));
                    }
            ).dimensions(centerX, centerY, 200, buttonHeight).build());
            
            this.addDrawableChild(ButtonWidget.builder(
                    Text.literal("Force Compatibility Mode: " + (forceCompatibilityMode ? "ON" : "OFF")),
                    button -> {
                        forceCompatibilityMode = !forceCompatibilityMode;
                        button.setMessage(Text.literal("Force Compatibility Mode: " + (forceCompatibilityMode ? "ON" : "OFF")));
                        if (forceCompatibilityMode) {
                            openGLVersion = 2; // Force OpenGL 2.0 if compatibility mode is on
                        }
                    }
            ).dimensions(centerX, centerY + spacing, 200, buttonHeight).build());

            this.addDrawableChild(new SliderWidget(centerX, centerY + 2 * spacing, 200, buttonHeight,
                    Text.literal("OpenGL Version: " + openGLVersion), openGLVersion / 4.0) {
                @Override
                protected void updateMessage() {
                    this.setMessage(Text.literal("OpenGL Version: " + (forceCompatibilityMode ? "2 (forced)" : openGLVersion)));
                    this.active = !forceCompatibilityMode;
                }

                @Override
                protected void applyValue() {
                    if (!forceCompatibilityMode) {
                        openGLVersion = (int) Math.round(this.value * 4);
                    }
                }
            });

            this.addDrawableChild(ButtonWidget.builder(
                    Text.literal("Disable VBO: " + (disableVBO ? "ON" : "OFF")),
                    button -> {
                        disableVBO = !disableVBO;
                        button.setMessage(Text.literal("Disable VBO: " + (disableVBO ? "ON" : "OFF")));
                    }
            ).dimensions(centerX, centerY + 3 * spacing, 200, buttonHeight).build());

            this.addDrawableChild(ButtonWidget.builder(
                    Text.literal("Iris IF Override: " + (irisIFOverride ? "ON" : "OFF")),
                    button -> {
                        irisIFOverride = !irisIFOverride;
                        button.setMessage(Text.literal("Iris IF Override: " + (irisIFOverride ? "ON" : "OFF")));
                    }
            ).dimensions(centerX, centerY + 4 * spacing, 200, buttonHeight).build());
        } else {
            // Add ARS toggle button at the top of ARS settings
            this.addDrawableChild(ButtonWidget.builder(
                    Text.literal("Enable Adaptive Render Scaling: " + (ARScalingEnabled ? "ON" : "OFF")),
                    button -> {
                        ARScalingEnabled = !ARScalingEnabled;
                        button.setMessage(Text.literal("Enable Adaptive Render Scaling: " + (ARScalingEnabled ? "ON" : "OFF")));
                        
                        // Update the enabled status of other ARS controls based on the toggle state
                        for (var element : this.children()) {
                            if (element != button && element instanceof ClickableWidget widget) {
                                if (widget instanceof SliderWidget) {
                                    widget.active = ARScalingEnabled;
                                }
                            }
                        }
                    }
            ).dimensions(centerX, centerY, 200, buttonHeight).build());

            // Create and add Min FPS Threshold slider
            SliderWidget minFpsSlider = new SliderWidget(centerX, centerY + spacing, 200, buttonHeight,
                    Text.literal("Min FPS Threshold: " + minFpsThreshold), (minFpsThreshold - 20) / 100.0) {
                @Override
                protected void updateMessage() {
                    this.setMessage(Text.literal("Min FPS Threshold: " + minFpsThreshold));
                }

                @Override
                protected void applyValue() {
                    minFpsThreshold = 20 + (int) Math.round(this.value * 100 / 5) * 5;
                }
            };
            minFpsSlider.active = ARScalingEnabled;
            this.addDrawableChild(minFpsSlider);

            // Create and add Max FPS Threshold slider
            SliderWidget maxFpsSlider = new SliderWidget(centerX, centerY + 2 * spacing, 200, buttonHeight,
                    Text.literal("Max FPS Threshold: " + maxFpsThreshold), (maxFpsThreshold - 30) / 330.0) {
                @Override
                protected void updateMessage() {
                    this.setMessage(Text.literal("Max FPS Threshold: " + maxFpsThreshold));
                }

                @Override
                protected void applyValue() {
                    maxFpsThreshold = 30 + (int) Math.round(this.value * 330 / 5) * 5;
                }
            };
            maxFpsSlider.active = ARScalingEnabled;
            this.addDrawableChild(maxFpsSlider);

            // Create and add Min Render Distance slider
            SliderWidget minRenderDistanceSlider = new SliderWidget(centerX, centerY + 3 * spacing, 200, buttonHeight,
                    Text.literal("Min Render Distance: " + minRenderDistance), (minRenderDistance - 2) / 18.0) {
                @Override
                protected void updateMessage() {
                    this.setMessage(Text.literal("Min Render Distance: " + minRenderDistance));
                }

                @Override
                protected void applyValue() {
                    minRenderDistance = 2 + (int) Math.round(this.value * 18);
                }
            };
            minRenderDistanceSlider.active = ARScalingEnabled;
            this.addDrawableChild(minRenderDistanceSlider);

            // Create and add Max Render Distance slider
            SliderWidget maxRenderDistanceSlider = new SliderWidget(centerX, centerY + 4 * spacing, 200, buttonHeight,
                    Text.literal("Max Render Distance: " + maxRenderDistance), (maxRenderDistance - 4) / 28.0) {
                @Override
                protected void updateMessage() {
                    this.setMessage(Text.literal("Max Render Distance: " + maxRenderDistance));
                }

                @Override
                protected void applyValue() {
                    maxRenderDistance = 4 + (int) Math.round(this.value * 28);
                }
            };
            maxRenderDistanceSlider.active = ARScalingEnabled;
            this.addDrawableChild(maxRenderDistanceSlider);

            // Create and add Default Render Distance slider
            SliderWidget defaultRenderDistanceSlider = new SliderWidget(centerX, centerY + 5 * spacing, 200, buttonHeight,
                    Text.literal("Default Render Distance: " + defaultRenderDistance), (defaultRenderDistance - 4) / 28.0) {
                @Override
                protected void updateMessage() {
                    this.setMessage(Text.literal("Default Render Distance: " + defaultRenderDistance));
                }

                @Override
                protected void applyValue() {
                    defaultRenderDistance = 4 + (int) Math.round(this.value * 28);
                }
            };
            defaultRenderDistanceSlider.active = ARScalingEnabled;
            this.addDrawableChild(defaultRenderDistanceSlider);

            // Create and add Check Interval slider
            SliderWidget checkIntervalSlider = new SliderWidget(centerX, centerY + 6 * spacing, 200, buttonHeight,
                    Text.literal("Check Interval: " + checkInterval + " ms"), (checkInterval - 500) / 1500.0) {
                @Override
                protected void updateMessage() {
                    this.setMessage(Text.literal("Check Interval: " + checkInterval + " ms"));
                }

                @Override
                protected void applyValue() {
                    checkInterval = 500 + (int) Math.round(this.value * 1500 / 500) * 500;
                }
            };
            checkIntervalSlider.active = ARScalingEnabled;
            this.addDrawableChild(checkIntervalSlider);

            // Create and add Update Interval slider
            SliderWidget updateIntervalSlider = new SliderWidget(centerX, centerY + 7 * spacing, 200, buttonHeight,
                    Text.literal("Update Interval: " + updateInterval + " ms"), (updateInterval - 1000) / 29000.0) {
                @Override
                protected void updateMessage() {
                    this.setMessage(Text.literal("Update Interval: " + updateInterval + " ms"));
                }

                @Override
                protected void applyValue() {
                    updateInterval = 1000 + (int) Math.round(this.value * 29000 / 1000) * 1000;
                }
            };
            updateIntervalSlider.active = ARScalingEnabled;
            this.addDrawableChild(updateIntervalSlider);
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // Add title text at the top
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 15, 0xFFFFFF);

        // Calculate where the informational text should be placed
        // Place below all controls but above the Save/Back buttons
        int infoY = this.height - 80;

        if (showOpenGLConfig) {
            // Draw informational text for the OpenGL settings
            String note = forceCompatibilityMode ? 
                "Compatibility Mode: Forces OpenGL 2.0 for maximum compatibility" : 
                "Note: Changing the GL version will require a restart. (Mod default = 2)";
                
            context.drawCenteredTextWithShadow(
                this.textRenderer,
                Text.literal(note),
                this.width / 2,
                infoY,
                0xAAAAAA
            );

            String compatibilityNote = forceCompatibilityMode ?
                "For legacy GPUs (Intel GMA, etc), try enabling \"Disable VBO\"." : 
                "For best compatibility use 3 or 4 if your graphics card supports it.";
            
            context.drawCenteredTextWithShadow(
                this.textRenderer,
                Text.literal(compatibilityNote),
                this.width / 2,
                infoY + 15,
                0xAAAAAA
            );
        } else {
            // Draw informational text for the ARS settings
            if (!ARScalingEnabled) {
                context.drawCenteredTextWithShadow(
                    this.textRenderer,
                    Text.literal("Adaptive Render Scaling is currently disabled"),
                    this.width / 2,
                    infoY,
                    0xAAAAAA
                );
                context.drawCenteredTextWithShadow(
                    this.textRenderer,
                    Text.literal("Enable it above to adjust render distance based on performance"),
                    this.width / 2,
                    infoY + 15,
                    0xAAAAAA
                );
            } else {
                context.drawCenteredTextWithShadow(
                    this.textRenderer,
                    Text.literal("ARS will automatically adjust render distance based on FPS"),
                    this.width / 2,
                    infoY,
                    0xAAAAAA
                );
                context.drawCenteredTextWithShadow(
                    this.textRenderer,
                    Text.literal("Lower FPS lowers render distance, higher FPS increases it"),
                    this.width / 2,
                    infoY + 15,
                    0xAAAAAA
                );
            }
        }
        
        super.render(context, mouseX, mouseY, delta);
    }
}
