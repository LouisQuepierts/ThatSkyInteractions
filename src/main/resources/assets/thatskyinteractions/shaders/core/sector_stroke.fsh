#version 150

uniform vec4 ColorModulator;

uniform float Smooth;

uniform vec2 Radians;
uniform vec3 Radii;

uniform float Radius;
uniform float Width;
uniform float Stroke;

#define SweepRadian Radians.x
#define MiddleRadian Radians.y
#define RingRadius Radii.x
#define SectorRadius Radii.y
#define EdgeRadius Radii.z

in vec4 vertexColor;
in vec2 texCoord0;

out vec4 fragColor;

vec2 rotate(vec2 p, float a) {
    float s=sin(a);
    float c=cos(a);
    return mat2(c,s,-s,c)*p;
}

// from https://iquilezles.org/articles/distfunctions2d/
float sdRing(vec2 p, vec2 n, float r, float th) {
    p.x = abs(p.x);

    p = mat2x2(n.x,n.y,-n.y,n.x)*p;

    float tt = - th * 0.5;
    return max(
        abs(length(p)-r) + tt,
        length(vec2(p.x,max(0.0,abs(r-p.y) + tt)))*sign(p.x)
    ) - EdgeRadius * 0.5;
}

void main() {
    vec2 v = texCoord0 - 0.5;
    float sweep = SweepRadian * 0.5;
    vec2 p = rotate(v, 1.570796326 - MiddleRadian);
    float dist = sdRing(p, vec2(cos(sweep), sin(sweep)), RingRadius, SectorRadius);
    dist = abs(dist) - Stroke;

    vec4 color = vertexColor * ColorModulator;
    color.a *= smoothstep(0, Smooth, -dist);

    fragColor = color;
}