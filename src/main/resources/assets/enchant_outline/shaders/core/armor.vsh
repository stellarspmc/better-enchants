#version 330

in vec3 Position;
in vec4 Color;
in vec2 UV0;
in ivec2 UV1;
in ivec2 UV2;
in vec3 Normal;

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;

out vec4 vertexColor;

void main() {
	vec3 normalizedNormal = normalize(Normal);
	vec3 inflatedPosition = Position + (normalizedNormal * 0.045); // thickness
	gl_Position = ProjMat * ModelViewMat * vec4(inflatedPosition, 1.0);
	vertexColor = Color;
}