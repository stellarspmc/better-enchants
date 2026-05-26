#version 330

#moj_import <minecraft:fog.glsl>

in vec3 Position;
in vec4 Color;
in vec2 UV0;
in ivec2 UV2;
in vec3 Normal;

uniform mat4 ProjMat;
uniform mat4 ModelViewMat;
uniform vec3 ModelOffset;

uniform sampler2D Sampler2;
uniform int FogShape; // 1.21.1 Fog shape variable

out float vertexDistance; // Single output distance
out vec4 vertexColor;
out vec2 texCoord0;

vec4 minecraft_sample_lightmap(sampler2D lightMap, ivec2 uv) {
    return texture(lightMap, clamp((uv / 256.0) + 0.5 / 16.0, vec2(0.5 / 16.0), vec2(15.5 / 16.0)));
}

void main() {
    vec3 pos = Position + ModelOffset;
    gl_Position = ProjMat * ModelViewMat * vec4(pos, 1.0);

    // 1.21.1 standard fog calculation
    vertexDistance = fog_distance(pos, FogShape);

    vertexColor = Color * minecraft_sample_lightmap(Sampler2, UV2);
    texCoord0 = UV0;
}