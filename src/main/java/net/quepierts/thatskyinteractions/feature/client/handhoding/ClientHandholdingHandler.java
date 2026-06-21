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
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.feature.client.control.event.LocalPlayerMovedEvent;

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

    public static void afterAiStep(Player follower) {

        /*final var attachment    = ClientHandholdingSystem.getLocalAttachment();
        final var relation      = attachment.getRelation();
        if (!relation.isFollowing()) {
            return;
        }

        final var leader        = relation.getLeader();

        final var tYRot         = leader.yBodyRot;
        final var dRot          = Mth.wrapDegrees(follower.yBodyRot - tYRot);

        follower.setYRot(follower.getYRot() - dRot);
        follower.setYHeadRot(follower.getYHeadRot() - dRot);
        follower.yBodyRot -= dRot;*/

    }

    @SuppressWarnings("DataFlowIssue")
    public static void onCameraAlign(final float partialTicks) {
        final var attachment    = ClientHandholdingSystem.getLocalAttachment();
        final var relation      = attachment.getRelation();

        final var local         = Minecraft.getInstance().player;

        if (!relation.isFollowing()) {
            return;
        }

        if (relation.getLeft() != null) {
            attachment.getResolver().clampRotation(relation.getLeft(), local, partialTicks);
        } else if (relation.getRight() != null) {
            attachment.getResolver().clampRotation(relation.getRight(), local, partialTicks);
        }
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
