package net.quepierts.thatskyinteractions.infra.animation.backend.uniform;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public enum UniformType {
    INT(1, 1),
    BOOL(1, 1),
    FLOAT(1, 1),

    VEC2(2, 4),
    VEC3(3, 4),
    VEC4(4, 4),

    IVEC2(2, 4),
    IVEC3(3, 4),
    IVEC4(4, 4),

    MAT2(4, 4),
    MAT3(9, 16),
    MAT4(16, 16);

    private final int size;
    private final int align;
}
