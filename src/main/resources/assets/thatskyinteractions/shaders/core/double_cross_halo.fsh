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

    vec2 rp = vec2(p.x - p.y, p.x + p.y) * 0.70710677; // sin 45 deg
    vec2 rx = vec2(rp.x, rp.y / 0.1);
    vec2 ry = vec2(rp.x / 0.1, rp.y);
    float rxIntensity = 1.0 - smoothstep(0.0, 0.25, dot(rx, rx));
    float ryIntensity = 1.0 - smoothstep(0.0, 0.25, dot(ry, ry));

    vec4 color = vertexColor * max(max(max(xIntensity, yIntensity), max(rxIntensity, ryIntensity)), iIntensity) * Intensity;
    if (color.a == 0.0) {
        discard;
    }
    color.rgb += iIntensity * Intensity;
    fragColor = color * ColorModulator;
}
