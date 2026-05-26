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
        // PERFECT 3D SIDE-QUAD FILTER (Fixes Ghost Lines & Side-View Glow)
        // Minecraft builds 3D item thickness using extruded side faces.
        // These side faces map to a constant U or V line in the texture atlas,
        // meaning their texture gradient lengths collapse to zero.
        // Flat faces maintain active gradients even at sharp perspective angles!
        // ==========================================================
        vec2 gradU = vec2(dFdx(texCoord0.x * atlasSize.x), dFdy(texCoord0.x * atlasSize.x));
        vec2 gradV = vec2(dFdx(texCoord0.y * atlasSize.y), dFdy(texCoord0.y * atlasSize.y));

        // If either texture gradient collapses near 0, we are on a 3D thickness quad.
        // Discarding it deletes the ghosting artifacts completely.
        if (length(gradU) < 0.01 || length(gradV) < 0.01) {
            discard;
        }

        // ==========================================================
        // TRUE SUBPIXEL BLOOM CONFIGURATION
        // targetThickness: Controls the softness/width of the glow trail.
        // ==========================================================
        float targetThickness = 1.25;

        // High-Precision Subpixel Coordinate Mapping
        vec2 pixelCoord = texCoord0 * atlasSize;
        vec2 subPixel = fract(pixelCoord);

        // 5x5 PERFECT BOX SEARCH
        float minDist = 999.0;

        for (float x = -2.0; x <= 2.0; x += 1.0) {
            for (float y = -2.0; y <= 2.0; y += 1.0) {
                if (x == 0.0 && y == 0.0) continue;

                // Sample the neighbor pixel
                float sampledAlpha = texture(Sampler0, texCoord0 + vec2(x, y) * texelSize).a;

                if (sampledAlpha > 0.1) {
                    // Precise distance calculation to the neighbor's pixel boundary box
                    vec2 distanceToBox = max(vec2(0.0), vec2(x, y) - subPixel) + max(vec2(0.0), subPixel - vec2(x + 1.0, y + 1.0));
                    float boxDist = length(distanceToBox);

                    minDist = min(minDist, boxDist);
                }
            }
        }

        // Smoothly blend the edge based on our precise subpixel distance
        float bloomLuminosity = smoothstep(targetThickness, 0.0, minDist);

        if (bloomLuminosity > 0.02) {
            // Your custom tuned subtle gold color palette
            vec4 brightGoldNeon = vec4(0.85, 0.70, 0.25, bloomLuminosity * 1.0) * GlintAlpha;
            fragColor = brightGoldNeon;
        } else {
            discard; // Pure empty space
        }
    } else {
        // Inside the tool body - matching your preferred custom gold sheen values
        vec4 goldSheen = vec4(0.85, 0.70, 0.25, 0.25) * GlintAlpha;
        fragColor = mix(baseColor, goldSheen, 0.18);
    }
}