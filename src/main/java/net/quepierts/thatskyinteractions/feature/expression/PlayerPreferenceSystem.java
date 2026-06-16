package net.quepierts.thatskyinteractions.feature.expression;

import lombok.experimental.UtilityClass;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.quepierts.thatskyinteractions.feature.expression.call.packet.ChangeVoicePacket;
import org.jspecify.annotations.NonNull;

@UtilityClass
public class PlayerPreferenceSystem {

    public static void setVoice(
            final @NonNull ServerPlayer player,
            final @NonNull Identifier   voice
    ) {
        PlayerPreferenceAttachment.getAttachment(player).setVoice(voice);
        PacketDistributor.sendToPlayer(
                player,
                new ChangeVoicePacket(voice)
        );
    }

    public static @NonNull Identifier getVoice(
            final @NonNull ServerPlayer player
    ) {
        return PlayerPreferenceAttachment.getAttachment(player).getVoice();
    }

}
