#version 150

in vec4 vertexColor;
in vec2 texCoord0;

uniform vec4 ColorModulator;
uniform float Intensity;

out vec4 fragColor;

vec3 halo(vec2 uv, vec4 color, vec4 params)
{
    float strength = params.x;
    float angleOffset = params.y;

    float r = length(uv);

    // --- radial masks ---
    float inner = smoothstep(0.0, 1.0, 1.0 - r);
    float outer = smoothstep(0.0, 1.0, (r - 1.0) * 9.0);

    // --- angle sparkle ---
    float angle = atan(uv.y, uv.x) + angleOffset;
    float sector = mod(angle, 0.03490658); // ~2 degrees

    float sparkleMask = step(sector, 0.02 / r);

    // --- strength shaping ---
    float strength3 = strength * strength * strength;

    float sparkleMix = sparkleMask * 0.15 + 0.85;

    float epsilon = mix(
            0.01,
            0.0001,
            strength3 * sparkleMix * (outer * 0.5 + 0.5)
    );

    // --- ring highlight ---
    float ringOffset = r - 0.9;

    float radialTerm =
    (inner * inner * inner) * 0.5 +
    outer * (1.0 / (ringOffset * ringOffset));

    // --- cross highlight ---
    float crossX = 1.0 + (0.06 / (uv.x * uv.x + epsilon));
    float crossY = 1.0 + (0.3  / (uv.y * uv.y + epsilon));

    float intensity =
    0.1 * radialTerm * crossX * crossY;

    intensity = clamp(intensity, 0.0, 100.0);

    // --- color shaping ---
    float edge = abs(dot(uv, uv) - 1.0) * 1.4492757;
    edge = min(edge, 3.0);

    vec3 edgeVec = vec3(edge);

    vec3 tint = (vec3(-0.25, -0.5, -0.75) + edgeVec) * 3.0;

    vec3 colorMask =
    mix(
            max(1.0 - tint * tint, 0.0),
            vec3(1.0),
            edgeVec
    );

    // --- final ---
    float glow = intensity * intensity * 0.1;

    return vec3(color.rgb * colorMask * glow);
}

void main() {
    vec2 uv = texCoord0 * 8.0 - 4.0;

    float strength = 0.68;
    vec4 iColor = vec4(0.06);
    vec4 iParams = vec4(strength, 0.0, 0.0, 0.0);

    vec3 color = halo(uv, iColor, iParams);

    vec3 remap = color / (1.0 + color);
    color = mix(remap, color, 0.22) * 0.5;

    float luma = dot(color, vec3(0.299, 0.587, 0.114));
    color = mix(vec3(luma), color, 0.36) * 0.5;

    fragColor = vec4(color, 0.0);
}
