package net.quepierts.thatskyinteractions.feature.animation;

import lombok.experimental.UtilityClass;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
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
import net.quepierts.thatskyinteractions.feature.registry.TsiEntityTags;
import net.quepierts.thatskyinteractions.feature.registry.TsiRegistries;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

@UtilityClass
@EventBusSubscriber(modid = ThatSkyInteractions.MODID)
public class PlayerAnimationSystem {

    public static final PlayerMask LOWER_BODY
            = PlayerMask.of(/*PlayerBone.ROOT, */PlayerBone.LEFT_LEG, PlayerBone.RIGHT_LEG)
                        .toImmutable();

    public static PlayerAnimationAttachment getAnimationData(final @NonNull LivingEntity entity) {
        return PlayerAnimationAttachment.getAttachment(entity);
    }

    public static @Nullable PlayerAnimationAttachment getExistingAttachment(final @NonNull Entity entity) {
        return PlayerAnimationAttachment.getExistingAttachment(entity);
    }

    public static void play(
            final @NonNull  LivingEntity    avatar,
            final @NonNull  Identifier      animation
    ) {

        if (PlayerAnimationSystem.isNotAnimatable(avatar)) {
            return;
        }

        PacketDistributor.sendToPlayersTrackingEntityAndSelf(
                avatar,
                ClientboundAnimationControlPacket.play(avatar, animation)
        );

        final var attachment = PlayerAnimationSystem.getAnimationData(avatar);
        attachment.getController().play(animation);
    }

    public static void play(
            final @NonNull  LivingEntity    avatar,
            final @NonNull  Identifier      animation,
            final @Nullable Identifier      layer
    ) {

        if (layer == null) {
            play(avatar, animation);
            return;
        }

        if (PlayerAnimationSystem.isNotAnimatable(avatar)) {
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
            final @NonNull LivingEntity     avatar
    ) {

        final var attachment = PlayerAnimationSystem.getExistingAttachment(avatar);

        if (attachment == null) {
            return;
        }

        PacketDistributor.sendToPlayersTrackingEntityAndSelf(
                avatar,
                ClientboundAnimationControlPacket.abort(avatar)
        );

        attachment.getController().abort();

    }

    public static void abort(
            final @NonNull  LivingEntity    avatar,
            final @Nullable Identifier      layer
    ) {

        if (layer == null) {
            abort(avatar);
            return;
        }

        final var attachment = PlayerAnimationSystem.getExistingAttachment(avatar);

        if (attachment == null) {
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

        attachment.getController().abort(layerType);

    }

    public static void exit(
            final @NonNull LivingEntity     avatar
    ) {

        final var attachment = PlayerAnimationSystem.getExistingAttachment(avatar);

        if (attachment == null) {
            return;
        }

        PacketDistributor.sendToPlayersTrackingEntityAndSelf(
                avatar,
                ClientboundAnimationControlPacket.exit(avatar)
        );

        attachment.getController().exit();

    }

    public static void exit(
            final @NonNull  LivingEntity    avatar,
            final @Nullable Identifier      layer
    ) {

        if (layer == null) {
            exit(avatar);
            return;
        }

        final var attachment = PlayerAnimationSystem.getExistingAttachment(avatar);

        if (attachment == null) {
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

        attachment.getController().exit(layerType);

    }

    public static void pause(
            final @NonNull LivingEntity     avatar
    ) {

        final var attachment = PlayerAnimationSystem.getExistingAttachment(avatar);

        if (attachment == null) {
            return;
        }

        PacketDistributor.sendToPlayersTrackingEntityAndSelf(
                avatar,
                ClientboundAnimationControlPacket.pause(avatar)
        );

        attachment.getController().pause();

    }

    public static void pause(
            final @NonNull  LivingEntity    avatar,
            final @Nullable Identifier      layer
    ) {
        if (layer == null) {
            pause(avatar);
            return;
        }

        final var attachment = PlayerAnimationSystem.getExistingAttachment(avatar);

        if (attachment == null) {
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

        attachment.getController().pause(layerType);

    }

    public static void resume(
            final @NonNull LivingEntity     avatar
    ) {
        final var attachment = PlayerAnimationSystem.getExistingAttachment(avatar);

        if (attachment == null) {
            return;
        }

        PacketDistributor.sendToPlayersTrackingEntityAndSelf(
                avatar,
                ClientboundAnimationControlPacket.resume(avatar)
        );

        attachment.getController().resume();

    }

    public static void resume(
            final @NonNull  LivingEntity    avatar,
            final @Nullable Identifier      layer
    ) {

        if (layer == null) {
            resume(avatar);
            return;
        }

        final var attachment = PlayerAnimationSystem.getExistingAttachment(avatar);

        if (attachment == null) {
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

        attachment.getController().resume(layerType);

    }

    public static void event(
            final @NonNull  LivingEntity    avatar,
            final @NonNull  String          event,
            final @Nullable Identifier      layer
    ) {

        if (layer == null) {
            event(avatar, event);
            return;
        }

        final var attachment = PlayerAnimationSystem.getExistingAttachment(avatar);

        if (attachment == null) {
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

        attachment.getController().event(event);

    }

    public static void event(
            @NonNull LivingEntity   avatar,
            @NonNull String         event
    ) {
        final var attachment = PlayerAnimationSystem.getExistingAttachment(avatar);

        if (attachment == null) {
            return;
        }

        PacketDistributor.sendToPlayersTrackingEntityAndSelf(
                avatar,
                ClientboundAnimationControlPacket.event(
                        avatar,
                        event
                )
        );

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
        final var attachment    = PlayerAnimationSystem.getExistingAttachment(entity);

        if (attachment == null) {
            return;
        }

        final var controller    = attachment.getController();
        final var sitting       = entity.getVehicle() != null;
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

        LivingEntity entity;
        if ((entity = PlayerAnimationSystem.tryParseAnimatable(event.getEntity())) != null) {

            PacketDistributor.sendToPlayer(
                    player,
                    ClientboundSyncAnimationControllerPacket.of(entity)
            );

        }
    }

    public static boolean isAnimatable(final @NonNull LivingEntity entity) {
        return entity.is(TsiEntityTags.ANIMATABLE_HUMANOID);
    }

    public static boolean isNotAnimatable(final @NonNull LivingEntity entity) {
        return !entity.is(TsiEntityTags.ANIMATABLE_HUMANOID);
    }

    public static @Nullable LivingEntity tryParseAnimatable(final @Nullable Entity entity) {
        return (entity instanceof LivingEntity living && isAnimatable(living)) ? living : null;
    }
}
