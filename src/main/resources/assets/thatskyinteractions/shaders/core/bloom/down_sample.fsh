#version 150

uniform sampler2D CurrentSampler;
uniform sampler2D NoiseSampler;

uniform vec2 Resolution;
uniform int FrameIndex;

in vec2 texCoord;
out vec4 fragColor;

#define PI 3.14159265358
#define E 2.718281828459
#define R 1

float gaussianWeight(float x, float y, float sigma) {
    float sigma2 = sigma * sigma;
    return (1.0 / sqrt(2.0 * PI * sigma2)) * pow(E, -((x * x + y * y) / (2.0 * sigma2)));
}

vec4 sampleGaussian(sampler2D tex, vec2 texCoord, vec2 stride) {
    vec4 color = vec4(0.0);
    float weight = 0.0;
    for (int x = -R; x <= R; x++) {
        for (int y = -R; y <= R; y++) {
            float w = gaussianWeight(x, y, 1.0);
            vec2 uv = texCoord + vec2(x, y) * stride;
            color += texture(tex, uv) * w;
            weight += w;
        }
    }
    color /= weight;
    return color;
}

void main() {
    vec2 noiseUV = mod(gl_FragCoord.xy + vec2(FrameIndex * 17), 128.0) / 128.0;
    float noise = texture(NoiseSampler, noiseUV).r;

    vec2 offset = vec2(noise - 0.5) * Resolution;
    vec2 uv = texCoord /*+ offset*/;

    fragColor = sampleGaussian(CurrentSampler, uv, Resolution);
}