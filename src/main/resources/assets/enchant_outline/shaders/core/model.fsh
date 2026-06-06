#version 150

#moj_import <minecraft:fog.glsl>

uniform float FogStart;
uniform float FogEnd;
uniform vec4 FogColor;
uniform vec4 ColorModulator;

in float vertexDistance;
in vec4 vertexColor;
in vec2 texCoord0;

out vec4 fragColor;

void main() {
    vec4 baseColor = vertexColor * ColorModulator;
    fragColor = linear_fog(baseColor, vertexDistance, FogStart, FogEnd, FogColor);
}