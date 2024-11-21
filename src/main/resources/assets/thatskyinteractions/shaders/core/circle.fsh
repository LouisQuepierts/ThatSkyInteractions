#version 150

in vec4 vertexColor;
in vec2 texCoord0;

uniform vec4 ColorModulator;

out vec4 fragColor;

void main() {
    vec2 p = texCoord0 - vec2(0.5);
    float leng = dot(p, p);
    float alpha = step(leng, 0.25);
    vec4 color = vertexColor;
    color.a *= alpha;
    if (color.a == 0.0) {
        discard;
    }
    fragColor = color * ColorModulator;
}
