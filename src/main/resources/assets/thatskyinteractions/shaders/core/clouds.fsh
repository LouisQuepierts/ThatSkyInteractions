#version 150

uniform vec4 ColorModulator;
uniform vec4 FogColor;

in vec4 vertexColor;
in float vertexWave;

out vec4 fragColor;

void main() {
    vec4 color = vertexColor * ColorModulator;
    vec3 fog = mix(color.rgb, FogColor.rgb * ColorModulator.rgb, 0.8);
    float mask = step(1, color.a);
    vec3 result = color.rgb * mask + fog * (1 - mask);
    fragColor = vec4(result * (vertexWave / 4 + 0.75), 1.0);
}
