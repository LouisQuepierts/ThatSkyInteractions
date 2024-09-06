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
    vec2 step = sampleStep * 4.32;
    blurred += texture(DiffuseSampler, texCoord + step * 1) * weight[1];
    blurred += texture(DiffuseSampler, texCoord - step * 1) * weight[1];

    blurred += texture(DiffuseSampler, texCoord + step * 2) * weight[2];
    blurred += texture(DiffuseSampler, texCoord - step * 2) * weight[2];

    blurred += texture(DiffuseSampler, texCoord + step * 3) * weight[3];
    blurred += texture(DiffuseSampler, texCoord - step * 3) * weight[3];

    blurred += texture(DiffuseSampler, texCoord + step * 4) * weight[4];
    blurred += texture(DiffuseSampler, texCoord - step * 4) * weight[4];

    blurred += texture(DiffuseSampler, texCoord + step * 5) * weight[5];
    blurred += texture(DiffuseSampler, texCoord - step * 5) * weight[5];

    blurred += texture(DiffuseSampler, texCoord + step * 6) * weight[6];
    blurred += texture(DiffuseSampler, texCoord - step * 6) * weight[6];

    blurred += texture(DiffuseSampler, texCoord + step * 7) * weight[7];
    blurred += texture(DiffuseSampler, texCoord - step * 7) * weight[7];

    blurred += texture(DiffuseSampler, texCoord + step * 8) * weight[8];
    blurred += texture(DiffuseSampler, texCoord - step * 8) * weight[8];

    blurred += texture(DiffuseSampler, texCoord + step * 9) * weight[9];
    blurred += texture(DiffuseSampler, texCoord - step * 9) * weight[9];

    blurred += texture(DiffuseSampler, texCoord + step * 10) * weight[10];
    blurred += texture(DiffuseSampler, texCoord - step * 10) * weight[10];

    blurred += texture(DiffuseSampler, texCoord + step * 11) * weight[11];
    blurred += texture(DiffuseSampler, texCoord - step * 11) * weight[11];

    blurred += texture(DiffuseSampler, texCoord + step * 12) * weight[12];
    blurred += texture(DiffuseSampler, texCoord - step * 12) * weight[12];

    // Adding the final texture with half weight as per the original code
    //blurred += texture(DiffuseSampler, texCoord + sampleStep * actualRadius) / 2.0;

    fragColor = blurred * 1.1f;
}