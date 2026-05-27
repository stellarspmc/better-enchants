#version 330

uniform sampler2D Sampler0;
uniform vec4 ColorModulator;

in vec2 texCoord0;
in vec4 vertexColor;

out vec4 fragColor;

void main() {
    // Sample the item texture to get the silhouette shape
    vec4 textureColor = texture(Sampler0, texCoord0);

    // If it's a transparent pixel in the texture, don't draw anything
    if (textureColor.a < 0.1) {
        discard;
    }

    // ==========================================================
    // YOUR GLOW COLOR
    // Pure, solid neon color for the backdrop outline pass
    // ==========================================================
    vec4 glowColor = vec4(0.85, 0.70, 0.25, 1.00); // Radiant Neon Purple

    // Multiply by ColorModulator to respect item transparency shifts
    fragColor = glowColor * ColorModulator;
}