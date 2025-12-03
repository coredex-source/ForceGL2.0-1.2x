package io.github.coredex.forceglars.sodium;

import io.github.coredex.forceglars.ForceGLARS;
import io.github.coredex.forceglars.config.ForceGLARSConfig;

/**
 * Patches Sodium shader source code to work with OpenGL 2.0
 * Converts integer vertex attributes to normalized floats
 */
public class SodiumShaderPatcher {
    
    private static boolean hasLogged = false;
    
    public static String patchShaderSource(String source) {
        // Only patch if OpenGL 2.0 is forced
        if (ForceGLARSConfig.CONFIG.instance().contextVersionMajor > 2) {
            return source;
        }
        
        // Only patch Sodium chunk shaders
        if (!source.contains("USE_VERTEX_COMPRESSION") || !source.contains("uvec")) {
            return source;
        }
        
        if (!hasLogged) {
            ForceGLARS.LOGGER.info("=== Patching Sodium Shaders for OpenGL 2.0 Compatibility ===");
            hasLogged = true;
        }
        
        String patched = source;
        
        // Step 1: Replace integer attribute declarations with float equivalents
        patched = patched.replace("in uvec2 a_Position;", "in vec2 a_Position;");
        patched = patched.replace("in uvec2 a_TexCoord;", "in vec2 a_TexCoord;");
        patched = patched.replace("in uvec4 a_LightAndData;", "in vec4 a_LightAndData;");
        
        // Step 2: Fix the position deinterleave function
        // Original expects uvec2, now receives vec2 normalized floats
        if (patched.contains("uvec3 _deinterleave_u20x3(uvec2 data)")) {
            patched = patched.replace(
                "uvec3 _deinterleave_u20x3(uvec2 data) {",
                "uvec3 _deinterleave_u20x3(vec2 data) {\n" +
                "    // Convert normalized float [0,1] back to full uint32 range\n" +
                "    uvec2 udata = uvec2(data * 4294967295.0);"
            );
            
            patched = patched.replace(
                "    uvec3 hi = (uvec3(data.x) >> uvec3(0u, 10u, 20u)) & 0x3FFu;",
                "    uvec3 hi = (uvec3(udata.x) >> uvec3(0u, 10u, 20u)) & 0x3FFu;"
            );
            
            patched = patched.replace(
                "    uvec3 lo = (uvec3(data.y) >> uvec3(0u, 10u, 20u)) & 0x3FFu;",
                "    uvec3 lo = (uvec3(udata.y) >> uvec3(0u, 10u, 20u)) & 0x3FFu;"
            );
        }
        
        // Step 3: Fix texture coordinate extraction
        // a_TexCoord comes as normalized float, convert to uint16 range
        if (patched.contains("vec2 _get_texcoord()")) {
            patched = patched.replace(
                "vec2 _get_texcoord() {\n    return vec2(a_TexCoord & TEXTURE_MAX_VALUE) / float(TEXTURE_MAX_COORD);",
                "vec2 _get_texcoord() {\n" +
                "    uvec2 texCoord = uvec2(a_TexCoord * 65535.0);\n" +
                "    return vec2(texCoord & TEXTURE_MAX_VALUE) / float(TEXTURE_MAX_COORD);"
            );
        }
        
        if (patched.contains("vec2 _get_texcoord_bias()")) {
            patched = patched.replace(
                "vec2 _get_texcoord_bias() {\n    return mix(vec2(-1.0), vec2(1.0), bvec2(a_TexCoord >> TEXTURE_BITS));",
                "vec2 _get_texcoord_bias() {\n" +
                "    uvec2 texCoord = uvec2(a_TexCoord * 65535.0);\n" +
                "    return mix(vec2(-1.0), vec2(1.0), bvec2(texCoord >> TEXTURE_BITS));"
            );
        }
        
        // Step 4: Fix light coordinate extraction from a_LightAndData
        // Now it's vec4 with normalized floats [0,1], convert to [0,255] range
        if (patched.contains("_vert_tex_light_coord = vec2(a_LightAndData.xy) / vec2(256.0);")) {
            patched = patched.replace(
                "_vert_tex_light_coord = vec2(a_LightAndData.xy) / vec2(256.0);",
                "// a_LightAndData.xy are normalized [0,1], scale to [0,255] then divide by 256\n" +
                "    _vert_tex_light_coord = (a_LightAndData.xy * 255.0) / 256.0;"
            );
        }
        
        // Step 5: Fix material params extraction (byte -> float conversion)
        if (patched.contains("_material_params = a_LightAndData[2];")) {
            patched = patched.replace(
                "_material_params = a_LightAndData[2];",
                "_material_params = uint(a_LightAndData[2] * 255.0);"
            );
        }
        
        // Step 6: Fix draw ID extraction (byte -> float conversion)
        if (patched.contains("_draw_id = a_LightAndData[3];")) {
            patched = patched.replace(
                "_draw_id = a_LightAndData[3];",
                "_draw_id = uint(a_LightAndData[3] * 255.0);"
            );
        }
        
        ForceGLARS.LOGGER.info("Successfully patched Sodium chunk shader for GL2.0");
        
        return patched;
    }
}
