#version 150

uniform sampler2D DiffuseSampler;

in vec2 texCoord;
in vec2 sampleStep;

uniform float Radius;
uniform float RadiusMultiplier;

out vec4 fragColor;
const float weight[] = float[] (0.0896631113333857,
    0.0874493212267511,
    0.0811305381519717,
    0.0715974486241365,
    0.0601029809166942,
    0.0479932050577658,
    0.0364543006660986,
    0.0263392293891488,
    0.0181026699707781,
    0.0118349786570722,
    0.0073599963704157,
    0.0043538453346397,
    0.0024499299678342
);

void main() {
    vec4 result = texture(DiffuseSampler, texCoord) * weight[0];
    vec2 textureOffset = 1.0 / vec2(textureSize(DiffuseSampler, 0));

    for (int i = 1; i < weight.length(); ++i) {
        result += texture(DiffuseSampler, texCoord + vec2(textureOffset.x * i, 0.0)) * weight[i];
        result += texture(DiffuseSampler, texCoord - vec2(textureOffset.x * i, 0.0)) * weight[i];
    }

    fragColor = result;
}
