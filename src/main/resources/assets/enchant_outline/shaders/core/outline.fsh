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
        // 3D GEOMETRY EXTRUSION GUARD (Ghosting & Artifact Fix)
        // Detects highly stretched side faces from 3D models using a scaling ratio.
        // REMOVED the 'scX < 0.05' check so the glint NEVER disappears up close!
        // ==========================================================
        vec2 derX = dFdx(texCoord0 * atlasSize);
        vec2 derY = dFdy(texCoord0 * atlasSize);
        float scX = length(derX);
        float scY = length(derY);

        // Stretched 3D side quads have massive coordinate distortion ratios (> 6.0),
        // while flat front faces stay uniform near 1.0 regardless of distance.
        float maxSc = max(scX, scY);
        float minSc = max(0.0001, min(scX, scY)); // Prevent division by zero

        if ((maxSc / minSc) > 6.0) {
            discard;
        }

        // ==========================================================
        // TRUE SUBPIXEL BLOOM CONFIGURATION
        // targetThickness: Controls the softness/width of the glow trail.
        // Set slightly above 1.0 to give tips a beautiful, fully rounded wrap.
        // ==========================================================
        float targetThickness = 1.25;

        // High-Precision Subpixel Coordinate Mapping
        vec2 pixelCoord = texCoord0 * atlasSize;
        vec2 subPixel = fract(pixelCoord);

        // ==========================================================
        // 5x5 PERFECT BOX SEARCH (Fixes Missing Tips & Bottoms)
        // Expanding to a 5x5 grid ensures long items and isolated diagonal tips
        // have enough coordinate runway to calculate smooth subpixel corners.
        // ==========================================================
        float minDist = 999.0;

        for (float x = -2.0; x <= 2.0; x += 1.0) {
            for (float y = -2.0; y <= 2.0; y += 1.0) {
                if (x == 0.0 && y == 0.0) continue;

                // Sample the neighbor pixel
                float sampledAlpha = texture(Sampler0, texCoord0 + vec2(x, y) * texelSize).a;

                if (sampledAlpha > 0.1) {
                    // Hardware-optimized calculation for exact distance to the neighbor's pixel boundary
                    vec2 distanceToBox = max(vec2(0.0), vec2(x, y) - subPixel) + max(vec2(0.0), subPixel - vec2(x + 1.0, y + 1.0));
                    float boxDist = length(distanceToBox);

                    minDist = min(minDist, boxDist);
                }
            }
        }

        // Smoothly blend the edge based on our precise subpixel distance
        float bloomLuminosity = smoothstep(targetThickness, 0.0, minDist);

        if (bloomLuminosity > 0.02) {
            vec4 brightGoldNeon = vec4(0.85, 0.70, 0.25, bloomLuminosity * 1.0) * GlintAlpha;
            fragColor = brightGoldNeon;
        } else {
            discard; // Pure empty space
        }
    } else {
        vec4 goldSheen = vec4(0.85, 0.70, 0.25, 0.25) * GlintAlpha;
        fragColor = mix(baseColor, goldSheen, 0.18);
    }
}