package net.quepierts.thatskyinteractions.feature.handhold;

import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.NonNull;

public final class FollowerPositionResolver {

    public boolean locked = false;

    public double x;
    public double y;
    public double z;

    public int counter = -1;

    public void follow(
            final @NonNull Player   leader,
            final @NonNull Player   follower,
            final boolean           left
    ) {

        final var current       = follower.position();
        final var position      = PlayerHandholdingSystem.computeHandholdPosition(leader, left);

        final var distance      = Math.sqrt(current.distanceToSqr(position));
        if (distance < 0.01) {
            this.counter        = 0;
            this.locked         = true;
        }

        if (this.counter > 60) {
            // force update

            follower            .setPos(position);
            this                .update(position);
            this.counter        = -1;
            return;
        } else if (this.counter > 10) {
            this.locked = false;
        }

        var dx                  = position.x - current.x;
        var dy                  = position.y - current.y;
        var dz                  = position.z - current.z;

        if (!this.locked) {
            final var length = Math.sqrt(dx * dx + dy * dy + dz * dz);
            final var speed = Math.min(Math.max(5 - distance, 0) * 0.05 + 0.5, 1.0f);
            if (length > speed) {
                final var factor = speed / length;
                dx *= factor;
                dy *= factor;
                dz *= factor;
            }
        }

        final var delta         = new Vec3(dx, dy, dz);

        follower.setDeltaMovement(0, 0, 0);
        follower.move(MoverType.SELF, delta);

        if (this.counter < 0 || distance > 4 && follower.distanceToSqr(this.x, this.y, this.z) < 0.1) {
            // seems stuck somewhere
            this.counter ++;
        }
        this.update(follower.position());

    }

    private void update(final @NonNull Vec3 position) {
        this.x = position.x;
        this.y = position.y;
        this.z = position.z;
    }

    public void reset() {
        this.locked = false;
        this.counter = -1;
    }
}
