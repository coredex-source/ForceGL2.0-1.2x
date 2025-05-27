package io.github.coredex.forceglars.AdaptiveRenderScaling;

import net.minecraft.client.MinecraftClient;

public class RenderScalingTools {
    public static void setRenderDistance(int RenderDistance){
        MinecraftClient client = MinecraftClient.getInstance();
        client.options.setServerViewDistance(RenderDistance);
        client.options.getViewDistance().setValue(RenderDistance);
    }
}
