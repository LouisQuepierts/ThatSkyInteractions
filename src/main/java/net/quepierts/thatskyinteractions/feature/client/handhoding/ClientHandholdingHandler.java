package net.quepierts.thatskyinteractions.feature.client.handhoding;

import lombok.experimental.UtilityClass;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.feature.control.event.EntityTurnEvent;
import net.quepierts.thatskyinteractions.feature.handhold.PlayerHandholdingAttachment;
import net.quepierts.thatskyinteractions.feature.handhold.PlayerHandholdingSystem;

@UtilityClass
@EventBusSubscriber(value = Dist.CLIENT, modid = ThatSkyInteractions.MODID)
public class ClientHandholdingHandler {

    /*@SubscribeEvent
    public static void beforeEntityTurn(final EntityTurnEvent.Pre event) {

        final var local         = Minecraft.getInstance().player;
        if (event.getEntity()   != local) {
            return;
        }

        final var attachment = ClientHandholdingSystem.getLocalAttachment();
        if (attachment.isFollowing()) {

            final var follower      = local;
            final var leader        = attachment.getLeader();
            final var yRot          = leader.yBodyRot;

            follower                .setYBodyRot(yRot);
            final var delta         = Mth.wrapDegrees(follower.getYRot() - yRot);
            final var tDelta        = Mth.clamp(delta, -42.0F, 42.0F);
            follower.yRotO          += tDelta - delta;
            follower                .setYRot(follower.getYRot() + tDelta - delta);
            follower                .setYHeadRot(follower.getYRot());

        }

    }*/

    public static void afterAiStep(Player follower) {

        final var attachment    = ClientHandholdingSystem.getLocalAttachment();
        if (!attachment.isFollowing()) {
            return;
        }

        final var deadZone      = 3;

        final var leader        = attachment.getLeader();
        final var left          = attachment.getLeft() == leader;

        /*final var current       = follower.position();
        final var position      = PlayerHandholdingSystem.computeHandholdPosition(leader, left);

        final var diff = position.subtract(current);

        double distXZ = Math.sqrt(diff.x * diff.x + diff.z * diff.z);
        double distY  = Math.abs(diff.y);

        double newX = current.x;
        double newZ = current.z;

        if (distXZ > 1.0) {
            double excess = distXZ - 1.0;
            double strength = Mth.clamp(excess * excess * 0.1, 0.05, 0.5);

            newX = Mth.lerp(strength, current.x, position.x);
            newZ = Mth.lerp(strength, current.z, position.z);
        }

        double newY = current.y;

        if (!follower.onGround() || distY > 1.2) {
            double excessY = distY - 1.2;
            double strengthY = Mth.clamp(excessY * 0.1, 0.02, 0.2);

            newY = Mth.lerp(strengthY, current.y, position.y);
        }

        follower.setPos(newX, newY, newZ);*/


        final var tYRot         = leader.getYRot();
        final var delta         = Mth.wrapDegrees(follower.getYRot() - tYRot);
        final var clamped       = Mth.clamp(delta, -42.0F, 42.0F);

        final var correction    = clamped - delta;

        follower.yRotO          += correction;

        follower.setYRot(follower.getYRot() + correction);
        follower.setYHeadRot(follower.getYRot());

        follower.yBodyRot       = leader.yBodyRot;
    }

//    @SubscribeEvent
    public static void onClientTick(final ClientTickEvent.Pre event) {

        final var minecraft     = Minecraft.getInstance();
        final var player        = minecraft.player;

        if (player == null) {
            return;
        }

        final var attachment    = ClientHandholdingSystem.getLocalAttachment();
        if (!attachment.isFollowing()) {
            return;
        }

        final var follower      = player;
        final var leader        = attachment.getLeader();
        final var yRot          = leader.yBodyRot;

        follower                .setYBodyRot(yRot);
        final var delta         = Mth.wrapDegrees(follower.getYRot() - yRot);
        final var tDelta        = Mth.clamp(delta, -42.0F, 42.0F);
        follower.yRotO          += tDelta - delta;
        follower                .setYRot(follower.getYRot() + tDelta - delta);
        follower                .setYHeadRot(follower.getYRot());
    }

}
