package io.github.coredex.forcegl20.config;

import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.YetAnotherConfigLib;
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

    public static Screen configScreen(Screen parent) {
        return YetAnotherConfigLib.create(CONFIG, ((defaults, config, builder) -> builder
                .title(Text.translatable("ForceGL Config"))
                .category(ConfigCategory.createBuilder()
                        .name(Text.translatable("ForceGL Config"))
                        .option(Option.<Integer>createBuilder()
                                .name(Text.translatable("OpenGL version"))
                                .description(OptionDescription.of(Text.translatable("Switch the openGL version according to your needs. Default = 2")))
                                .binding(defaults.contextVersionMajor, () -> config.contextVersionMajor, newVal -> config.contextVersionMajor = newVal)
                                .controller(opt -> IntegerSliderControllerBuilder.create(opt)
                                        .range(1, 4) // OpenGL major version range
                                        .step(1))
                                .build())
                        .build()
                ))).generateScreen(parent);
    }
}
