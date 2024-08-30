#version 150

uniform sampler2D ScreenSampler;
uniform sampler2D DiffuseSampler;

in vec2 texCoord;
out vec4 fragColor;

void main() {
    vec3 textureColor = texture(ScreenSampler, texCoord).rgb;
    vec4 blurColor = texture(DiffuseSampler, texCoord);

    vec3 result = textureColor + blurColor.rgb * 1.2;
    fragColor = vec4(result, 1.0);
}
