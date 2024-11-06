#version 150

in vec4 vertexColor;
in vec2 texCoord0;

uniform vec4 ColorModulator;
uniform float Intensity;

out vec4 fragColor;

void main() {
    vec2 p = abs(texCoord0 - vec2(0.5));
    float iIntensity = (Intensity + 0.35) / (1 + 20 * dot(p, p)) - 0.3;

    vec4 color = vertexColor * iIntensity;
    color.rgb += iIntensity * 0.3;
    fragColor = color * ColorModulator;
}
