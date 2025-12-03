// The projection matrix
uniform mat4 u_ProjectionMatrix;

// The model-view matrix
uniform mat4 u_ModelViewMatrix;

// The model-view-projection matrix
// Note: In GLSL 1.20 we can't use #define with expressions, so this is removed
// You'll need to manually multiply matrices where needed
