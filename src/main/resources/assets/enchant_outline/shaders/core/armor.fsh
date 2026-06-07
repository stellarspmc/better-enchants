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
    vec4 finalColor;
    if (vertexColor.a < 1.0) finalColor = vec4(vertexColor.rgb, 1.0) * ColorModulator;
    else {
        vec4 yellowTint = vec4(0.85, 0.70, 0.25, 1.0);
        finalColor = textureColor * yellowTint * ColorModulator;
        finalColor.rgb *= 1.5;
    }

    fragColor = linear_fog(finalColor, vertexDistance, FogStart, FogEnd, FogColor);
}