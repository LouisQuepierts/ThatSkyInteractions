package net.quepierts.thatskyinteractions.feature.handhold;

import lombok.experimental.UtilityClass;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityLeaveLevelEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import org.jspecify.annotations.NonNull;

@UtilityClass
@EventBusSubscriber(modid = ThatSkyInteractions.MODID)
public class PlayerHandholdingHandler {

    private static final Vec3       LEFT    = new Vec3(-1.0, 0.0, -0.2);
    private static final Vec3       RIGHT   = new Vec3(1.0, 0.0, -0.2);

    @SubscribeEvent
    public static void onPlayerLeave(final EntityLeaveLevelEvent event) {
        final var entity = event.getEntity();
        if (!(entity instanceof ServerPlayer player)) {
            return;
        }

        PlayerHandholdingSystem.unholdAll(player);

    }

    @SubscribeEvent
    public static void onPlayerTick(final PlayerTickEvent.Pre event) {
        final var player        = event.getEntity();

        final var attachment    = PlayerHandholdingAttachment.getAttachment(player);

        if (attachment.isFollowing()) {
            final var left          = attachment.getLeft();
            final var right         = attachment.getRight();

            if (left != null) {
                PlayerHandholdingHandler.follow(left, player, true);
            } else if (right != null) {
                PlayerHandholdingHandler.follow(right, player, false);
            }
        }
    }

    private static void follow(
            final @NonNull  Player      leader,
            final @NonNull  Player      follower,
            final           boolean     left
    ) {
        // calculate pos by leader's body rotation
        final var current       = follower.position();
        final var other         = leader.position();

        final var diff          = current.subtract(other);
        final var position      = PlayerHandholdingSystem.computeHandholdPosition(leader, left);

        var dx                  = position.x - current.x;
        var dy                  = position.y - current.y;
        var dz                  = position.z - current.z;

        follower.setDeltaMovement(0, 0, 0);

        final var d2 = dx * dx + dz * dz;
        if (d2 > 12) {
            // set position
            follower.setPos(position.x, position.y, position.z);
            return;
        }

        final var delta = new Vec3(dx, dy, dz);
        follower.move(MoverType.SELF, delta);

        /*follower                .setYBodyRot(yRot);
        final var delta         = Mth.wrapDegrees(follower.getYRot() - yRot);
        final var tDelta        = Mth.clamp(delta, -42.0F, 42.0F);
        follower.yRotO          += tDelta - delta;
        follower                .setYRot(follower.getYRot() + tDelta - delta);
        follower                .setYHeadRot(follower.getYRot());*/

    }

}
