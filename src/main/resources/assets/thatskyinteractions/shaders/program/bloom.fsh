/**
#version 150

uniform sampler2D DiffuseSampler;

in vec2 texCoord;
in vec2 sampleStep;

out vec4 fragColor;

const float weight[] = float[] (
    0.0896631113333857,
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
    vec4 blurred = vec4(0.0);
    float actualRadius = round(Radius * RadiusMultiplier);

    for (int i = 1; i < weight.length(); ++i) {
        blurred += texture(DiffuseSampler, texCoord + sampleStep * i) * weight[i];
        blurred += texture(DiffuseSampler, texCoord - sampleStep * i) * weight[i];
    }

    blurred += texture(DiffuseSampler, texCoord + sampleStep * actualRadius) / 2.0;
    fragColor = blurred;
}
*/

#version 150

uniform sampler2D DiffuseSampler;

in vec2 texCoord;
in vec2 sampleStep;

out vec4 fragColor;

const float weight[] = float[] (
    0.0896631113333857,
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
    vec4 blurred = vec4(0.0);
    vec2 step = sampleStep * 9.43;

    for (int i = 1; i < 13; i++) {
        blurred += texture(DiffuseSampler, texCoord + step * i) * weight[i];
        blurred += texture(DiffuseSampler, texCoord - step * i) * weight[i];
    }

    fragColor = blurred;
}