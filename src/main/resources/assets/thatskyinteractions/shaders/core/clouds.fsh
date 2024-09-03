#version 150

uniform vec4 ColorModulator;
uniform vec4 FogColor;

in vec4 vertexColor;

out vec4 fragColor;

void main() {
    vec4 color = vertexColor * ColorModulator;
    fragColor = vec4(mix(color.rgb, FogColor.rgb * ColorModulator.rgb, 0.8), color.a);
}
