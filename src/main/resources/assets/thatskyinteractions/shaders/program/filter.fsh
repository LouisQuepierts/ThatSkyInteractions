#version 150

uniform sampler2D DiffuseSampler;

in vec2 texCoord;
in vec2 sampleStep;
out vec4 fragColor;

void main() {
    vec4 texColor = texture(DiffuseSampler, texCoord);
    float brightness = dot(texColor.xyz, vec3(0.2126, 0.7152, 0.0722));
    fragColor = texColor * step(0.92, brightness);
}
