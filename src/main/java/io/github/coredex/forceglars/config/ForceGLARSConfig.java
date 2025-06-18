package io.github.coredex.forceglars.config;

import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.OptionGroup;
import dev.isxander.yacl3.api.YetAnotherConfigLib;
import dev.isxander.yacl3.api.controller.BooleanControllerBuilder;
import dev.isxander.yacl3.api.controller.IntegerSliderControllerBuilder;
import dev.isxander.yacl3.api.controller.EnumControllerBuilder;
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import dev.isxander.yacl3.platform.YACLPlatform;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import io.github.coredex.forceglars.hud.InteractiveHUD;

public class ForceGLARSConfig {
    public static final ConfigClassHandler<ForceGLARSConfig> CONFIG = ConfigClassHandler.createBuilder(ForceGLARSConfig.class)
            .serializer(config -> GsonConfigSerializerBuilder.create(config)
                    .setPath(YACLPlatform.getConfigDir().resolve("forcegl20.json"))
                    .build())
            .build();

    @SerialEntry
    public int contextVersionMajor = 2;

    @SerialEntry
    public int contextVersionMinor = 0;

    @SerialEntry
    public boolean modEnabled = true;

    @SerialEntry
    public boolean irisIFOverride = false;
    
    @SerialEntry
    public boolean forceCompatibilityMode = false;
    
    @SerialEntry
    public boolean disableVBO = false;

    @SerialEntry
    public boolean adaptiveRenderScalingEnabled = false;

    @SerialEntry
    public int minFpsThreshold = 30;

    @SerialEntry
    public int maxFpsThreshold = 60;

    @SerialEntry
    public int minRenderDistance = 6;

    @SerialEntry
    public int maxRenderDistance = 12;

    @SerialEntry
    public int defaultRenderDistance = 8;

    @SerialEntry
    public int checkInterval = 500;

    @SerialEntry
    public int updateInterval = 10000;

    @SerialEntry
    public boolean renderCompatibilityEnabled = false;

    @SerialEntry
    public boolean sodiumCompatibilityMode = false;

    @SerialEntry
    public boolean sodiumDisableVertexArrayObjects = true;

    @SerialEntry
    public boolean sodiumDisableVertexBufferObjects = false;

    @SerialEntry
    public boolean sodiumDisableInstancedRendering = true;

    @SerialEntry
    public boolean sodiumForceFixedFunction = false;

    @SerialEntry
    public boolean sodiumDisableGeometryShaders = true;

    @SerialEntry
    public boolean sodiumDisableComputeShaders = true;

    @SerialEntry
    public boolean sodiumUseLegacyChunkRenderer = true;

    // HUD Configuration
    @SerialEntry
    public boolean hudEnabled = false;

    @SerialEntry
    public InteractiveHUD.HudPosition hudPosition = InteractiveHUD.HudPosition.TOP_LEFT;

    @SerialEntry
    public int hudOffsetX = 10;

    @SerialEntry
    public int hudOffsetY = 10;

    @SerialEntry
    public int hudTransparency = 200;

    @SerialEntry
    public boolean hudShowBackground = true;

    @SerialEntry
    public boolean hudShowDetailedInfo = false;

    public static Screen configScreen(Screen parent) {
        return YetAnotherConfigLib.create(CONFIG, ((defaults, config, builder) -> builder
                .title(Text.translatable("ForceGL Config"))
                .category(ConfigCategory.createBuilder()
                        .name(Text.translatable("ForceGL Config"))
                        .group(OptionGroup.createBuilder()
                            .name(Text.translatable("OpenGL Settings"))
                            .option(Option.<Boolean>createBuilder()
                                    .name(Text.translatable("Enable GL Mod"))
                                    .description(OptionDescription.of(Text.translatable("Toggle OpenGL patching on or off")))
                                    .binding(defaults.modEnabled, () -> config.modEnabled, newVal -> config.modEnabled = newVal)
                                    .controller(BooleanControllerBuilder::create)
                                    .build())
                            .option(Option.<Boolean>createBuilder()
                                    .name(Text.translatable("Force Compatibility Mode"))
                                    .description(OptionDescription.of(Text.translatable("Forces OpenGL 2.0 and maximum compatibility settings. Recommended for older GPUs.")))
                                    .binding(defaults.forceCompatibilityMode, () -> config.forceCompatibilityMode, newVal -> config.forceCompatibilityMode = newVal)
                                    .controller(BooleanControllerBuilder::create)
                                    .build())
                            .option(Option.<Integer>createBuilder()
                                    .name(Text.translatable("OpenGL version"))
                                    .description(OptionDescription.of(Text.translatable("Switch the OpenGL version according to your needs. Default = 2")))
                                    .binding(defaults.contextVersionMajor, () -> config.contextVersionMajor, newVal -> config.contextVersionMajor = newVal)
                                    .controller(opt -> IntegerSliderControllerBuilder.create(opt)
                                            .range(1, 4) // OpenGL major version range
                                            .step(1))
                                    .build())
                        .option(Option.<Integer>createBuilder()
                                .name(Text.translatable("version Minor"))
                                .description(OptionDescription.of(Text.translatable("GLSL Minor")))
                                .binding(defaults.contextVersionMinor, () -> config.contextVersionMinor, newVal -> config.contextVersionMinor = newVal)
                                .controller(opt -> IntegerSliderControllerBuilder.create(opt)
                                        .range(0, 9)
                                        .step(1))
                                .build())
                            .option(Option.<Boolean>createBuilder()
                                    .name(Text.translatable("Disable VBO"))
                                    .description(OptionDescription.of(Text.translatable("Disables Vertex Buffer Objects for maximum compatibility with legacy GPUs. Only works in compatibility mode.")))
                                    .binding(defaults.disableVBO, () -> config.disableVBO, newVal -> config.disableVBO = newVal)
                                    .controller(BooleanControllerBuilder::create)
                                    .build())
                            .option(Option.<Boolean>createBuilder()
                                    .name(Text.translatable("Enable Iris-ImmediatelyFast Override"))
                                    .description(OptionDescription.of(Text.translatable("Allows ForceGL to work even if Iris and ImmediatelyFast are detected together. Default = false.")))
                                    .binding(defaults.irisIFOverride, () -> config.irisIFOverride, newVal -> config.irisIFOverride = newVal)
                                    .controller(BooleanControllerBuilder::create)
                                    .build())
                            .build())
                        .group(OptionGroup.createBuilder()
                            .name(Text.translatable("Adaptive Render Scaling"))
                            .option(Option.<Boolean>createBuilder()
                                    .name(Text.translatable("Enable Adaptive Render Scaling(ARS)"))
                                    .description(OptionDescription.of(Text.translatable("Dynamically adjust render distance based on FPS.")))
                                    .binding(defaults.adaptiveRenderScalingEnabled, () -> config.adaptiveRenderScalingEnabled, newVal -> config.adaptiveRenderScalingEnabled = newVal)
                                    .controller(BooleanControllerBuilder::create)
                                    .build())
                            .option(Option.<Integer>createBuilder()
                                    .name(Text.translatable("Min FPS Threshold"))
                                    .description(OptionDescription.of(Text.translatable("FPS below this value decreases render distance.")))
                                    .binding(defaults.minFpsThreshold, () -> config.minFpsThreshold, newVal -> config.minFpsThreshold = newVal)
                                    .controller(opt -> IntegerSliderControllerBuilder.create(opt).range(20, 120).step(5))
                                    .build())
                            .option(Option.<Integer>createBuilder()
                                    .name(Text.translatable("Max FPS Threshold"))
                                    .description(OptionDescription.of(Text.translatable("FPS above this value increases render distance.")))
                                    .binding(defaults.maxFpsThreshold, () -> config.maxFpsThreshold, newVal -> config.maxFpsThreshold = newVal)
                                    .controller(opt -> IntegerSliderControllerBuilder.create(opt).range(30, 360).step(5))
                                    .build())
                            .option(Option.<Integer>createBuilder()
                                    .name(Text.translatable("Min Render Distance"))
                                    .description(OptionDescription.of(Text.translatable("Set the minimum render distance.")))
                                    .binding(defaults.minRenderDistance, () -> config.minRenderDistance, newVal -> config.minRenderDistance = newVal)
                                    .controller(opt -> IntegerSliderControllerBuilder.create(opt).range(2, 20).step(1))
                                    .build())
                            .option(Option.<Integer>createBuilder()
                                    .name(Text.translatable("Max Render Distance"))
                                    .description(OptionDescription.of(Text.translatable("Set the maximum render distance.")))
                                    .binding(defaults.maxRenderDistance, () -> config.maxRenderDistance, newVal -> config.maxRenderDistance = newVal)
                                    .controller(opt -> IntegerSliderControllerBuilder.create(opt).range(4, 32).step(1))
                                    .build())
                            .option(Option.<Integer>createBuilder()
                                    .name(Text.translatable("Default Render Distance"))
                                    .description(OptionDescription.of(Text.translatable("Set the starting point render distance for ARS.")))
                                    .binding(defaults.defaultRenderDistance, () -> config.defaultRenderDistance, newVal -> config.defaultRenderDistance = newVal)
                                    .controller(opt -> IntegerSliderControllerBuilder.create(opt).range(4, 32).step(1))
                                    .build())
                            .option(Option.<Integer>createBuilder()
                                    .name(Text.translatable("Default Check Interval (ms)"))
                                    .description(OptionDescription.of(Text.translatable("Set the performance check interval for ARS.")))
                                    .binding(defaults.checkInterval, () -> config.checkInterval, newVal -> config.checkInterval = newVal)
                                    .controller(opt -> IntegerSliderControllerBuilder.create(opt).range(500, 2000).step(500))
                                    .build())
                            .option(Option.<Integer>createBuilder()
                                    .name(Text.translatable("Default Update Interval (ms)"))
                                    .description(OptionDescription.of(Text.translatable("Set the performance update interval for ARS.")))
                                    .binding(defaults.updateInterval, () -> config.updateInterval, newVal -> config.updateInterval = newVal)
                                    .controller(opt -> IntegerSliderControllerBuilder.create(opt).range(1000, 30000).step(1000))
                                    .build())
                            .build())
                        .group(OptionGroup.createBuilder()
                            .name(Text.translatable("Render Compatibility"))
                            .option(Option.<Boolean>createBuilder()
                                    .name(Text.translatable("Enable Render Compatibility"))
                                    .description(OptionDescription.of(Text.translatable("Enable compatibility features for other rendering mods.")))
                                    .binding(defaults.renderCompatibilityEnabled, () -> config.renderCompatibilityEnabled, newVal -> config.renderCompatibilityEnabled = newVal)
                                    .controller(BooleanControllerBuilder::create)
                                    .build())
                            .option(Option.<Boolean>createBuilder()
                                    .name(Text.translatable("Sodium Compatibility Mode"))
                                    .description(OptionDescription.of(Text.translatable("Enable specific compatibility features for Sodium mod.")))
                                    .binding(defaults.sodiumCompatibilityMode, () -> config.sodiumCompatibilityMode, newVal -> config.sodiumCompatibilityMode = newVal)
                                    .controller(BooleanControllerBuilder::create)
                                    .build())
                            .option(Option.<Boolean>createBuilder()
                                    .name(Text.translatable("Disable Vertex Array Objects"))
                                    .description(OptionDescription.of(Text.translatable("Disables VAOs in Sodium for OpenGL 2.x compatibility.")))
                                    .binding(defaults.sodiumDisableVertexArrayObjects, () -> config.sodiumDisableVertexArrayObjects, newVal -> config.sodiumDisableVertexArrayObjects = newVal)
                                    .controller(BooleanControllerBuilder::create)
                                    .build())
                            .option(Option.<Boolean>createBuilder()
                                    .name(Text.translatable("Disable Vertex Buffer Objects"))
                                    .description(OptionDescription.of(Text.translatable("Disables VBOs in Sodium for maximum compatibility.")))
                                    .binding(defaults.sodiumDisableVertexBufferObjects, () -> config.sodiumDisableVertexBufferObjects, newVal -> config.sodiumDisableVertexBufferObjects = newVal)
                                    .controller(BooleanControllerBuilder::create)
                                    .build())
                            .option(Option.<Boolean>createBuilder()
                                    .name(Text.translatable("Disable Instanced Rendering"))
                                    .description(OptionDescription.of(Text.translatable("Disables instanced rendering for older GPUs.")))
                                    .binding(defaults.sodiumDisableInstancedRendering, () -> config.sodiumDisableInstancedRendering, newVal -> config.sodiumDisableInstancedRendering = newVal)
                                    .controller(BooleanControllerBuilder::create)
                                    .build())
                            .option(Option.<Boolean>createBuilder()
                                    .name(Text.translatable("Force Fixed Function Pipeline"))
                                    .description(OptionDescription.of(Text.translatable("Forces Sodium to use fixed function rendering (OpenGL 1.x/2.x).")))
                                    .binding(defaults.sodiumForceFixedFunction, () -> config.sodiumForceFixedFunction, newVal -> config.sodiumForceFixedFunction = newVal)
                                    .controller(BooleanControllerBuilder::create)
                                    .build())
                            .option(Option.<Boolean>createBuilder()
                                    .name(Text.translatable("Disable Geometry Shaders"))
                                    .description(OptionDescription.of(Text.translatable("Disables geometry shaders (requires OpenGL 3.2+).")))
                                    .binding(defaults.sodiumDisableGeometryShaders, () -> config.sodiumDisableGeometryShaders, newVal -> config.sodiumDisableGeometryShaders = newVal)
                                    .controller(BooleanControllerBuilder::create)
                                    .build())
                            .option(Option.<Boolean>createBuilder()
                                    .name(Text.translatable("Disable Compute Shaders"))
                                    .description(OptionDescription.of(Text.translatable("Disables compute shaders (requires OpenGL 4.3+).")))
                                    .binding(defaults.sodiumDisableComputeShaders, () -> config.sodiumDisableComputeShaders, newVal -> config.sodiumDisableComputeShaders = newVal)
                                    .controller(BooleanControllerBuilder::create)
                                    .build())
                            .option(Option.<Boolean>createBuilder()
                                    .name(Text.translatable("Use Legacy Chunk Renderer"))
                                    .description(OptionDescription.of(Text.translatable("Forces Sodium to use legacy chunk rendering for compatibility.")))
                                    .binding(defaults.sodiumUseLegacyChunkRenderer, () -> config.sodiumUseLegacyChunkRenderer, newVal -> config.sodiumUseLegacyChunkRenderer = newVal)
                                    .controller(BooleanControllerBuilder::create)
                                    .build())
                            .build())
                        .group(OptionGroup.createBuilder()
                            .name(Text.translatable("Interactive HUD"))
                            .option(Option.<Boolean>createBuilder()
                                    .name(Text.translatable("Enable HUD"))
                                    .description(OptionDescription.of(Text.translatable("Show real-time performance metrics overlay.")))
                                    .binding(defaults.hudEnabled, () -> config.hudEnabled, newVal -> config.hudEnabled = newVal)
                                    .controller(BooleanControllerBuilder::create)
                                    .build())
                            .option(Option.<InteractiveHUD.HudPosition>createBuilder()
                                    .name(Text.translatable("HUD Position"))
                                    .description(OptionDescription.of(Text.translatable("Choose where to display the HUD on screen.")))
                                    .binding(defaults.hudPosition, () -> config.hudPosition, newVal -> config.hudPosition = newVal)
                                    .controller(opt -> EnumControllerBuilder.create(opt).enumClass(InteractiveHUD.HudPosition.class))
                                    .build())
                            .option(Option.<Integer>createBuilder()
                                    .name(Text.translatable("HUD X Offset"))
                                    .description(OptionDescription.of(Text.translatable("Horizontal offset from the selected position.")))
                                    .binding(defaults.hudOffsetX, () -> config.hudOffsetX, newVal -> config.hudOffsetX = newVal)
                                    .controller(opt -> IntegerSliderControllerBuilder.create(opt).range(0, 100).step(1))
                                    .build())
                            .option(Option.<Integer>createBuilder()
                                    .name(Text.translatable("HUD Y Offset"))
                                    .description(OptionDescription.of(Text.translatable("Vertical offset from the selected position.")))
                                    .binding(defaults.hudOffsetY, () -> config.hudOffsetY, newVal -> config.hudOffsetY = newVal)
                                    .controller(opt -> IntegerSliderControllerBuilder.create(opt).range(0, 100).step(1))
                                    .build())
                            .option(Option.<Integer>createBuilder()
                                    .name(Text.translatable("HUD Transparency"))
                                    .description(OptionDescription.of(Text.translatable("Transparency level of the HUD (0=invisible, 255=opaque).")))
                                    .binding(defaults.hudTransparency, () -> config.hudTransparency, newVal -> config.hudTransparency = newVal)
                                    .controller(opt -> IntegerSliderControllerBuilder.create(opt).range(50, 255).step(5))
                                    .build())
                            .option(Option.<Boolean>createBuilder()
                                    .name(Text.translatable("Show Background"))
                                    .description(OptionDescription.of(Text.translatable("Display a background behind the HUD text.")))
                                    .binding(defaults.hudShowBackground, () -> config.hudShowBackground, newVal -> config.hudShowBackground = newVal)
                                    .controller(BooleanControllerBuilder::create)
                                    .build())
                            .option(Option.<Boolean>createBuilder()
                                    .name(Text.translatable("Show Detailed Info"))
                                    .description(OptionDescription.of(Text.translatable("Display additional technical information.")))
                                    .binding(defaults.hudShowDetailedInfo, () -> config.hudShowDetailedInfo, newVal -> config.hudShowDetailedInfo = newVal)
                                    .controller(BooleanControllerBuilder::create)
                                    .build())
                            .build())
                        .build()
                ))).generateScreen(parent);
    }
}
