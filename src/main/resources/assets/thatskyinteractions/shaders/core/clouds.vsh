#version 150

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

#define MINECRAFT_LIGHT_POWER   (0.6)
#define MINECRAFT_AMBIENT_LIGHT (0.4)

vec4 minecraft_mix_light(vec3 lightDir0, vec3 lightDir1, vec3 normal, vec4 color) {
    float light0 = max(0.0, dot(lightDir0, normal));
    float light1 = max(0.0, dot(lightDir1, normal));
    float lightAccum = min(1.0, (light0 + light1) * MINECRAFT_LIGHT_POWER + MINECRAFT_AMBIENT_LIGHT);
    return vec4(color.rgb * lightAccum, color.a);
}

float fastSin(float x) {
    x = mod(x + 3.14159265, 6.28318531) - 3.14159265;
    return x * (1.0 - abs(x) / 3.14159265);
}

float noise0(vec2 co) {
    return fract(fastSin(dot(co.xy, vec2(12.9898, 78.233))) * 43758.5453);
}

void main() {
    vec3 position = Position;

    float delta = GameTime * 943 + (Position.x + Position.z);
    float noise = noise0(Position.xz);
    float wave = (fastSin(delta) + fastSin(delta + 1.57079632)) * noise;
    position.x += (fastSin(delta + 9.43) + fastSin(delta + 1.57079632)) * noise0(Position.yz) / 2;
    position.y += wave / 2;
    position.z += (fastSin(delta - 9.43) + fastSin(delta + 0.942477796)) * noise0(Position.xy) / 2;

    gl_Position = ProjMat * ModelViewMat * vec4(position + ChunkOffset, 1.0);
    texCoord0 = UV0;
    vertexColor = minecraft_mix_light(Light0_Direction, Light1_Direction, Normal, Color);
    vertexColor.rgb *= wave;
}
