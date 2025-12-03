// GLSL 1.20 compatible version - no arrays of floats
const float ALPHA_CUTOFF_0 = 0.0;
const float ALPHA_CUTOFF_1 = 0.1;
const float ALPHA_CUTOFF_2 = 0.1;
const float ALPHA_CUTOFF_3 = 1.0;

bool _material_use_mips(uint material) {
    return ((material >> 0u) & 1u) != 0u;
}

bool _material_use_mips_float(float material) {
    float m = floor(material + 0.5);
    return mod(floor(m), 2.0) != 0.0;
}

float _material_alpha_cutoff(uint material) {
    uint index = (material >> 1u) & 3u;
    if (index == 0u) return ALPHA_CUTOFF_0;
    if (index == 1u) return ALPHA_CUTOFF_1;
    if (index == 2u) return ALPHA_CUTOFF_2;
    return ALPHA_CUTOFF_3;
}

float _material_alpha_cutoff_float(float material) {
    float m = floor(material + 0.5);
    float index = mod(floor(m / 2.0), 4.0);
    if (index < 0.5) return ALPHA_CUTOFF_0;
    if (index < 1.5) return ALPHA_CUTOFF_1;
    if (index < 2.5) return ALPHA_CUTOFF_2;
    return ALPHA_CUTOFF_3;
}
