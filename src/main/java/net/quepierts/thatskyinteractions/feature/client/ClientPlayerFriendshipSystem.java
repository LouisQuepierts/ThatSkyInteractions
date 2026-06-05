package net.quepierts.thatskyinteractions.feature.client;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Avatar;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.feature.animation.tween.PhysicalTweenAttachment;
import net.quepierts.thatskyinteractions.feature.client.gui.ScreenLoader;
import net.quepierts.thatskyinteractions.feature.client.gui.screen.FriendshipScreen;
import net.quepierts.thatskyinteractions.feature.client.input.TSIKeys;
import net.quepierts.thatskyinteractions.feature.friendship.FriendshipTreeData;
import net.quepierts.thatskyinteractions.feature.friendship.PlayerFriendshipAttachment;
import net.quepierts.thatskyinteractions.feature.friendship.packet.PlayerFriendshipRequestPacket;

@UtilityClass
@SuppressWarnings({"unused", "DataFlowIssue"})
@EventBusSubscriber(value = Dist.CLIENT, modid = ThatSkyInteractions.MODID)
public class ClientPlayerFriendshipSystem {

    public static PlayerFriendshipAttachment getLocalFriendshipData() {
        return PlayerFriendshipAttachment.getAttachment(Minecraft.getInstance().player);
    }

    public static void unlockFriendshipNode(
            final @NonNull FriendshipTreeData data,
            final int node
    ) {

        if (!data.isUnlockable(node)) {
            return;
        }

        final var local = Minecraft.getInstance().player;
        final var other = data.getOther(local.getUUID());

        ClientPacketDistributor.sendToServer(
                PlayerFriendshipRequestPacket.unlock(
                        other,
                        node
                )
        );
    }

    public static void interactFriendshipNode(
            final @NonNull FriendshipTreeData data,
            final int node
    ) {
        if (!data.isUnlocked(node)) {
            return;
        }

        final var local = Minecraft.getInstance().player;
        final var other = data.getOther(local.getUUID());

        ClientPacketDistributor.sendToServer(
                PlayerFriendshipRequestPacket.interact(
                        other,
                        node
                )
        );
    }

    @SubscribeEvent
    public static void onInteractPlayer(final PlayerInteractEvent.EntityInteract event) {

        final var player        = event.getEntity();
        if (!player.isLocalPlayer()) {
            return;
        }

        if (!player.getMainHandItem().isEmpty() || !player.getOffhandItem().isEmpty()) {
            return;
        }

        if (!TSIKeys.KEY_INTERACT.isDown()) {
            return;
        }

        final var target        = event.getTarget();
        if (!(target instanceof Avatar other)) {
            return;
        }

        final var attachment    = PlayerFriendshipAttachment.getAttachment(player);
        final var data          = attachment.get(other.getUUID(), PlayerFriendshipAttachment.FRIEND);

        final var tween         = PhysicalTweenAttachment.tween(player.level());
        tween                   .wait(
                                    () -> {
                                        if (Minecraft.getInstance().screen == null) {
                                            ScreenLoader.open(FriendshipScreen.class, data);
                                        }
                                    },
                                    0.2f
                                );

        event                   .setCancellationResult(InteractionResult.SUCCESS);
        event                   .setCanceled(true);
    }

}
