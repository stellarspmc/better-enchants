#version 330

uniform sampler2D Sampler0;
uniform vec4 ColorModulator;
uniform float GlintAlpha;

in vec2 texCoord0;
in vec4 vertexColor;

out vec4 fragColor;

void main() {
    // 1. Core color extraction
    vec4 baseColor = texture(Sampler0, texCoord0) * vertexColor * ColorModulator;

    vec2 atlasSize = vec2(textureSize(Sampler0, 0));
    vec2 texelSize = 1.0 / atlasSize;

    if (baseColor.a < 0.1) {
        // ==========================================================
        // 3D GEOMETRY EXTRUSION GUARD (Anti-Ghosting)
        // Measures texture coordinate scaling across screen pixels.
        // Distorted/ghost quads on 3D models are instantly dropped.
        // ==========================================================
        vec2 texelsPerPixelX = dFdx(texCoord0 * atlasSize);
        vec2 texelsPerPixelY = dFdy(texCoord0 * atlasSize);
        float distortionCheck = max(length(texelsPerPixelX), length(texelsPerPixelY));

        if (distortionCheck > 3.0 || distortionCheck < 0.01) {
            discard;
        }

        // 2. High-Precision Subpixel Coordinate Mapping
        vec2 pixelCoord = texCoord0 * atlasSize;
        vec2 subPixel = fract(pixelCoord);

        // 3. Scan 8-way neighbors for solid item pixels
        float aL  = texture(Sampler0, texCoord0 + vec2(-texelSize.x, 0.0)).a;
        float aR  = texture(Sampler0, texCoord0 + vec2( texelSize.x, 0.0)).a;
        float aU  = texture(Sampler0, texCoord0 + vec2(0.0, -texelSize.y)).a;
        float aD  = texture(Sampler0, texCoord0 + vec2(0.0,  texelSize.y)).a;
        float aTL = texture(Sampler0, texCoord0 + vec2(-texelSize.x, -texelSize.y)).a;
        float aTR = texture(Sampler0, texCoord0 + vec2( texelSize.x, -texelSize.y)).a;
        float aBL = texture(Sampler0, texCoord0 + vec2(-texelSize.x,  texelSize.y)).a;
        float aBR = texture(Sampler0, texCoord0 + vec2( texelSize.x,  texelSize.y)).a;

        float minDist = 999.0;

        // Orthogonal distance checks
        if (aR > 0.1) minDist = min(minDist, 1.0 - subPixel.x);
        if (aL > 0.1) minDist = min(minDist, subPixel.x);
        if (aD > 0.1) minDist = min(minDist, 1.0 - subPixel.y);
        if (aU > 0.1) minDist = min(minDist, subPixel.y);

        // Euclidean corner rounding checks
        if (aTL > 0.1 && aL < 0.1 && aU < 0.1) minDist = min(minDist, length(vec2(subPixel.x, subPixel.y)));
        if (aTR > 0.1 && aR < 0.1 && aU < 0.1) minDist = min(minDist, length(vec2(1.0 - subPixel.x, subPixel.y)));
        if (aBL > 0.1 && aL < 0.1 && aD < 0.1) minDist = min(minDist, length(vec2(subPixel.x, 1.0 - subPixel.y)));
        if (aBR > 0.1 && aR < 0.1 && aD < 0.1) minDist = min(minDist, length(vec2(1.0 - subPixel.x, 1.0 - subPixel.y)));

        // Precise Subpixel Line Thickness Configuration (0.15 = 15% of an item pixel)
        float targetThickness = 0.2;

        if (minDist <= targetThickness) {
            // Perfectly constant, crisp Gold Vector Stroke
            fragColor = vec4(1.0, 0.78, 0.15, 1.0) * GlintAlpha;
        } else {
            discard;
        }
    } else {
        // We are INSIDE the tool body.
        // Soft amber/gold internal enchanted glint overlay
        vec4 goldSheen = vec4(0.98, 0.80, 0.20, 0.25) * GlintAlpha;
        fragColor = mix(baseColor, goldSheen, 0.18);
    }
}