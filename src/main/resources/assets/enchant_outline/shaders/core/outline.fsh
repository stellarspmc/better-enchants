#version 330

uniform sampler2D Sampler0;
uniform vec4 ColorModulator;
uniform float GlintAlpha;
uniform vec4 FogColor;
uniform float FogStart;
uniform float FogEnd;

in vec2 texCoord0;
in vec2 texCoord2;
in vec4 vertexColor;

out vec4 fragColor;

void main() {
    vec4 baseColor = texture(Sampler0, texCoord0) * vertexColor * ColorModulator;

    // Check if we are at the edge of the opaque area
    vec2 texelSize = 1.0 / vec2(textureSize(Sampler0, 0));
    float alphaSum = 0.0;

    // Only sample the alpha channel to find the "border"
    for (float x = -1.0; x <= 1.0; x++) {
        for (float y = -1.0; y <= 1.0; y++) {
            alphaSum += texture(Sampler0, texCoord0 + vec2(x, y) * texelSize).a;
        }
    }

    // If current pixel is transparent but neighbors are opaque, it's an edge!
    if (baseColor.a < 0.1 && alphaSum > 0.0) {
        fragColor = vec4(1.0, 1.0, 1.0, 1.0); // Your outline color (White here)
    } else {
        fragColor = baseColor; // Draw normal item
    }
}