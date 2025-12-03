// The position of the vertex around the model origin
vec3 _vert_position;

// The block texture coordinate of the vertex
vec2 _vert_tex_diffuse_coord;
vec2 _vert_tex_diffuse_coord_bias;

// The light texture coordinate of the vertex
vec2 _vert_tex_light_coord;

// The color of the vertex
vec4 _vert_color;

// The index of the draw command which this vertex belongs to
uint _draw_id;
float _draw_id_float; // GLSL 1.20 compatible version

// The material bits for the primitive
uint _material_params;
float _material_params_float; // GLSL 1.20 compatible version

#ifdef USE_VERTEX_COMPRESSION
const uint POSITION_BITS        = 20u;
const uint POSITION_MAX_COORD   = 1u << POSITION_BITS;
const uint POSITION_MAX_VALUE   = POSITION_MAX_COORD - 1u;

const uint TEXTURE_BITS         = 15u;
const uint TEXTURE_MAX_COORD    = 1u << TEXTURE_BITS;
const uint TEXTURE_MAX_VALUE    = TEXTURE_MAX_COORD - 1u;

const float VERTEX_SCALE = 32.0 / float(POSITION_MAX_COORD);
const float VERTEX_OFFSET = -8.0;

// OpenGL 2.0 Compatibility: Use vec instead of uvec for attributes
// Data comes in as normalized floats [0,1] and needs to be converted back
in vec2 a_Position;
in vec4 a_Color;
in vec2 a_TexCoord;
in vec4 a_LightAndData;

uvec3 _deinterleave_u20x3(vec2 data) {
    // Convert normalized float [0,1] back to full uint32 range
    uvec2 udata = uvec2(data * 4294967295.0);
    
    uvec3 hi = (uvec3(udata.x) >> uvec3(0u, 10u, 20u)) & 0x3FFu;
    uvec3 lo = (uvec3(udata.y) >> uvec3(0u, 10u, 20u)) & 0x3FFu;

    return (hi << 10u) | lo;
}

vec2 _get_texcoord() {
    // Convert normalized float [0,1] back to uint16 range
    uvec2 texCoord = uvec2(a_TexCoord * 65535.0);
    return vec2(texCoord & TEXTURE_MAX_VALUE) / float(TEXTURE_MAX_COORD);
}

vec2 _get_texcoord_bias() {
    // Convert normalized float [0,1] back to uint16 range
    uvec2 texCoord = uvec2(a_TexCoord * 65535.0);
    return mix(vec2(-1.0), vec2(1.0), bvec2(texCoord >> TEXTURE_BITS));
}

void _vert_init() {
    _vert_position = (_deinterleave_u20x3(a_Position) * VERTEX_SCALE) + VERTEX_OFFSET;
    _vert_color = a_Color;
    _vert_tex_diffuse_coord = _get_texcoord();
    _vert_tex_diffuse_coord_bias = _get_texcoord_bias();

    // a_LightAndData.xy are normalized [0,1], convert to [0,255] then divide by 256
    _vert_tex_light_coord = (a_LightAndData.xy * 255.0) / 256.0;

    // Convert normalized float [0,1] back to uint8 range [0,255]
    _material_params = uint(a_LightAndData[2] * 255.0);
    _draw_id = uint(a_LightAndData[3] * 255.0);
    
    // GLSL 1.20 compatible float versions
    _material_params_float = a_LightAndData[2] * 255.0;
    _draw_id_float = a_LightAndData[3] * 255.0;
}

#else
#error "Vertex compression must be enabled"
#endif
