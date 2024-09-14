#version 150

in vec3 Position;
in vec4 Color;
in vec2 UV0;

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;
uniform mat4 TransMat;
uniform vec3 ChunkOffset;

out vec4 vertexColor;
out vec2 texCoord;

void main() {
    gl_Position = ProjMat * ModelViewMat * TransMat * vec4(Position + ChunkOffset, 1.0);
    vertexColor = Color;
    texCoord = UV0;
}
