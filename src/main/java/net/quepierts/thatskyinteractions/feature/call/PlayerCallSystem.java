package net.quepierts.thatskyinteractions.feature.call;

import lombok.experimental.UtilityClass;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.block.NoteBlock;
import net.quepierts.thatskyinteractions.feature.particle.CallParticleOption;
import org.jspecify.annotations.NonNull;

@UtilityClass
public class PlayerCallSystem {

    //                                   C  D   E  G   A
    private static final int[] notes = { 6, 8, 10, 13, 15 };

    public static void call(
            final @NonNull  ServerPlayer    player,
            final           float           strength
    ) {

        final var level = player.level();
        final var note  = notes[player.getRandom().nextInt(notes.length)];
        final var pitch = NoteBlock.getPitchFromNote(note);
        level.playSound(
                null,
                player.getX(),
                player.getY(),
                player.getZ(),
                SoundEvents.NOTE_BLOCK_PLING.value(),
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
