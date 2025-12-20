package io.github.coredex.forceglars.AdaptiveRenderScaling;

import net.minecraft.client.Minecraft;

public class RenderScalingTools {
    public static void setRenderDistance(int RenderDistance){
        Minecraft client = Minecraft.getInstance();
        client.options.setServerRenderDistance(RenderDistance);
        client.options.renderDistance().set(RenderDistance);
    }
}
