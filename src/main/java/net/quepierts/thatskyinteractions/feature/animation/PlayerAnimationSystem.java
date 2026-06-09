package net.quepierts.thatskyinteractions.feature.animation;

import lombok.experimental.UtilityClass;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Avatar;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.feature.animation.packet.ClientboundAnimationControlPacket;
import net.quepierts.thatskyinteractions.feature.animation.packet.AnimationSignalPacket;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

@UtilityClass
@EventBusSubscriber(modid = ThatSkyInteractions.MODID)
public class PlayerAnimationSystem {

    public static PlayerAnimationAttachment getAnimationData(final @NonNull Avatar entity) {
        return PlayerAnimationAttachment.getAttachment(entity);
    }

    public static void play(
            @NonNull Avatar         avatar,
            @NonNull Identifier     animation
    ) {

        PacketDistributor.sendToPlayersTrackingEntityAndSelf(
                avatar,
                ClientboundAnimationControlPacket.play(avatar, animation)
        );

    }

    public static void play(
            @NonNull Avatar         avatar,
            @NonNull Identifier     animation,
            @Nullable Identifier     layer
    ) {

        if (layer == null) {
            play(avatar, animation);
            return;
        }

        PacketDistributor.sendToPlayersTrackingEntityAndSelf(
                avatar,
                ClientboundAnimationControlPacket.play(avatar, animation, layer)
        );

    }

    public static void abort(
            @NonNull Avatar         avatar
    ) {

        PacketDistributor.sendToPlayersTrackingEntityAndSelf(
                avatar,
                ClientboundAnimationControlPacket.abort(avatar)
        );

    }

    public static void abort(
            @NonNull Avatar         avatar,
            @Nullable Identifier     layer
    ) {

        if (layer == null) {
            abort(avatar);
            return;
        }

        PacketDistributor.sendToPlayersTrackingEntityAndSelf(
                avatar,
                ClientboundAnimationControlPacket.abort(avatar, layer)
        );

    }

    public static void exit(
            @NonNull Avatar         avatar
    ) {

        PacketDistributor.sendToPlayersTrackingEntityAndSelf(
                avatar,
                ClientboundAnimationControlPacket.exit(avatar)
        );

    }

    public static void exit(
            @NonNull Avatar         avatar,
            @Nullable Identifier     layer
    ) {

        if (layer == null) {
            exit(avatar);
            return;
        }

        PacketDistributor.sendToPlayersTrackingEntityAndSelf(
                avatar,
                ClientboundAnimationControlPacket.exit(avatar, layer)
        );

    }

    public static void pause(
            @NonNull Avatar         avatar
    ) {

        PacketDistributor.sendToPlayersTrackingEntityAndSelf(
                avatar,
                ClientboundAnimationControlPacket.pause(avatar)
        );

    }

    public static void pause(
            @NonNull Avatar         avatar,
            @Nullable Identifier     layer
    ) {

        if (layer == null) {
            pause(avatar);
            return;
        }

        PacketDistributor.sendToPlayersTrackingEntityAndSelf(
                avatar,
                ClientboundAnimationControlPacket.pause(avatar, layer)
        );

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
            @NonNull Avatar         avatar,
            @Nullable Identifier     layer
    ) {

        if (layer == null) {
            resume(avatar);
            return;
        }

        PacketDistributor.sendToPlayersTrackingEntityAndSelf(
                avatar,
                ClientboundAnimationControlPacket.resume(avatar, layer)
        );

    }

    public static void event(
            @NonNull Avatar         avatar,
            @NonNull String         event,
            @Nullable Identifier     layer
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

    }

    @SubscribeEvent
    public static void onPlayerTick(final EntityTickEvent.Pre event) {
        final var entity        = event.getEntity();

        if (!(entity instanceof Avatar avatar)) {
            return;
        }

        final var data          = PlayerAnimationSystem.getAnimationData(avatar);

        data.getController()    .tick(entity.tickCount);
    }

}
