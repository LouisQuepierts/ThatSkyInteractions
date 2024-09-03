#version 150

uniform sampler2D DiffuseSampler;

in vec2 texCoord;
in vec2 sampleStep;

out vec4 fragColor;

void main() {
    vec4 blurred = texture(DiffuseSampler, texCoord);
    vec2 step = sampleStep * 5.43;

    blurred = max(texture(DiffuseSampler, texCoord + step), blurred);
    blurred = max(texture(DiffuseSampler, texCoord + step * 5.2), blurred);
    blurred = max(texture(DiffuseSampler, texCoord - step), blurred);
    blurred = max(texture(DiffuseSampler, texCoord - step * 5.2), blurred);

    // Adding the final texture with half weight as per the original code
    //blurred.a += texture(DiffuseSampler, texCoord + sampleStep * actualRadius) / 2.0;

    fragColor = blurred;
}