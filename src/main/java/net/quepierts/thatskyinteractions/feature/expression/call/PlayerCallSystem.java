package net.quepierts.thatskyinteractions.feature.expression.call;

import lombok.experimental.UtilityClass;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.block.NoteBlock;
import net.quepierts.thatskyinteractions.feature.expression.PlayerPreferenceAttachment;
import net.quepierts.thatskyinteractions.feature.particle.CallParticleOption;
import org.jspecify.annotations.NonNull;

@UtilityClass
public class PlayerCallSystem {

    public static void call(
            final @NonNull  ServerPlayer    player
    ) {

        final var attachment    = PlayerPreferenceAttachment.getAttachment(player);
        final var voiceType     = attachment.getVoiceType();

        if (!voiceType.available()) {
            return;
        }

        final var level         = player.level();
        final var note          = voiceType.getNote(player.getRandom());
        final var pitch         = NoteBlock.getPitchFromNote(note);

        level.playSound(
                null,
                player.getX(),
                player.getY(),
                player.getZ(),
                voiceType.getSound().value(),
                SoundSource.PLAYERS,
                1.0f,
                pitch
        );
        level.sendParticles(
                CallParticleOption.of(player),
                player.getX(),
                player.getY() + player.getEyeHeight(),
                player.getZ(),
                1,
                0.0f,
                0.0f,
                0.0f,
                1.0f
        );

    }

}
