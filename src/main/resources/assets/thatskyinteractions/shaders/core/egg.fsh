#version 150

uniform vec4 ColorModulator;
uniform float Smooth;

uniform vec2 Radians;
uniform vec3 Radii;

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
    ) - EdgeRadius;
}

float sdEgg(vec2 p, float ra, float rb){
    const float k = sqrt(3.0);
    p.x = abs(p.x);
    float r = ra - rb;
    return ((p.y<0.0)       ? length(vec2(p.x,  p.y    )) - r :
    (k*(p.x+r)<p.y) ? length(vec2(p.x,  p.y-k*r)) :
    length(vec2(p.x+r,p.y    )) - 2.0*r) - rb;
}

void main() {
    vec2 v = texCoord0 - 0.5;
    float sweep = SweepRadian * 0.5;
    vec2 p = rotate(v, 1.570796326 - MiddleRadian);
    float dist = sdRing(p, vec2(cos(sweep), sin(sweep)), RingRadius, SectorRadius);

    vec4 color = vertexColor * ColorModulator;
    color.a *= smoothstep(0, Smooth, -dist);

    fragColor = color;
}