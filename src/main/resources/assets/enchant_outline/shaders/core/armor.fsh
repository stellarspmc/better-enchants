#version 330

in vec4 vertexColor;
uniform vec4 ColorModulator;

out vec4 fragColor;

void main() {
    vec4 baseColor = vec4(0.85, 0.70, 0.25, 1.00);
    fragColor = baseColor * ColorModulator;

    if (fragColor.a < 0.1) {
        discard;
    }
}