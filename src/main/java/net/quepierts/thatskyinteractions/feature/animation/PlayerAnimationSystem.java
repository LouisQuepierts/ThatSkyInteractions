package net.quepierts.thatskyinteractions.feature.animation;

import lombok.experimental.UtilityClass;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.feature.entity.AvatarExtension;
import net.quepierts.thatskyinteractions.feature.network.PlayAnimationPacket;
import org.jspecify.annotations.NonNull;

@UtilityClass
@EventBusSubscriber(modid = ThatSkyInteractions.MODID)
public class PlayerAnimationSystem {

    public static void play(
            @NonNull ServerPlayer   player,
            @NonNull Identifier     animation
    ) {

        PacketDistributor.sendToPlayersInDimension(
                player.level(),
                new PlayAnimationPacket(
                        animation,
                        player.getId()
                )
        );

    }

    @SubscribeEvent
    public static void onEntityTick(final EntityTickEvent.Pre event) {
        final var entity = event.getEntity();
        if (entity instanceof AvatarExtension extension) {
            final var state = extension.a4j$GetAnimationState();
            state.tick(entity.tickCount);
        }
    }

}
