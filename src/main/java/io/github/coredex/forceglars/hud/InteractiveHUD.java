package io.github.coredex.forceglars.hud;

import io.github.coredex.forceglars.ForceGLARS;
import io.github.coredex.forceglars.config.ForceGLARSConfig;
import io.github.coredex.forceglars.config.ForceGLOptionsScreen;
import io.github.coredex.forceglars.rendercompat.RenderCompatibilityManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

public class InteractiveHUD {
    private static final MinecraftClient client = MinecraftClient.getInstance();
    private static long lastUpdateTime = 0;
    private static final long UPDATE_INTERVAL = 500; // Update every 500ms
    
    // Performance metrics
    private static int currentFPS = 0;
    private static long memoryUsed = 0;
    private static long memoryTotal = 0;
    private static String glRenderer = "";
    private static String glVersion = "";
    private static int renderDistance = 0;
    
    // Color constants
    private static final int COLOR_GOOD = 0x00FF00;     // Green
    private static final int COLOR_WARNING = 0xFFFF00;  // Yellow
    private static final int COLOR_CRITICAL = 0xFF0000; // Red
    private static final int COLOR_NEUTRAL = 0xFFFFFF;  // White
    private static final int COLOR_INFO = 0x00FFFF;     // Cyan
    
    public static void render(DrawContext context, float delta) {
        if (!ForceGLARSConfig.CONFIG.instance().hudEnabled) {
            return;
        }
        
        updateMetrics();
        
        int x = getHudX();
        int y = getHudY();
        int transparency = getTransparency();
        
        List<HudLine> lines = buildHudLines();
        
        // Render background if enabled
        if (ForceGLARSConfig.CONFIG.instance().hudShowBackground) {
            renderBackground(context, x, y, lines.size(), transparency);
        }
        
        // Render HUD lines
        for (int i = 0; i < lines.size(); i++) {
            HudLine line = lines.get(i);
            int lineY = y + (i * 10);
            
            int color = applyTransparency(line.color, transparency);
            context.drawTextWithShadow(
                client.textRenderer,
                Text.literal(line.text),
                x,
                lineY,
                color
            );
        }
    }
    
    private static void updateMetrics() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastUpdateTime < UPDATE_INTERVAL) {
            return;
        }
        
        lastUpdateTime = currentTime;
        
        // Update FPS
        currentFPS = client.getCurrentFps();
        
        // Update memory info
        Runtime runtime = Runtime.getRuntime();
        memoryUsed = (runtime.totalMemory() - runtime.freeMemory()) / 1024 / 1024;
        memoryTotal = runtime.maxMemory() / 1024 / 1024;
        
        // Update GL info
        try {
            glRenderer = GL11.glGetString(GL11.GL_RENDERER);
            glVersion = GL11.glGetString(GL11.GL_VERSION);
        } catch (Exception e) {
            glRenderer = "Unknown";
            glVersion = "Unknown";
        }
        
        // Update render distance
        if (client.options != null) {
            renderDistance = client.options.getViewDistance().getValue();
        }
    }
    
    private static List<HudLine> buildHudLines() {
        List<HudLine> lines = new ArrayList<>();
        
        // FPS with color coding
        int fpsColor = getFPSColor(currentFPS);
        lines.add(new HudLine("FPS: " + currentFPS, fpsColor));
        
        // Memory usage with color coding
        int memoryColor = getMemoryColor(memoryUsed, memoryTotal);
        lines.add(new HudLine("Memory: " + memoryUsed + "/" + memoryTotal + " MB", memoryColor));
        
        // Render distance with ARS status
        String rdText = "Render Distance: " + renderDistance;
        if (ForceGLOptionsScreen.ARScalingEnabled) {
            rdText += " (ARS)";
        }
        lines.add(new HudLine(rdText, COLOR_INFO));
        
        // ForceGL status
        String glStatus = ForceGLARSConfig.CONFIG.instance().modEnabled ? "Active" : "Disabled";
        int glColor = ForceGLARSConfig.CONFIG.instance().modEnabled ? COLOR_GOOD : COLOR_WARNING;
        if (ForceGLARSConfig.CONFIG.instance().forceCompatibilityMode) {
            glStatus += " (Compat)";
            glColor = COLOR_WARNING;
        }
        lines.add(new HudLine("ForceGL: " + glStatus, glColor));
        
        // OpenGL version info
        if (ForceGLARSConfig.CONFIG.instance().hudShowDetailedInfo) {
            String glVersionText = "GL: " + ForceGLARSConfig.CONFIG.instance().contextVersionMajor + 
                                  "." + ForceGLARSConfig.CONFIG.instance().contextVersionMinor;
            lines.add(new HudLine(glVersionText, COLOR_NEUTRAL));
            
            // Sodium compatibility info
            if (RenderCompatibilityManager.isSodiumDetected()) {
                String sodiumStatus = RenderCompatibilityManager.isRenderCompatibilityEnabled() ? 
                    "Sodium: Compatible" : "Sodium: Detected";
                int sodiumColor = RenderCompatibilityManager.isRenderCompatibilityEnabled() ? 
                    COLOR_GOOD : COLOR_WARNING;
                lines.add(new HudLine(sodiumStatus, sodiumColor));
            }
            
            // Performance thresholds for ARS
            if (ForceGLOptionsScreen.ARScalingEnabled) {
                lines.add(new HudLine("ARS: " + ForceGLARSConfig.CONFIG.instance().minFpsThreshold + 
                         "-" + ForceGLARSConfig.CONFIG.instance().maxFpsThreshold + " FPS", COLOR_INFO));
            }
        }
        
        return lines;
    }
    
    private static int getFPSColor(int fps) {
        if (fps >= 60) return COLOR_GOOD;
        if (fps >= 30) return COLOR_WARNING;
        return COLOR_CRITICAL;
    }
    
    private static int getMemoryColor(long used, long total) {
        double usage = (double) used / total;
        if (usage < 0.7) return COLOR_GOOD;
        if (usage < 0.9) return COLOR_WARNING;
        return COLOR_CRITICAL;
    }
    
    private static int getHudX() {
        int screenWidth = client.getWindow().getScaledWidth();
        HudPosition position = ForceGLARSConfig.CONFIG.instance().hudPosition;
        
        switch (position) {
            case TOP_LEFT:
            case BOTTOM_LEFT:
                return ForceGLARSConfig.CONFIG.instance().hudOffsetX;
            case TOP_RIGHT:
            case BOTTOM_RIGHT:
                return screenWidth - 150 - ForceGLARSConfig.CONFIG.instance().hudOffsetX;
            case TOP_CENTER:
            case BOTTOM_CENTER:
                return (screenWidth / 2) - 75 + ForceGLARSConfig.CONFIG.instance().hudOffsetX;
            default:
                return 10;
        }
    }
    
    private static int getHudY() {
        int screenHeight = client.getWindow().getScaledHeight();
        HudPosition position = ForceGLARSConfig.CONFIG.instance().hudPosition;
        
        switch (position) {
            case TOP_LEFT:
            case TOP_RIGHT:
            case TOP_CENTER:
                return ForceGLARSConfig.CONFIG.instance().hudOffsetY;
            case BOTTOM_LEFT:
            case BOTTOM_RIGHT:
            case BOTTOM_CENTER:
                return screenHeight - 60 - ForceGLARSConfig.CONFIG.instance().hudOffsetY;
            default:
                return 10;
        }
    }
    
    private static int getTransparency() {
        return Math.max(0, Math.min(255, ForceGLARSConfig.CONFIG.instance().hudTransparency));
    }
    
    private static void renderBackground(DrawContext context, int x, int y, int lines, int transparency) {
        int width = 150;
        int height = lines * 10 + 4;
        int bgColor = applyTransparency(0x000000, transparency / 2); // Semi-transparent black
        
        context.fill(x - 2, y - 2, x + width, y + height, bgColor);
    }
    
    private static int applyTransparency(int color, int transparency) {
        int alpha = Math.max(0, Math.min(255, transparency));
        return (alpha << 24) | (color & 0xFFFFFF);
    }
    
    public static class HudLine {
        public final String text;
        public final int color;
        
        public HudLine(String text, int color) {
            this.text = text;
            this.color = color;
        }
    }
    
    public enum HudPosition {
        TOP_LEFT, TOP_RIGHT, TOP_CENTER,
        BOTTOM_LEFT, BOTTOM_RIGHT, BOTTOM_CENTER
    }
}
