#version 150

#moj_import <light.glsl>

in vec3 Position;
in vec2 UV0;
in vec4 Color;
in vec3 Normal;

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;
uniform vec3 ChunkOffset;
uniform float GameTime;

uniform vec3 Light0_Direction;
uniform vec3 Light1_Direction;

out vec2 texCoord0;
out vec4 vertexColor;

float fastSin(float x) {
    x = mod(x + 3.14159265, 6.28318531) - 3.14159265;
    return x * (1.0 - abs(x) / 3.14159265);
}

float noise(vec2 co) {
    return fract(fastSin(dot(co.xy, vec2(12.9898, 78.233))) * 43758.5453);
}

void main() {
    vec3 position = Position;

    float delta = GameTime * 943 + (Position.x + Position.y + Position.z);
    float noise = noise(Position.xz);
    float wave = (fastSin(delta) + fastSin(delta + 1.57079632)) * noise;
    position.x += noise(Position.yz) / 2;
    position.y += wave / 2;
    position.z += noise(Position.xy) / 2;

    gl_Position = ProjMat * ModelViewMat * vec4(position + ChunkOffset, 1.0);
    texCoord0 = UV0;
    vertexColor = minecraft_mix_light(Light0_Direction, Light1_Direction, Normal, Color);
    vertexColor.rgb *= wave;
}
