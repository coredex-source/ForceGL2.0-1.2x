#version 120
// OpenGL 2.0 compatible version - using GLSL 1.20

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

vec2 getFragDistance(vec3 position) {
    return vec2(max(length(position.xz), abs(position.y)), length(position));
}

// ===== chunk_vertex.glsl =====
vec3 _vert_position;
vec2 _vert_tex_diffuse_coord;
vec2 _vert_tex_diffuse_coord_bias;
vec2 _vert_tex_light_coord;
vec4 _vert_color;
float _draw_id_float;
float _material_params_float;

#ifdef USE_VERTEX_COMPRESSION
const float VERTEX_SCALE = 32.0 / 1048576.0; // 32.0 / (1 << 20)
const float VERTEX_OFFSET = -8.0;

// Note: On GL2.0, glVertexAttribPointer doesn't support GL_UNSIGNED_INT
// So we receive the 2x uint32 as 4x uint16 (vec4 with normalized=false)
attribute vec4 a_Position;
attribute vec4 a_Color;
attribute vec2 a_TexCoord;
attribute vec4 a_LightAndData;

vec3 _deinterleave_u20x3(vec4 data) {
    // data contains 4x uint16 values as floats (not normalized)
    // Reconstruct the 2x uint32 from the 4x uint16
    // data.x = low 16 bits of first uint32, data.y = high 16 bits of first uint32
    // data.z = low 16 bits of second uint32, data.w = high 16 bits of second uint32
    float uint32_0 = data.x + (data.y * 65536.0);
    float uint32_1 = data.z + (data.w * 65536.0);
    
    // Emulate: uvec3 hi = (uvec3(data.x) >> uvec3(0u, 10u, 20u)) & 0x3FFu;
    vec3 hi;
    hi.x = mod(floor(uint32_0 / 1.0), 1024.0);         // bits 0-9 from uint32_0
    hi.y = mod(floor(uint32_0 / 1024.0), 1024.0);      // bits 10-19 from uint32_0
    hi.z = mod(floor(uint32_0 / 1048576.0), 1024.0);   // bits 20-29 from uint32_0
    
    // Emulate: uvec3 lo = (uvec3(data.y) >> uvec3(0u, 10u, 20u)) & 0x3FFu;
    vec3 lo;
    lo.x = mod(floor(uint32_1 / 1.0), 1024.0);         // bits 0-9 from uint32_1
    lo.y = mod(floor(uint32_1 / 1024.0), 1024.0);      // bits 10-19 from uint32_1
    lo.z = mod(floor(uint32_1 / 1048576.0), 1024.0);   // bits 20-29 from uint32_1
    
    // Emulate: return (hi << 10u) | lo;
    return (hi * 1024.0) + lo;
}

vec2 _get_texcoord() {
    // a_TexCoord contains uint16 values converted to float (not normalized)
    // Emulate: vec2(a_TexCoord & TEXTURE_MAX_VALUE) / float(TEXTURE_MAX_COORD)
    // TEXTURE_MAX_VALUE = 32767 (0x7FFF), TEXTURE_MAX_COORD = 32768
    return mod(a_TexCoord, 32768.0) / 32768.0;
}

vec2 _get_texcoord_bias() {
    // a_TexCoord contains uint16 values converted to float (not normalized)
    vec2 result;
    result.x = (a_TexCoord.x >= 32768.0) ? 1.0 : -1.0;
    result.y = (a_TexCoord.y >= 32768.0) ? 1.0 : -1.0;
    return result;
}

void _vert_init() {
    _vert_position = (_deinterleave_u20x3(a_Position) * VERTEX_SCALE) + VERTEX_OFFSET;
    _vert_color = a_Color;
    _vert_tex_diffuse_coord = _get_texcoord();
    _vert_tex_diffuse_coord_bias = _get_texcoord_bias();
    _vert_tex_light_coord = a_LightAndData.xy / 256.0;
    _material_params_float = a_LightAndData.z;
    _draw_id_float = a_LightAndData.w;
}
#else
#error "Vertex compression must be enabled"
#endif

// ===== chunk_matrices.glsl =====
uniform mat4 u_ProjectionMatrix;
uniform mat4 u_ModelViewMatrix;

varying vec4 v_Color;
varying vec2 v_TexCoord;

// Note: 'flat' and integer varyings are not available in GLSL 1.20
// We'll pass material as float and convert back in fragment shader
varying float v_Material;

#ifdef USE_FOG
varying vec2 v_FragDistance;
#endif

uniform vec3 u_RegionOffset;
uniform vec2 u_TexCoordShrink;

uniform sampler2D u_LightTex; // The light map texture sampler

vec3 _get_relative_chunk_coord(float pos) {
    // Convert to uint-like behavior using floor
    float p = floor(pos);
    // Emulate bit shifts and masks using division and modulo
    float x = mod(floor(p / 32.0), 8.0);
    float y = mod(p, 4.0);
    float z = mod(floor(p / 4.0), 8.0);
    return vec3(x, y, z);
}

vec3 _get_draw_translation(float pos) {
    return _get_relative_chunk_coord(pos) * vec3(16.0);
}

void main() {
    _vert_init();

    // Transform the chunk-local vertex position into world model space
    vec3 translation = u_RegionOffset + _get_draw_translation(_draw_id_float);
    vec3 position = _vert_position + translation;

#ifdef USE_FOG
    v_FragDistance = getFragDistance(position);
#endif

    // Transform the vertex position into model-view-projection space
    gl_Position = u_ProjectionMatrix * u_ModelViewMatrix * vec4(position, 1.0);

    // Add the light color to the vertex color, and pass the texture coordinates to the fragment shader
    v_Color = _vert_color * texture2D(u_LightTex, _vert_tex_light_coord);
    v_TexCoord = (_vert_tex_diffuse_coord_bias * u_TexCoordShrink) + _vert_tex_diffuse_coord;

    v_Material = _material_params_float;
}
