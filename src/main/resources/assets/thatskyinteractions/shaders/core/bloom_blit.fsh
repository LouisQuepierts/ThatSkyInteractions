#version 150

uniform sampler2D ScreenSampler;
uniform sampler2D DiffuseSampler;
uniform float Blend;

in vec2 texCoord;
out vec4 fragColor;

void main() {
    vec3 textureColor = texture(ScreenSampler, texCoord).rgb;
    vec4 blurColor = texture(DiffuseSampler, texCoord);

    vec3 result = mix(textureColor + blurColor.rgb * Blend, blurColor.rgb * Blend, blurColor.a);
    fragColor = vec4(result, 1.0);
}
