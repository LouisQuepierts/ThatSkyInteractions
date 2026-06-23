package net.quepierts.thatskyinteractions.feature.client.bond;

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
public class ClientBondHandler {

    @SubscribeEvent
    public static void onLocalPlayerMove(final LocalPlayerMovedEvent event) {

        if (!ClientBondSystem.isFollowing()) {
            return;
        }

        if (!event.getInput().sprint()) {
            event.getPlayer().sendOverlayMessage(Component.translatable("message.thatskyinteractions.handhold.stop"));
            event.setCanceled(true);
            return;
        }

        ClientBondSystem.unhold();

    }

    @SubscribeEvent
    @SuppressWarnings("DataFlowIssue")
    public static void onPlayerClick(final InputEvent.InteractionKeyMappingTriggered event) {

        final var minecraft = Minecraft.getInstance();
        if (!minecraft.hasAltDown()) {
            return;
        }

        final var attachment    = ClientBondSystem.getLocalAttachment();
        final var relation      = attachment.getHandhold();

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

            ClientBondSystem.unhold(onMain);
        } else if (event.isUseItem() && onOff != null) {
            event.setSwingHand(true);
            event.setCanceled(true);

            ClientBondSystem.unhold(onOff);
        }

    }

    @SuppressWarnings("DataFlowIssue")
    public static void onCameraAlign(final float partialTicks) {
        final var attachment    = ClientBondSystem.getLocalAttachment();
        final var relation      = attachment.getHandhold();

        final var local         = Minecraft.getInstance().player;

        if (!relation.isFollowing()) {
            return;
        }

        attachment              .getResolver()
                                .clampRotation(
                                        relation.getLeft() != null
                                                ? relation.getLeft()
                                                : relation.getRight(),
                                        local,
                                        partialTicks
                                );
    }

}
