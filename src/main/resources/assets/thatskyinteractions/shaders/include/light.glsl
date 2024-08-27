#version 150

float simple_light(vec2 pos, float intensity) {
    return (intensity + 0.2) / 9 * dot(pos, pos) - 0.2;
}