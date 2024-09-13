#version 150

uniform vec4 ColorModulator;
uniform vec4 FogColor;
uniform sampler2D Sampler0;

in vec4 vertexColor;
in vec2 texCoord;

out vec4 fragColor;

void main() {
    vec4 color = texture(Sampler0, texCoord) * vertexColor * ColorModulator;

    if (color.a == 0) {
        discard;
    }

    fragColor = color;
}
