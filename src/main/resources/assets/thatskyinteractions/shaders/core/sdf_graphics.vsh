#version            150

uniform mat4            uModelMatrix;
uniform mat4            ProjMat;
uniform vec4            ColorModulator;

uniform vec4            uRect;
uniform vec4            uSharedParams;

in      vec3            Position;
in      vec2            UV0;
in      vec4            Color;

out     vec2            vPosition;
out     vec4            vColor;

#define uPosition       uRect.xy
#define uSize           uRect.zw

#define uSmoothRadius   uSharedParams.x

void main() {
    gl_Position = ProjMat * uModelMatrix * vec4(Position.xy, 0.0, 1.0);

    vPosition   = Position.xy * uSize;
    vColor      = Color * ColorModulator;
}