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

    // ==========================================================
    // SUBPIXEL CONFIGURATION
    // Change this float to whatever you want!
    // 0.5 = half a texture pixel line
    // 1.5 = one and a half texture pixels line
    // ==========================================================
    float targetThickness = 0.75;

    float edgeDistance = 0.0;

    // We scan slightly past our target thickness to capture the fractional blend
    float scanLimit = ceil(targetThickness) + 1.0;

    for (float x = -scanLimit; x <= scanLimit; x += 1.0) {
        for (float y = -scanLimit; y <= scanLimit; y += 1.0) {
            // Keep a circular/Euclidean distance check for smooth subpixel rounding
            float currentDist = length(vec2(x, y));
            if (currentDist > scanLimit) continue;

            // Sample the alpha at this offset
            float sampledAlpha = texture(Sampler0, texCoord0 + vec2(x, y) * texelSize).a;

            // If we hit item opacity, calculate how much this sample contributes
            // based on how far away it is
            if (sampledAlpha > 0.01) {
                // Weight the distance by the alpha gradient
                float estimatedDist = currentDist - (sampledAlpha * 0.5);
                if (edgeDistance == 0.0 || estimatedDist < edgeDistance) {
                    edgeDistance = estimatedDist;
                }
            }
        }
    }

    // 4. Fragment routing rules
    if (baseColor.a < 0.1) {
        // We are OUTSIDE the tool.
        // Use a sharp step threshold at our exact subpixel float value!
        if (edgeDistance > 0.0 && edgeDistance <= targetThickness) {
            // Perfectly crisp, constant solid line, even at fractional widths
            fragColor = vec4(0.85, 0.2, 1.0, 1.0) * GlintAlpha;
        } else {
            discard;
        }
    } else {
        // We are INSIDE the tool. Keep internal glint sheen.
        vec4 enchantSheen = vec4(0.6, 0.2, 0.9, 0.3) * GlintAlpha;
        fragColor = mix(baseColor, enchantSheen, 0.25);
    }
}