#version 150

#moj_import <minecraft:fog.glsl>

uniform sampler2D Sampler0;

uniform float FogStart;
uniform float FogEnd;
uniform vec4 FogColor;
uniform vec4 ColorModulator;

in float vertexDistance;
in vec4 vertexColor;
in vec2 texCoord0;

out vec4 fragColor;

void main() {
    vec4 textureColor = texture(Sampler0, texCoord0);
    if (textureColor.a < 0.1) discard;

    vec4 baseColor = vertexColor * ColorModulator;
    fragColor = linear_fog(baseColor, vertexDistance, FogStart, FogEnd, FogColor);
}