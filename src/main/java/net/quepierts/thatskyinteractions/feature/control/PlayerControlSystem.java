package net.quepierts.thatskyinteractions.feature.control;

import lombok.experimental.UtilityClass;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.quepierts.thatskyinteractions.feature.control.packet.AlignBodyPacket;
import org.jspecify.annotations.NonNull;

@UtilityClass
public class PlayerControlSystem {

    public static void align(final @NonNull ServerPlayer player) {

        PacketDistributor.sendToPlayersTrackingEntityAndSelf(
                player,
                AlignBodyPacket.of(player)
        );

    }

}
