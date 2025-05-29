#version 150

in vec4 vertexColor;
in vec2 texCoord0;

uniform vec4 ColorModulator;
uniform float Intensity;

out vec4 fragColor;

void dodge_blend(float base, float blend, float opacity, out float result) {
    result = base / (1.0 - clamp(blend, 0.000001, 0.999999));
    result = mix(base, result, opacity);
}

void hsva2rgba(vec4 hsv, out vec4 rgb) {
    vec4 K = vec4(1.0, 2.0 / 3.0, 1.0 / 3.0, 3.0);
    vec3 p = abs(fract(hsv.xxx + K.xyz) * 6.0 - K.www);
    rgb.rgb = hsv.z * mix(K.xxx, clamp(p - K.xxx, 0.0, 1.0), hsv.y);
    rgb.a = hsv.a;
}

float saturate(float c) {
    return clamp(c, 0, 1);
}

void main() {
    vec2 uv = texCoord0 * 2 - 1;
    float r = length(uv);

    // halo ring color
    float halo_ring = smoothstep(0.34, 0.5, r) * smoothstep(0.44, 0.35, r);
    float hue = clamp(smoothstep(0.37, 0.48, r), 0, 0.76);
    vec4 halo_hsva = vec4(hue, 1.72, 3.04, 1);
    vec4 halo_color;
    hsva2rgba(halo_hsva, halo_color);
    halo_color = mix(vec4(1), halo_color, halo_ring * 2.5);

    // base mask
    float base = (1 - saturate(r)) * 0.05;
    dodge_blend(base, halo_ring, 0.65, base);

    float inner_edge = smoothstep(0.29, 0.39, r);
    // y light
    float y_line = 1 - smoothstep(0, 0.38, abs(uv.y));
    float y_range = smoothstep(3.77 * Intensity, 0.1, r) * inner_edge;
    y_line = y_line * y_range;

    dodge_blend(base, y_line, y_range, base);

    // x light
    float x_line = 1 - smoothstep(0, 0.3, abs(uv.x));
    float x_range = smoothstep(1.75 * Intensity, 0.27, r) * inner_edge;
    x_line = x_line * x_range;

    dodge_blend(base, x_line, x_range, base);

    // core light
    float core_light = smoothstep(0.11, 0, r);
    dodge_blend(base, core_light, 0.5, base);

    // color mask
    vec4 color = ColorModulator * base;
    vec4 result = mix(halo_color, color, base * 0.13);
    result.a = base;
    
    fragColor = result;
}
