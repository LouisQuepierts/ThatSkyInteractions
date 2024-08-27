#version 150

in vec4 vertexColor;
in vec2 texCoord0;

uniform vec4 ColorModulator;
uniform float Intensity;
uniform float Width;

out vec4 fragColor;

void main() {
    vec2 p = abs(texCoord0 - vec2(0.5));
    vec2 x = vec2(p.x, p.y / Width);
    vec2 y = vec2(p.x / Width, p.y);
    float iIntensity = (Intensity + 0.35) / (1 + 20 * dot(p, p)) - 0.3;
    float xIntensity = (Intensity + 0.5) / (1 + 8 * dot(x, x)) - 0.4;
    float yIntensity = (Intensity + 0.5) / (1 + 8 * dot(y, y)) - 0.4;

    vec2 rp = vec2(p.x - p.y, p.x + p.y) * 0.70710677; // sin 45 deg
    vec2 rx = vec2(rp.x, rp.y / Width);
    vec2 ry = vec2(rp.x / Width, rp.y);
    float rxIntensity = (Intensity + 0.5) / (1 + 8 * dot(rx, rx)) - 0.4;
    float ryIntensity = (Intensity + 0.5) / (1 + 8 * dot(ry, ry)) - 0.4;

    vec4 color = vertexColor * max(max(max(xIntensity, yIntensity), max(rxIntensity, ryIntensity)), iIntensity);
    color.rgb += iIntensity * 0.3;
    fragColor = color * ColorModulator;
}
