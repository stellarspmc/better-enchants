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

    // 2. Compute precise texel step sizes
    vec2 atlasSize = vec2(textureSize(Sampler0, 0));
    vec2 texelSize = 1.0 / atlasSize;

    // 3. Strict Border Checking Logic
    bool isNearEdge = false;

    // Adjust this integer to change the exact thickness of your line (1 = 1 pixel wide, 2 = 2 pixels wide)
    int lineThickness = 1;

    // Look in a tight cross/diamond pattern for maximum pixel-art precision
    for (int x = -lineThickness; x <= lineThickness; x++) {
        for (int y = -lineThickness; y <= lineThickness; y++) {
            // Optimization: Skip checking far corners to keep the border rounded/hugging
            if (abs(x) + abs(y) > lineThickness) continue;

            float sampledAlpha = texture(Sampler0, texCoord0 + vec2(x, y) * texelSize).a;
            if (sampledAlpha > 0.1) {
                isNearEdge = true;
                break; // Found the tool! We know this pixel belongs to the outline.
            }
        }
        if (isNearEdge) break;
    }

    // 4. Fragment routing rules
    if (baseColor.a < 0.1) {
        // We are OUTSIDE the tool. If we detected the tool nearby, draw a constant crisp line.
        if (isNearEdge) {
            // Solid bright neon purple/magenta (No blurry transparency fading!)
            fragColor = vec4(0.85, 0.2, 1.0, 1.0) * GlintAlpha;
        } else {
            discard; // Empty sky / block background
        }
    } else {
        // We are INSIDE the tool. Keep your clean internal enchanted glint sheen overlay.
        vec4 enchantSheen = vec4(0.6, 0.2, 0.9, 0.3) * GlintAlpha;
        fragColor = mix(baseColor, enchantSheen, 0.25);
    }
}