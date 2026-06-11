package net.quepierts.thatskyinteractions.feature.animation.fk;

import net.quepierts.thatskyinteractions.feature.client.model.MinecraftModelAdaptor;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.jspecify.annotations.NonNull;

public final class FKController {

    private final FKTarget[]    targets;
    private final Matrix4f      toLocal;

    public FKController() {
        final var values = FKTargetType.values();
        this.targets        = new FKTarget[values.length];
        this.toLocal        = new Matrix4f();

        for (var i = 0; i < values.length; i++) {
            this.targets[i] = FKTarget.of(values[i]);
        }
    }

    public void setup(
            final @NonNull Matrix4f toLocal
    ) {
        this.toLocal.set(toLocal).invert();
    }

    public void setTarget(
            final @NonNull FKTargetType     type,
            final @NonNull FKTargetSupplier supplier
    ) {
        final var fk = this.get(type);
        fk.setSupplier(supplier);
    }

    public void clearTarget(
            final @NonNull FKTargetType     type
    ) {
        final var fk = this.get(type);
        fk.setSupplier(null);
    }

    public void setConfiguration(
            final @NonNull FKTargetType type,
            final          float        weight,
            final          boolean      active
    ) {
        final var fk = this.get(type);
        fk.setWeight(weight);
        fk.setActive(active);
    }

    public void update(
            final float partialTick
    ) {
        for (final var target : this.targets) {

            if (!target.isActive()) {
                continue;
            }

            final var supplier = target.getSupplier();
            if (supplier != null) {
                supplier.get(target.getTarget(), partialTick);
            }
        }
    }

    public void apply(
            final @NonNull MinecraftModelAdaptor adaptor
    ) {

        final var skeleton = adaptor.getSkeleton();

        for (final var target : this.targets) {

            final var weight    = target.getWeight();
            if (!target.isActive()
                    || weight == 0.0f) {
                continue;
            }

            final var type      = target.getType();
            final var bone      = type.getBone();


            final var view      = skeleton.get(bone.getMapped() - 1);
            final var part      = view.part();

            final var local     = this.toLocal
                                .transformPosition(target.getTarget(), new Vector3f());


            final var point     = new Vector3f(
                                        part.x,
                                        part.y + 8,
                                        part.z
                                );

            final var direction = local.sub(point);
            final var from = new Vector3f(0, 1, 0);
            final var quat = new Quaternionf().rotationTo(from, direction);

            view.setQuaternion(quat.x(), quat.y(), quat.z(), quat.w(), weight);

        }
    }

    private @NonNull FKTarget get(
            @NonNull final FKTargetType  type
    ) {
        return this.targets[type.ordinal()];
    }
}
