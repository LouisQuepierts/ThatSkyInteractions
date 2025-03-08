#version 150

uniform vec4 ColorModulator;

uniform vec2 Rect;
uniform float Radius;
uniform float Smooth;

in vec4 vertexColor;
in vec2 texCoord0;

out vec4 fragColor;

float sdRing(in vec2 p, in vec2 n, in float r, float th) {
    p.x = abs(p.x);

    p = mat2x2(n.x,n.y,-n.y,n.x)*p;

    return max( abs(length(p)-r)-th*0.5,
    length(vec2(p.x,max(0.0,abs(r-p.y)-th*0.5)))*sign(p.x) );
}

void main() {
    vec2 pt = (texCoord0 * 2 - 1) * Rect;

    vec2 b = max(Rect - Radius, vec2(0.0));
    vec2 d = abs(pt) - b;
    float dist = min(max(d.x,d.y),0.0) + length(max(d,0.0)) - Radius;

    vec4 color = vertexColor * ColorModulator;
    color.a *= smoothstep(0, Smooth, -dist);

    if (color.a == 0.0) {
        discard;
    }

    fragColor = color;
}
