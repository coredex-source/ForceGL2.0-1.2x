package io.github.coredex.forcegl20.config;

import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.YetAnotherConfigLib;
import dev.isxander.yacl3.api.controller.BooleanControllerBuilder;
import dev.isxander.yacl3.api.controller.IntegerSliderControllerBuilder;
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import dev.isxander.yacl3.platform.YACLPlatform;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class ForceGL20Config {
    public static final ConfigClassHandler<ForceGL20Config> CONFIG = ConfigClassHandler.createBuilder(ForceGL20Config.class)
            .serializer(config -> GsonConfigSerializerBuilder.create(config)
                    .setPath(YACLPlatform.getConfigDir().resolve("forcegl20.json"))
                    .build())
            .build();

    @SerialEntry
    public int contextVersionMajor = 2;

    @SerialEntry
    public boolean modEnabled = true;

    @SerialEntry
    public boolean irisIFOverride = false;

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

    public static Screen configScreen(Screen parent) {
        return YetAnotherConfigLib.create(CONFIG, ((defaults, config, builder) -> builder
                .title(Text.translatable("ForceGL Config"))
                .category(ConfigCategory.createBuilder()
                        .name(Text.translatable("ForceGL Config"))
                        .option(Option.<Boolean>createBuilder()
                                .name(Text.translatable("Enable GL Mod"))
                                .description(OptionDescription.of(Text.translatable("Toggle OpenGL patching on or off")))
                                .binding(defaults.modEnabled, () -> config.modEnabled, newVal -> config.modEnabled = newVal)
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
                        .option(Option.<Boolean>createBuilder()
                                .name(Text.translatable("Enable Iris-ImmediatelyFast Override"))
                                .description(OptionDescription.of(Text.translatable("Allows ForceGL to work even if Iris and ImmediatelyFast are detected together. Default = false.")))
                                .binding(defaults.irisIFOverride, () -> config.irisIFOverride, newVal -> config.irisIFOverride = newVal)
                                .controller(BooleanControllerBuilder::create)
                                .build())
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
                                .controller(opt -> IntegerSliderControllerBuilder.create(opt).range(10, 60).step(1))
                                .build())
                        .option(Option.<Integer>createBuilder()
                                .name(Text.translatable("Max FPS Threshold"))
                                .description(OptionDescription.of(Text.translatable("FPS above this value increases render distance.")))
                                .binding(defaults.maxFpsThreshold, () -> config.maxFpsThreshold, newVal -> config.maxFpsThreshold = newVal)
                                .controller(opt -> IntegerSliderControllerBuilder.create(opt).range(30, 120).step(1))
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
                        .build()
                ))).generateScreen(parent);
    }
}
