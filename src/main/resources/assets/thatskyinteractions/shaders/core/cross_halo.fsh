#version 150

in vec4 vertexColor;
in vec2 texCoord0;

uniform vec4 ColorModulator;
uniform float Intensity;

out vec4 fragColor;

void main() {
    vec2 p = abs(texCoord0 - vec2(0.5));
    vec2 x = vec2(p.x, p.y / 0.1);
    vec2 y = vec2(p.x / 0.1, p.y);
    float iIntensity = 0.9 - smoothstep(0.0, 0.05, dot(p, p)) * 0.9;
    float xIntensity = 1.0 - smoothstep(0.0, 0.25, dot(x, x));
    float yIntensity = 1.0 - smoothstep(0.0, 0.25, dot(y, y));

    vec4 color = vertexColor * max(max(xIntensity, yIntensity), iIntensity) * Intensity;
    if (color.a == 0.0) {
        discard;
    }
    color.rgb += iIntensity * Intensity;
    fragColor = color * ColorModulator;
}
