#version 150

in vec4 vertexColor;
in vec2 texCoord0;

uniform vec4 ColorModulator;
uniform float Width;

out vec4 fragColor;

void main() {
    vec2 p = (texCoord0 - vec2(0.5)) * 1.5f;
    float leng = length(p);
    float inv = leng - 0.5f + Width / 2;
    float glow = -8 * inv * inv + 0.5f;
    float alpha = max(step(leng, 0.5) * step(0.5 - Width, leng), glow);
    vec4 color = vertexColor;
    color.a *= alpha;
    if (color.a == 0.0) {
        discard;
    }
    fragColor = color * ColorModulator;
}
