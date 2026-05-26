#version 150

uniform sampler2D Sampler0;
uniform vec4 ColorModulator;
uniform float GlintAlpha;
uniform vec2 ScreenSize;

in vec2 texCoord0;

out vec4 fragColor;

void main() {
    fragColor = vec4(1.0, 0.0, 0.0, 0.5);   // semi‑transparent red
    fragColor.rgb += 0.0 * ScreenSize.x;     // keep uniform alive
}