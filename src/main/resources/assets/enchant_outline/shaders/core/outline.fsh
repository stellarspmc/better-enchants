#version 330

uniform sampler2D Sampler0;
uniform vec4 ColorModulator;
uniform float GlintAlpha;

in vec2 texCoord0;
in vec4 vertexColor;

out vec4 fragColor;

void main() {
    vec4 baseColor = texture(Sampler0, texCoord0) * vertexColor * ColorModulator;

    vec2 atlasSize = vec2(textureSize(Sampler0, 0));
    vec2 texelSize = 1.0 / atlasSize;

    if (baseColor.a < 0.1) {
        // We are OUTSIDE the tool.

        // Find exactly where we are inside the current empty pixel grid space (0.0 to 1.0)
        vec2 pixelCoord = texCoord0 * atlasSize;
        vec2 subPixel = fract(pixelCoord);

        // Look at the immediate 8 surrounding pixel blocks
        float aL  = texture(Sampler0, texCoord0 + vec2(-texelSize.x, 0.0)).a;
        float aR  = texture(Sampler0, texCoord0 + vec2( texelSize.x, 0.0)).a;
        float aU  = texture(Sampler0, texCoord0 + vec2(0.0, -texelSize.y)).a;
        float aD  = texture(Sampler0, texCoord0 + vec2(0.0,  texelSize.y)).a;
        float aTL = texture(Sampler0, texCoord0 + vec2(-texelSize.x, -texelSize.y)).a;
        float aTR = texture(Sampler0, texCoord0 + vec2( texelSize.x, -texelSize.y)).a;
        float aBL = texture(Sampler0, texCoord0 + vec2(-texelSize.x,  texelSize.y)).a;
        float aBR = texture(Sampler0, texCoord0 + vec2( texelSize.x,  texelSize.y)).a;

        float minDist = 999.0;

        // Calculate precise mathematical distance to adjacent solid orthogonal borders
        if (aR > 0.1) minDist = min(minDist, 1.0 - subPixel.x);
        if (aL > 0.1) minDist = min(minDist, subPixel.x);
        if (aD > 0.1) minDist = min(minDist, 1.0 - subPixel.y);
        if (aU > 0.1) minDist = min(minDist, subPixel.y);

        // Calculate Euclidean distance for wrapping cleanly around diagonal corners
        if (aTL > 0.1 && aL < 0.1 && aU < 0.1) minDist = min(minDist, length(vec2(subPixel.x, subPixel.y)));
        if (aTR > 0.1 && aR < 0.1 && aU < 0.1) minDist = min(minDist, length(vec2(1.0 - subPixel.x, subPixel.y)));
        if (aBL > 0.1 && aL < 0.1 && aD < 0.1) minDist = min(minDist, length(vec2(subPixel.x, 1.0 - subPixel.y)));
        if (aBR > 0.1 && aR < 0.1 && aD < 0.1) minDist = min(minDist, length(vec2(1.0 - subPixel.x, 1.0 - subPixel.y)));

        // ==========================================================
        // TRUE SUBPIXEL CONFIGURATION
        // Must be between 0.01 and 1.0 (1.0 = standard full pixel width)
        // 0.15 = Razor thin 15% subpixel vector line
        // ==========================================================
        float targetThickness = 0.15;

        if (minDist <= targetThickness) {
            // Constant, sharp Gold stroke
            fragColor = vec4(1.0, 0.78, 0.15, 1.0) * GlintAlpha;
        } else {
            discard; // Pure empty space
        }
    } else {
        // We are INSIDE the tool body.
        vec4 goldSheen = vec4(0.98, 0.80, 0.20, 0.25) * GlintAlpha;
        fragColor = mix(baseColor, goldSheen, 0.18);
    }
}