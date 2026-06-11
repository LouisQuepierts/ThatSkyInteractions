package net.quepierts.thatskyinteractions.feature.animation.fk;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.joml.Quaternionf;
import org.joml.Vector3f;

@Getter
@Setter
@RequiredArgsConstructor(staticName = "of")
public final class FKTarget {

    private final   Vector3f            target  = new Vector3f();
    private final   FKTargetType        type;

    private         FKTargetSupplier    supplier;

    private         float               weight;
    private         boolean             active;

}
