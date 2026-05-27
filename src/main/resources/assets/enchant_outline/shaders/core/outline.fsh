#version 330

uniform sampler2D Sampler0;
uniform vec4 ColorModulator;

in vec2 texCoord0;
in vec4 vertexColor;

out vec4 fragColor;

void main() {
    vec4 textureColor = texture(Sampler0, texCoord0);
    if (textureColor.a < 0.1) {
        discard;
    }

    vec4 glowColor = vec4(0.85, 0.70, 0.25, 1.00); // change color

    fragColor = glowColor * ColorModulator;
}