package net.quepierts.thatskyinteractions.feature.client;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Avatar;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import net.neoforged.neoforge.event.entity.EntityLeaveLevelEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.feature.animation.tween.PhysicalTweenAttachment;
import net.quepierts.thatskyinteractions.feature.client.gui.ScreenLoader;
import net.quepierts.thatskyinteractions.feature.client.gui.screen.FriendshipScreen;
import net.quepierts.thatskyinteractions.feature.client.reference.TsiKeys;
import net.quepierts.thatskyinteractions.feature.friendship.FriendshipTreeData;
import net.quepierts.thatskyinteractions.feature.friendship.PlayerFriendshipAttachment;
import net.quepierts.thatskyinteractions.feature.friendship.PlayerFriendshipSystem;
import net.quepierts.thatskyinteractions.feature.friendship.packet.PlayerFriendshipRequestPacket;
import net.quepierts.thatskyinteractions.feature.gui.handler.ClientFriendshipUiHandler;
import net.quepierts.thatskyinteractions.feature.interaction.event.PlayerInteractionEvent;
import net.quepierts.thatskyinteractions.feature.registry.InteractionTypes;

@UtilityClass
@SuppressWarnings({"unused", "DataFlowIssue"})
public class ClientPlayerFriendshipSystem {

    public static PlayerFriendshipAttachment getLocalFriendshipData() {
        return PlayerFriendshipAttachment.getAttachment(Minecraft.getInstance().player);
    }

    public static void unlockFriendshipNode(
            final @NonNull FriendshipTreeData data,
            final int node
    ) {

        if (PlayerFriendshipSystem.isFriendshipConditional() && !data.isUnlockable(node)) {
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

    public static void acceptUnlock(
            final Player requester
    ) {

        ClientPacketDistributor.sendToServer(
                PlayerFriendshipRequestPacket.accept(
                        requester.getUUID()
                )
        );

    }

    public static void interactFriendshipNode(
            final @NonNull FriendshipTreeData data,
            final int node
    ) {
        final var minecraft = Minecraft.getInstance();

        if (PlayerFriendshipSystem.isFriendshipConditional() && !data.isUnlocked(node)) {
            return;
        }

        final var local = minecraft.player;
        final var other = data.getOther(local.getUUID());

        ClientPacketDistributor.sendToServer(
                PlayerFriendshipRequestPacket.interact(
                        other,
                        node
                )
        );
    }

    @UtilityClass
    @EventBusSubscriber(value = Dist.CLIENT, modid = ThatSkyInteractions.MODID)
    static final class Handler {

        @SubscribeEvent
        public static void onInteractPlayer(final PlayerInteractEvent.EntityInteract event) {

            final var player        = event.getEntity();
            if (!player.isLocalPlayer()) {
                return;
            }

            if (!player.getMainHandItem().isEmpty() || !player.getOffhandItem().isEmpty()) {
                return;
            }

            if (!TsiKeys.KEY_INTERACT.isDown()) {
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

        @SubscribeEvent
        public static void onPlayerLeaveEvent(final EntityLeaveLevelEvent event) {

            if (!(event.getEntity() instanceof Player other)) {
                return;
            }

            ClientFriendshipUiHandler.cancel(other);

        }

        @SubscribeEvent
        public static void onUnlockAccepted(final PlayerInteractionEvent.Accept.Post event) {

            // just for in case
            if (!event.isClient()) {
                return;
            }

            if (!event.getInteraction().is(InteractionTypes.UNLOCK)) {
                return;
            }

            final var minecraft = Minecraft.getInstance();
            if (minecraft.screen instanceof FriendshipScreen screen) {
                minecraft.popGuiLayer();

                final var model = screen.getController().getModel();
                final var tween = PhysicalTweenAttachment.tween(Minecraft.getInstance().level);
                tween.wait(
                        () -> ScreenLoader.open(FriendshipScreen.class, model),
                        0.5f
                );
            }

        }

        @SubscribeEvent
        public static void onPlayerLeaved(final EntityLeaveLevelEvent event) {
            if (!(event.getEntity() instanceof Player player)) {
                return;
            }

            final var uuid = player.getUUID();
            final var minecraft = Minecraft.getInstance();
            if (minecraft.screen instanceof FriendshipScreen screen) {
                final var local = minecraft.player;
                final var other = screen.getController().getModel().getOther(uuid);

                if (other.equals(local.getUUID())) {
                    minecraft.popGuiLayer();
                }
            }
        }

    }

}
