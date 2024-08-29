#version 150

in vec4 vertexColor;
in vec2 texCoord0;

uniform vec4 ColorModulator;
uniform float Intensity;

out vec4 fragColor;

void main() {
    vec2 p = abs(texCoord0 - vec2(0.5));
    float distSqr = dot(p, p);

    vec2 ep = abs(step(0.5, texCoord0.xy) - texCoord0.xy) * 0.943;
    ep.x *= 0.89;
    float edist = dot(ep, ep);
    float innerSqr = 0.04;
    float innerEdge = smoothstep(innerSqr - 0.01, innerSqr + 0.005, distSqr);
    float core =  (Intensity + 0.35) / (1.2 + 94.3 * max(0, 0.38 - edist)) - 0.3;
    float expand = (Intensity + 0.35) / (1.2 + 94.3 * max(0, 0.25 - edist)) - 0.3;
    vec4 color = vertexColor;

    color.a *= max(innerEdge * expand, core);
    if (color.a == 0.0) {
        discard;
    }
    fragColor = color * ColorModulator;
}
