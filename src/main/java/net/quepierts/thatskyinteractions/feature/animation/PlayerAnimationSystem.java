package net.quepierts.thatskyinteractions.feature.animation;

import lombok.experimental.UtilityClass;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Avatar;
import net.minecraft.world.entity.Entity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.feature.animation.packet.AnimationControlPacket;
import net.quepierts.thatskyinteractions.feature.registry.AttachmentTypes;
import org.jspecify.annotations.NonNull;

@UtilityClass
@EventBusSubscriber(modid = ThatSkyInteractions.MODID)
public class PlayerAnimationSystem {

    public static PlayerAnimationData getAnimationData(final @NonNull Entity entity) {
        return entity.getData(AttachmentTypes.PLAYER_ANIMATION);
    }

    public static void play(
            @NonNull ServerPlayer   player,
            @NonNull Identifier     animation
    ) {

        PacketDistributor.sendToPlayersInDimension(
                player.level(),
                AnimationControlPacket.play(player, animation)
        );

    }

    public static void abort(
            @NonNull ServerPlayer   player
    ) {

        PacketDistributor.sendToPlayersInDimension(
                player.level(),
                AnimationControlPacket.abort(player)
        );

    }

    public static void exit(
            @NonNull ServerPlayer   player
    ) {

        PacketDistributor.sendToPlayersInDimension(
                player.level(),
                AnimationControlPacket.exit(player)
        );

    }

    public static void event(
            @NonNull ServerPlayer   player,
            Identifier              event
    ) {

        PacketDistributor.sendToPlayersInDimension(
                player.level(),
                AnimationControlPacket.event(player, event)
        );

    }

    @SubscribeEvent
    public static void onPlayerTick(final EntityTickEvent.Pre event) {
        final var entity    = event.getEntity();

        if (!(entity instanceof Avatar)) {
            return;
        }

        final var data      = PlayerAnimationSystem.getAnimationData(entity);

        data.getAnimation() .tick(entity.tickCount);
    }

}
