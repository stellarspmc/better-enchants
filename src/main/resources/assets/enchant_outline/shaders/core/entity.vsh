#version 150

in vec3 Position;
in vec4 Color;
in vec2 UV0;
in ivec2 UV1;
in ivec2 UV2;
in vec3 Normal;

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;
uniform mat4 TextureMat;
uniform float Thickness;

out vec2 texCoord0;
out vec2 texCoord2;
out vec4 vertexColor;

void main() {
	vec3 inflatedPosition = Position;

	if (length(Normal) > 0.001) {
		inflatedPosition += normalize(Normal) * Thickness;
	}

	vec4 clipPos = ProjMat * ModelViewMat * vec4(Position, 1.0);
	vec4 clipNormal = ProjMat * ModelViewMat * vec4(Normal, 0.0);
	clipPos.xy += normalize(clipNormal.xy) * Thickness * clipPos.w;

	gl_Position = clipPos; // ?

	//gl_Position = ProjMat * ModelViewMat * vec4(inflatedPosition, 1.0);
	texCoord0 = (TextureMat * vec4(UV0, 0.0, 1.0)).xy;
	texCoord2 = UV2;
	vertexColor = Color;
}