package net.quepierts.thatskyinteractions.client.shader;

import com.mojang.blaze3d.shaders.Uniform;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum GLUniformType {
    FLOAT(1, Uniform.UT_FLOAT1, false),
    INT(1, Uniform.UT_INT1, false),
    VEC2(2, Uniform.UT_FLOAT2, false),
    VEC3(3, Uniform.UT_FLOAT3, false),
    VEC4(4, Uniform.UT_FLOAT4, false),
    IVEC2(2, Uniform.UT_INT2, false),
    IVEC3(3, Uniform.UT_INT3, false),
    IVEC4(4,Uniform.UT_INT4, false),
    MAT2(4, Uniform.UT_MAT2, false),
    MAT3(9, Uniform.UT_INT3, false),
    MAT4(16, Uniform.UT_MAT4, false),
    SAMPLER2D(1, Uniform.UT_INT1, true),
    SAMPLER3D(1, Uniform.UT_INT1, true),
    IMAGE2D(1, Uniform.UT_INT1, true),
    IMAGE3D(1, Uniform.UT_INT1, true);

    private static final Map<String, GLUniformType> REFERENCE;
    private final int size;
    private final int type;

    private final boolean sampler;

    static {
        Map<String, GLUniformType> temp = new HashMap<>(GLUniformType.values().length);
        for (GLUniformType value : GLUniformType.values()) {
            temp.put(value.name().toLowerCase(Locale.ROOT), value);
        }
        REFERENCE = Collections.unmodifiableMap(temp);
    }

    public static GLUniformType parse(String type) {
        GLUniformType glType = REFERENCE.get(type);
        if (glType == null) {
            throw new RuntimeException("Unsupported glType: " + type);
        }

        return glType;
    }
}
