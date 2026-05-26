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

    // 2. Compute dynamic texel step mapping to isolate atlas boundaries
    vec2 atlasSize = vec2(textureSize(Sampler0, 0));
    vec2 texelSize = 1.0 / atlasSize;

    // 3. Scan neighborhood alpha properties using a procedural kernel loop
    float alphaAccumulation = 0.0;
    float scanRadius = 1.5; // Controls the absolute thickness of the outline edge

    for (float x = -scanRadius; x <= scanRadius; x += 1.0) {
        for (float y = -scanRadius; y <= scanRadius; y += 1.0) {
            // Sample neighboring spots on the atlas texture
            float sampledAlpha = texture(Sampler0, texCoord0 + vec2(x, y) * texelSize).a;
            alphaAccumulation += sampledAlpha;
        }
    }

    // Normalize our neighborhood density
    float totalSamples = (scanRadius * 2.0 + 1.0) * (scanRadius * 2.0 + 1.0);
    float edgeScore = alphaAccumulation / totalSamples;

    // 4. Fragment routing rules
    if (baseColor.a < 0.1) {
        // If the point is empty but near a solid asset edge: Draw the tracking glow layer
        if (edgeScore > 0.02) {
            // Beautiful purple neon bloom profile matching image_bfc3aa.jpg
            float glowIntensity = smoothstep(0.02, 0.5, edgeScore) * (1.0 - baseColor.a);
            fragColor = vec4(0.75, 0.15, 0.95, glowIntensity * 0.9) * GlintAlpha;
        } else {
            discard; // Empty space far outside the tool silhouette
        }
    } else {
        // Main body interior: Blend standard tool graphics with a purple enchanted glint sheen
        vec4 enchantSheen = vec4(0.55, 0.2, 0.9, 0.35) * GlintAlpha;
        fragColor = mix(baseColor, enchantSheen, 0.3);
    }
}