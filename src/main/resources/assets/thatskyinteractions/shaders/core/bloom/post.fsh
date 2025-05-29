#version 150

uniform sampler2D DiffuseSampler;
uniform sampler2D ScreenSampler;
uniform sampler2D BaseSampler;

in vec2 texCoord;
out vec4 fragColor;

void main() {
    vec4 bloom = texture(DiffuseSampler, texCoord);
    vec4 screen = texture(ScreenSampler, texCoord);
    vec4 base = texture(BaseSampler, texCoord);

    vec3 hdr = clamp(bloom.rgb, 0.0, 1.0);
    vec3 src = mix(screen.rgb, base.rgb, base.a);

    vec3 result = src + hdr - src * hdr;
    fragColor = vec4(result, 1.0);
}