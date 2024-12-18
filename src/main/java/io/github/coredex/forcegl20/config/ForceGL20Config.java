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
    public boolean modEnabled = true; // New field to toggle the mod

    @SerialEntry
    public boolean irisIFOverride = false; // Default is false


    public static Screen configScreen(Screen parent) {
        return YetAnotherConfigLib.create(CONFIG, ((defaults, config, builder) -> builder
                .title(Text.translatable("ForceGL Config"))
                .category(ConfigCategory.createBuilder()
                        .name(Text.translatable("ForceGL Config"))
                        .option(Option.<Boolean>createBuilder()
                                .name(Text.translatable("Enable Mod"))
                                .description(OptionDescription.of(Text.translatable("Toggle ForceGL mod on or off")))
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
                        .build()
                ))).generateScreen(parent);
    }
}
