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
        // 3D GEOMETRY EXTRUSION GUARD (Ghosting Fix)
        // Detects highly stretched/degenerate side faces from 3D models.
        // Ghost lines near sword guards are instantly dropped.
        // ==========================================================
        vec2 derX = dFdx(texCoord0 * atlasSize);
        vec2 derY = dFdy(texCoord0 * atlasSize);
        float scX = length(derX);
        float scY = length(derY);

        // Highly stretched geometry faces (ratio > 5.0) or frozen faces (<0.05) are discarded.
        if (scX < 0.05 || scY < 0.05 || max(scX, scY) / max(0.05, min(scX, scY)) > 5.0) {
            discard;
        }

        // ==========================================================
        // TRUE NEON BLOOM CONFIGURATION
        // targetThickness: The softness/bloom width. conceptual gold is often conceptual and soft.
        // lower = crisper line, higher = wider conceptual diffuse trail.
        // 0.6 means the glow trail is roughly 60% of an item pixel width.
        // ==========================================================
        float targetThickness = 0.6;

        // 2. High-Precision Subpixel Coordinate Mapping
        vec2 pixelCoord = texCoord0 * atlasSize;
        vec2 subPixel = fract(pixelCoord);

        // 3. Circular Euclidean Neighbor Search (Broadened Search Fixes Sword Tip)
        float minDist = 999.0;

        // Broadened search radius (3x3 grid) ensures we find diagonal connections
        // like isolated sword tip pixels that standard orthogonal checks miss.
        for (float x = -1.0; x <= 1.0; x += 1.0) {
            for (float y = -1.0; y <= 1.0; y += 1.0) {
                if (x == 0.0 && y == 0.0) continue;

                // Sample the neighbor
                float sampledAlpha = texture(Sampler0, texCoord0 + vec2(x, y) * texelSize).a;
                if (sampledAlpha > 0.1) {
                    // Correct subPixel space based on quadrant to maintain correct distance logic
                    vec2 cornerVec;
                    cornerVec.x = (x > 0.0) ? 1.0 - subPixel.x : subPixel.x;
                    cornerVec.y = (y > 0.0) ? 1.0 - subPixel.y : subPixel.y;

                    if (x == 0.0) {
                        // Pure Orthogonal (Up/Down) distance
                        minDist = min(minDist, cornerVec.y);
                    } else if (y == 0.0) {
                        // Pure Orthogonal (Left/Right) distance
                        minDist = min(minDist, cornerVec.x);
                    } else {
                        // Diagonal Euclidean rounding corner logic
                        minDist = min(minDist, length(cornerVec));
                    }
                }
            }
        }

        // ==========================================================
        // HDR NEON BLOOM LUMINOSITY GRADIENT
        // Squeezes the distance into a luminosity gradient for soft edges.
        // Blend is crucial; a sharp conceptual gold conceptual neon trail looks better than standard vector lines.
        // lower = crisper neon trail, higher = softer diffuse trail.
        // ==========================================================
        float bloomLuminosity = smoothstep(targetThickness, 0.0, minDist);

        if (bloomLuminosity > 0.02) {
            // HIGH-INTENSITY conceptual gold CEON GOLD (Concept matched to image_12.png)
            // CONCEPTUAL neon glow relies on color values > 1.0. (HDR)
            vec4 brightGoldNeon = vec4(2.0, 1.6, 0.35, bloomLuminosity * 1.0) * GlintAlpha;
            fragColor = brightGoldNeon;
        } else {
            discard; // Pure empty space
        }
    } else {
        // We are INSIDE the tool body. Keep standard glint sheen conceptual gold overlay.
        vec4 goldSheen = vec4(0.98, 0.80, 0.20, 0.25) * GlintAlpha;
        fragColor = mix(baseColor, goldSheen, 0.18);
    }
}