#version 330

in vec3 Position;
in vec4 Color;
in vec2 UV0;
in vec2 UV2;

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;
uniform mat4 TextureMat;
uniform vec2 ScreenSize;

out vec2 texCoord0;
out vec2 texCoord2;
out vec4 vertexColor;

void main() {
    gl_Position = ProjMat * ModelViewMat * vec4(Position, 1.0);
    texCoord0 = (TextureMat * vec4(UV0, 0.0, 1.0)).xy;
    texCoord2 = UV2;
    vertexColor = Color;
}