#version                150

#define RT_RECT         0
#define RT_CIRCLE       1
#define RT_ARC          2
#define RT_SECTOR       3
#define RT_PIE          4

#define PASS_FILL       0
#define PASS_STROKE     1
#define PASS_LIGHT      2

uniform int             uRenderType;
uniform int             uPassType;

uniform vec4            uSharedParams;
uniform vec4            uShapeParams;

in      vec2            vPosition;
in      vec4            vColor;

out     vec4            fragColor;

#define uSmoothRadius   uSharedParams.x
#define uLightRadius    uSharedParams.x
#define uStrokeWidth    uSharedParams.y
#define uCornerRadius   uSharedParams.z

// from https://iquilezles.org/articles/distfunctions2d/
float sdRect( in vec2 p, in vec2 b ) {
    vec2 d = abs(p)-b;
    return length(max(d,0.0)) + min(max(d.x,d.y),0.0);
}

// from https://iquilezles.org/articles/distfunctions2d/
float sdRoundRect( in vec2 p, in vec2 b, in float r ) {
    vec2 q = abs(p)-b+vec2(r);
    return min(max(q.x,q.y),0.0) + length(max(q,0.0)) - r;
}

// from https://iquilezles.org/articles/distfunctions2d/
float sdCircle(vec2 p, float r) {
    return length(p) - r;
}

// from https://iquilezles.org/articles/distfunctions2d/
float sdArc(in vec2 p, in vec2 sc, in float ra, in float rb) {
    // sc is the sin/cos of the arc's aperture
    p.x = abs(p.x);
    return ((sc.y*p.x>sc.x*p.y) ? length(p-sc*ra) :
    abs(length(p)-ra)) - rb;
}

// from https://iquilezles.org/articles/distfunctions2d/
float sdRing(in vec2 p, in vec2 n, in float r, in float th) {
    p.x = abs(p.x);
    p = mat2x2(n.x,n.y,-n.y,n.x)*p;
    return max( abs(length(p)-r)-th*0.5,
            length(vec2(p.x,max(0.0,abs(r-p.y)-th*0.5)))*sign(p.x) );
}

// from https://iquilezles.org/articles/distfunctions2d/
float sdPie(in vec2 p, in vec2 c, in float r) {
    p.x = abs(p.x);
    float l = length(p) - r;
    float m = length(p-c*clamp(dot(p,c),0.0,r)); // c=sin/cos of aperture
    return max(l,m*sign(c.y*p.x-c.x*p.y));
}

float sdf(vec2 p) {
    switch      (uRenderType) {
        case    RT_RECT:
        return  sdRect(p, uShapeParams.xy - vec2(uCornerRadius)) - uCornerRadius;
        case    RT_CIRCLE:
        return  sdCircle(p, uShapeParams.x);
        case    RT_ARC:
        return  sdArc(p, uShapeParams.xy, uShapeParams.z, uShapeParams.w) - uCornerRadius;
        case    RT_SECTOR:
        return  sdRing(p, uShapeParams.xy, uShapeParams.z, uShapeParams.w) - uCornerRadius;
        case    RT_PIE:
        return  sdPie(p, uShapeParams.xy, uShapeParams.z);
    }

    return      1e5;
}

void main() {
    vec2    p               = vPosition;
    float   d               = sdf(p);

    float   alpha;

    if (uPassType == PASS_FILL) {
        float   aa          = max(fwidth(d) * 0.5, uSmoothRadius);
        alpha               = smoothstep(0.0 + aa, 0.0 - aa, d);
    }
    else if (uPassType == PASS_STROKE) {
        float   aa          = max(fwidth(d) * 0.5, uSmoothRadius);
        alpha               = smoothstep(0.0 + aa, 0.0 - aa, d) *
                              smoothstep(0.0 - aa, 0.0 + aa, d + uStrokeWidth);
    }
    else if (uPassType == PASS_LIGHT) {
        float k             = 4.605 / uLightRadius;
        alpha               = exp(-abs(d) * k);
    }

    vec4    color           = vColor;
    color.a                 *= alpha;
    fragColor               = color;
}
