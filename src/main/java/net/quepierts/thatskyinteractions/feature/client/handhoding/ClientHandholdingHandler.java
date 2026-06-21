package net.quepierts.thatskyinteractions.feature.client.handhoding;

import lombok.experimental.UtilityClass;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.feature.animation.PlayerAnimationSystem;
import net.quepierts.thatskyinteractions.feature.animation.fk.FKTargetType;
import net.quepierts.thatskyinteractions.feature.client.ClientPlayerCallSystem;
import net.quepierts.thatskyinteractions.feature.client.control.event.LocalPlayerMovedEvent;
import net.quepierts.thatskyinteractions.feature.client.reference.TsiKeys;
import net.quepierts.thatskyinteractions.feature.control.event.EntityTurnEvent;
import net.quepierts.thatskyinteractions.feature.handhold.PlayerHandholdRelation;
import net.quepierts.thatskyinteractions.feature.handhold.PlayerHandholdingAttachment;
import net.quepierts.thatskyinteractions.feature.handhold.PlayerHandholdingSystem;
import org.joml.Vector3f;

@UtilityClass
@EventBusSubscriber(value = Dist.CLIENT, modid = ThatSkyInteractions.MODID)
public class ClientHandholdingHandler {

    @SubscribeEvent
    public static void onLocalPlayerMove(final LocalPlayerMovedEvent event) {

        if (!ClientHandholdingSystem.isFollowing()) {
            return;
        }

        if (!event.getInput().sprint()) {
            event.getPlayer().sendOverlayMessage(Component.translatable("message.thatskyinteractions.handhold.stop"));
            event.setCanceled(true);
            return;
        }

        ClientHandholdingSystem.unhold();

    }

    @SubscribeEvent
    @SuppressWarnings("DataFlowIssue")
    public static void onPlayerClick(final InputEvent.InteractionKeyMappingTriggered event) {

        final var minecraft = Minecraft.getInstance();
        if (!minecraft.hasAltDown()) {
            return;
        }

        final var attachment    = ClientHandholdingSystem.getLocalAttachment();
        final var relation      = attachment.getRelation();

        if (!relation.isHolding()) {
            return;
        }

        final var main          = minecraft.player.getMainArm();
        Player onMain, onOff;

        if (main == HumanoidArm.RIGHT) {
            onMain = relation.getRight();
            onOff  = relation.getLeft();
        } else {
            onMain = relation.getLeft();
            onOff  = relation.getRight();
        }

        if (event.isAttack() && onMain != null) {
            event.setSwingHand(true);
            event.setCanceled(true);

            ClientHandholdingSystem.unhold(onMain);
        } else if (event.isUseItem() && onOff != null) {
            event.setSwingHand(true);
            event.setCanceled(true);

            ClientHandholdingSystem.unhold(onOff);
        }

    }

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

    /*@SubscribeEvent
    public static void onPlayerTick(final PlayerTickEvent.Pre event) {

        final var player        = event.getEntity();
        final var attachment    = PlayerHandholdingSystem.getAttachment(player);
        final var controller    = PlayerAnimationSystem.getAnimationData(player).getController();

        final var playerPos     = player.position();
        final var left          = attachment.getLeft();
        if (left != null) {
            // update fk

            final var leftPos   = left.position();
            final var center    = new Vector3f(
                    (float) ((playerPos.x + leftPos.x) * 0.5),
                    (float) ((playerPos.y + leftPos.y) * 0.5) + 0.4f,
                    (float) ((playerPos.z + leftPos.z) * 0.5)
            );

            controller.getFkController().setTarget(
                    FKTargetType.LEFT_ARM,
                    center,
                    1.0f
            );
        }

        final var right         = attachment.getRight();
        if (right != null) {
            // update fk

            final var rightPos  = right.position();
            final var center    = new Vector3f(
                    (float) ((playerPos.x + rightPos.x) * 0.5),
                    (float) ((playerPos.y + rightPos.y) * 0.5) + 0.4f,
                    (float) ((playerPos.z + rightPos.z) * 0.5)
            );

            controller.getFkController().setTarget(
                    FKTargetType.RIGHT_ARM,
                    center,
                    1.0f
            );
        }

    }*/

    public static void afterAiStep(Player follower) {

        final var attachment    = ClientHandholdingSystem.getLocalAttachment();
        final var relation      = attachment.getRelation();
        if (!relation.isFollowing()) {
            return;
        }

        final var deadZone      = 3;

        final var leader        = relation.getLeader();
        final var left          = relation.getLeft() == leader;

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
        final var relation      = attachment.getRelation();
        if (!relation.isFollowing()) {
            return;
        }

        final var follower      = player;
        final var leader        = relation.getLeader();
        final var yRot          = leader.yBodyRot;

        follower                .setYBodyRot(yRot);
        final var delta         = Mth.wrapDegrees(follower.getYRot() - yRot);
        final var tDelta        = Mth.clamp(delta, -42.0F, 42.0F);
        follower.yRotO          += tDelta - delta;
        follower                .setYRot(follower.getYRot() + tDelta - delta);
        follower                .setYHeadRot(follower.getYRot());
    }

}
