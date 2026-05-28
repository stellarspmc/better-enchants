#version 150

uniform sampler2D Sampler0;
uniform vec4 ColorModulator;
uniform vec4 GlowColor;

in vec2 texCoord0;
in vec4 vertexColor;

out vec4 fragColor;

void main() {
    vec4 textureColor = texture(Sampler0, texCoord0);
    if (textureColor.a < 0.1) {
        discard;
    }

    fragColor = GlowColor * ColorModulator;
}