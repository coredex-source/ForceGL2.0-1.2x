#version 120
// OpenGL 2.0 compatible version - using GLSL 1.20

#ifndef MAX_TEXTURE_LOD_BIAS
#define MAX_TEXTURE_LOD_BIAS 4
#endif

// ===== fog.glsl =====
const int FOG_SHAPE_SPHERICAL = 0;
const int FOG_SHAPE_CYLINDRICAL = 1;

float linear_fog_value(float vertexDistance, float fogStart, float fogEnd) {
    if (vertexDistance <= fogStart) {
        return 0.0;
    } else if (vertexDistance >= fogEnd) {
        return 1.0;
    }
    return (vertexDistance - fogStart) / (fogEnd - fogStart);
}

float total_fog_value(float sphericalVertexDistance, float cylindricalVertexDistance, float environmentalStart, float environmantalEnd, float renderDistanceStart, float renderDistanceEnd) {
    return max(linear_fog_value(sphericalVertexDistance, environmentalStart, environmantalEnd), linear_fog_value(cylindricalVertexDistance, renderDistanceStart, renderDistanceEnd));
}

vec4 _linearFog(vec4 fragColor, vec2 fragDistance, vec4 fogColor, vec2 environmentFog, vec2 renderFog) {
#ifdef USE_FOG
    float fogValue = total_fog_value(fragDistance.y, fragDistance.x, environmentFog.x, environmentFog.y, renderFog.x, renderFog.y);
    return vec4(mix(fragColor.rgb, fogColor.rgb, fogValue * fogColor.a), fragColor.a);
#else
    return fragColor;
#endif
}

// ===== chunk_material.glsl =====
const float ALPHA_CUTOFF_0 = 0.0;
const float ALPHA_CUTOFF_1 = 0.1;
const float ALPHA_CUTOFF_2 = 0.1;
const float ALPHA_CUTOFF_3 = 1.0;

float _material_alpha_cutoff_float(float material) {
    float m = floor(material + 0.5);
    float index = mod(floor(m / 2.0), 4.0);
    if (index < 0.5) return ALPHA_CUTOFF_0;
    if (index < 1.5) return ALPHA_CUTOFF_1;
    if (index < 2.5) return ALPHA_CUTOFF_2;
    return ALPHA_CUTOFF_3;
}

bool _material_use_mips_float(float material) {
    float m = floor(material + 0.5);
    return mod(floor(m), 2.0) != 0.0;
}

varying vec4 v_Color; // The interpolated vertex color
varying vec2 v_TexCoord; // The interpolated block texture coordinates
varying vec2 v_FragDistance; // The fragment's distance from the camera (cylindrical and spherical)

varying float v_Material;

uniform sampler2D u_BlockTex; // The block texture

uniform vec4 u_FogColor; // The color of the shader fog
uniform vec2 u_EnvironmentFog; // The start and end position for environmental fog
uniform vec2 u_RenderFog; // The start and end position for border fog

void main() {
    // Convert material back to uint-like value
    float material = floor(v_Material + 0.5);
    
    float lodBias = _material_use_mips_float(material) ? 0.0 : float(-MAX_TEXTURE_LOD_BIAS);

    vec4 color = texture2D(u_BlockTex, v_TexCoord, lodBias);
    color *= v_Color; // Apply per-vertex color modulator

#ifdef USE_FRAGMENT_DISCARD
    if (color.a < _material_alpha_cutoff_float(material)) {
        discard;
    }
#endif

    gl_FragColor = _linearFog(color, v_FragDistance, u_FogColor, u_EnvironmentFog, u_RenderFog);
}
