#version 150

uniform sampler2D DiffuseSampler;

in vec2 texCoord;
out vec4 fragColor;

float near = 0.1;
float far  = 128.0;

float LinearizeDepth(float depth) {
    float z = depth * 2.0 - 1.0; // back to NDC
    return (2.0 * near * far) / (far + near - z * (far - near));
}

void main() {
    float depth = LinearizeDepth(texture(DiffuseSampler, texCoord).r) / far;
    fragColor = vec4(vec3(depth), 1.0);
}
