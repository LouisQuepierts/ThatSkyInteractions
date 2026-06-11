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
        final var relation      = attachment.getRelation();

        if (relation.isFollowing()) {
            final var left          = relation.getLeft();
            final var right         = relation.getRight();
            final var resolver      = attachment.getResolver();

            if (left != null) {
                resolver.follow(left, player, true);
            } else if (right != null) {
                resolver.follow(right, player, false);
            }
        }
    }

    private static void follow(
            final @NonNull  FollowerPositionResolver    resolver,
            final @NonNull  Player                      leader,
            final @NonNull  Player                      follower,
            final           boolean                     left
    ) {
        // calculate pos by leader's body rotation


        /*follower                .setYBodyRot(yRot);
        final var delta         = Mth.wrapDegrees(follower.getYRot() - yRot);
        final var tDelta        = Mth.clamp(delta, -42.0F, 42.0F);
        follower.yRotO          += tDelta - delta;
        follower                .setYRot(follower.getYRot() + tDelta - delta);
        follower                .setYHeadRot(follower.getYRot());*/

    }

}
