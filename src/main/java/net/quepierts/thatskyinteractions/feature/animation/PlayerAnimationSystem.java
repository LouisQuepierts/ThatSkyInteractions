package net.quepierts.thatskyinteractions.feature.animation;

import lombok.experimental.UtilityClass;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Avatar;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.core.animation.model.PlayerBone;
import net.quepierts.thatskyinteractions.core.animation.model.PlayerMask;
import net.quepierts.thatskyinteractions.feature.animation.packet.ClientboundAnimationControlPacket;
import net.quepierts.thatskyinteractions.feature.animation.packet.AnimationSignalPacket;
import net.quepierts.thatskyinteractions.feature.animation.packet.ClientboundSyncAnimationControllerPacket;
import net.quepierts.thatskyinteractions.feature.registry.TsiRegistries;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

@UtilityClass
@EventBusSubscriber(modid = ThatSkyInteractions.MODID)
public class PlayerAnimationSystem {

    public static final PlayerMask LOWER_BODY
            = PlayerMask.of(/*PlayerBone.ROOT, */PlayerBone.LEFT_LEG, PlayerBone.RIGHT_LEG)
                        .toImmutable();

    public static PlayerAnimationAttachment getAnimationData(final @NonNull Avatar entity) {
        return PlayerAnimationAttachment.getAttachment(entity);
    }

    public static void play(
            final @NonNull  Avatar          avatar,
            final @NonNull  Identifier      animation
    ) {

        PacketDistributor.sendToPlayersTrackingEntityAndSelf(
                avatar,
                ClientboundAnimationControlPacket.play(avatar, animation)
        );

        final var attachment = PlayerAnimationSystem.getAnimationData(avatar);
        attachment.getController().play(animation);
    }

    public static void play(
            final @NonNull  Avatar          avatar,
            final @NonNull  Identifier      animation,
            final @Nullable Identifier      layer
    ) {

        if (layer == null) {
            play(avatar, animation);
            return;
        }

        final var layerType    = TsiRegistries.ANIMATION_LAYER_TYPE
                                .getOptional(layer)
                                .orElse(null);

        if (layerType == null) {
            return;
        }

        PacketDistributor.sendToPlayersTrackingEntityAndSelf(
                avatar,
                ClientboundAnimationControlPacket.play(avatar, animation, layer)
        );

        final var attachment = PlayerAnimationSystem.getAnimationData(avatar);
        attachment.getController().play(animation, layerType);

    }

    public static void abort(
            final @NonNull  Avatar          avatar
    ) {

        PacketDistributor.sendToPlayersTrackingEntityAndSelf(
                avatar,
                ClientboundAnimationControlPacket.abort(avatar)
        );

        final var attachment = PlayerAnimationSystem.getAnimationData(avatar);
        attachment.getController().abort();

    }

    public static void abort(
            final @NonNull  Avatar          avatar,
            final @Nullable Identifier      layer
    ) {

        if (layer == null) {
            abort(avatar);
            return;
        }

        final var layerType    = TsiRegistries.ANIMATION_LAYER_TYPE
                .getOptional(layer)
                .orElse(null);

        if (layerType == null) {
            return;
        }

        PacketDistributor.sendToPlayersTrackingEntityAndSelf(
                avatar,
                ClientboundAnimationControlPacket.abort(avatar, layer)
        );

        final var attachment = PlayerAnimationSystem.getAnimationData(avatar);
        attachment.getController().abort(layerType);

    }

    public static void exit(
            final @NonNull Avatar           avatar
    ) {

        PacketDistributor.sendToPlayersTrackingEntityAndSelf(
                avatar,
                ClientboundAnimationControlPacket.exit(avatar)
        );

        final var attachment = PlayerAnimationSystem.getAnimationData(avatar);
        attachment.getController().exit();

    }

    public static void exit(
            final @NonNull  Avatar          avatar,
            final @Nullable Identifier      layer
    ) {

        if (layer == null) {
            exit(avatar);
            return;
        }

        final var layerType     = TsiRegistries.ANIMATION_LAYER_TYPE
                                .getOptional(layer)
                                .orElse(null);

        if (layerType == null) {
            return;
        }

        PacketDistributor.sendToPlayersTrackingEntityAndSelf(
                avatar,
                ClientboundAnimationControlPacket.exit(avatar, layer)
        );

        final var attachment = PlayerAnimationSystem.getAnimationData(avatar);
        attachment.getController().exit(layerType);

    }

    public static void pause(
            final @NonNull  Avatar          avatar
    ) {

        PacketDistributor.sendToPlayersTrackingEntityAndSelf(
                avatar,
                ClientboundAnimationControlPacket.pause(avatar)
        );

        final var attachment = PlayerAnimationSystem.getAnimationData(avatar);
        attachment.getController().pause();

    }

    public static void pause(
            final @NonNull  Avatar          avatar,
            final @Nullable Identifier      layer
    ) {

        if (layer == null) {
            pause(avatar);
            return;
        }

        final var layerType     = TsiRegistries.ANIMATION_LAYER_TYPE
                                .getOptional(layer)
                                .orElse(null);

        if (layerType == null) {
            return;
        }

        PacketDistributor.sendToPlayersTrackingEntityAndSelf(
                avatar,
                ClientboundAnimationControlPacket.pause(avatar, layer)
        );

        final var attachment = PlayerAnimationSystem.getAnimationData(avatar);
        attachment.getController().pause(layerType);

    }

    public static void resume(
            @NonNull Avatar         avatar
    ) {

        PacketDistributor.sendToPlayersTrackingEntityAndSelf(
                avatar,
                ClientboundAnimationControlPacket.resume(avatar)
        );

    }

    public static void resume(
            final @NonNull  Avatar          avatar,
            final @Nullable Identifier      layer
    ) {

        if (layer == null) {
            resume(avatar);
            return;
        }

        final var layerType     = TsiRegistries.ANIMATION_LAYER_TYPE
                                .getOptional(layer)
                                .orElse(null);

        if (layerType == null) {
            return;
        }

        PacketDistributor.sendToPlayersTrackingEntityAndSelf(
                avatar,
                ClientboundAnimationControlPacket.resume(avatar, layer)
        );

        final var attachment = PlayerAnimationSystem.getAnimationData(avatar);
        attachment.getController().resume(layerType);

    }

    public static void event(
            final @NonNull  Avatar          avatar,
            final @NonNull  String          event,
            final @Nullable Identifier      layer
    ) {

        if (layer == null) {
            event(avatar, event);
            return;
        }

        PacketDistributor.sendToPlayersTrackingEntityAndSelf(
                avatar,
                ClientboundAnimationControlPacket.event(
                        avatar,
                        event,
                        layer
                )
        );

        final var attachment = PlayerAnimationSystem.getAnimationData(avatar);
        attachment.getController().event(event);

    }

    public static void event(
            @NonNull Avatar         avatar,
            @NonNull String         event
    ) {

        PacketDistributor.sendToPlayersTrackingEntityAndSelf(
                avatar,
                ClientboundAnimationControlPacket.event(
                        avatar,
                        event
                )
        );

        final var attachment = PlayerAnimationSystem.getAnimationData(avatar);
        attachment.getController().event(event);

    }

    public static void signal(
            @NonNull ServerPlayer   player,
            int                     signal
    ) {

        PacketDistributor.sendToPlayersInDimension(
                player.level(),
                AnimationSignalPacket.of(
                        player,
                        signal
                )
        );


        final var attachment = PlayerAnimationSystem.getAnimationData(player);
        attachment.getController().event(signal);

    }

    @SubscribeEvent
    public static void onPlayerTick(final EntityTickEvent.Pre event) {
        final var entity        = event.getEntity();

        if (!(entity instanceof Avatar avatar)) {
            return;
        }

        final var data          = PlayerAnimationSystem.getAnimationData(avatar);

        final var controller    = data.getController();
        final var sitting       = avatar.getVehicle() != null;
        final var executionMask = controller.getExecutionMask();

        if (sitting) {
            executionMask.not(LOWER_BODY);
        } else {
            executionMask.or(LOWER_BODY);
        }

        controller              .tick(entity.tickCount);
    }

    @SubscribeEvent
    public static void onPlayerStartTrack(final PlayerEvent.StartTracking event) {

        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        if (event.getTarget() instanceof Avatar avatar) {

            PacketDistributor.sendToPlayer(
                    player,
                    ClientboundSyncAnimationControllerPacket.of(avatar)
            );

        }
    }
}
