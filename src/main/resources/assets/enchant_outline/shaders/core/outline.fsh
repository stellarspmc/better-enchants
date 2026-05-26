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
    // 1. Fetch the primary pixel color
    vec4 baseColor = texture(Sampler0, texCoord0) * vertexColor * ColorModulator;

    // 2. Determine texture resolution bounds to compute precise texel sizes
    vec2 alphaUV = texCoord0;
    vec2 texelSize = 1.0 / vec2(textureSize(Sampler0, 0));

    // 3. Sample adjacent texel alpha transparency flags
    float alphaAbove = texture(Sampler0, alphaUV + vec2(0.0, texelSize.y)).a;
    float alphaBelow = texture(Sampler0, alphaUV - vec2(0.0, texelSize.y)).a;
    float alphaLeft  = texture(Sampler0, alphaUV - vec2(texelSize.x, 0.0)).a;
    float alphaRight = texture(Sampler0, alphaUV + vec2(texelSize.x, 0.0)).a;

    // Also sample diagonals to soften item corner silhouettes
    float alphaTopLeft     = texture(Sampler0, alphaUV + vec2(-texelSize.x,  texelSize.y)).a;
    float alphaTopRight    = texture(Sampler0, alphaUV + vec2( texelSize.x,  texelSize.y)).a;
    float alphaBottomLeft  = texture(Sampler0, alphaUV + vec2(-texelSize.x, -texelSize.y)).a;
    float alphaBottomRight = texture(Sampler0, alphaUV + vec2( texelSize.x, -texelSize.y)).a;

    // Accumulate total surrounding alpha weights
    float edgeWeight = alphaAbove + alphaBelow + alphaLeft + alphaRight +
    alphaTopLeft + alphaTopRight + alphaBottomLeft + alphaBottomRight;

    // 4. Determine if the fragment qualifies as an edge outline
    // If the center is empty but surrounded by solid pixels, it's an external border edge
    if (baseColor.a < 0.1 && edgeWeight > 0.1) {
        // Build a glowing purple hue matching image 2
        vec4 glowColor = vec4(0.65, 0.2, 0.9, 0.85) * GlintAlpha;

        // Handle Fog calculations
        float fogDistance = length(texCoord2) / 255.0;
        float fogFactor = smoothstep(FogStart, FogEnd, fogDistance);
        fragColor = mix(glowColor, FogColor, fogFactor);
    }
    else if (baseColor.a >= 0.1) {
        // It's the inside body of the tool.
        // We blend a subtle tint overlay or let the base tool shine through.
        vec4 internalGlint = vec4(0.5, 0.1, 0.8, 0.3) * GlintAlpha;
        vec4 finalColor = mix(baseColor, internalGlint, 0.4);

        float fogDistance = length(texCoord2) / 255.0;
        float fogFactor = smoothstep(FogStart, FogEnd, fogDistance);
        fragColor = mix(finalColor, FogColor, fogFactor);
    }
    else {
        // Complete transparent airspace outside of the tool's glow threshold
        discard;
    }
}