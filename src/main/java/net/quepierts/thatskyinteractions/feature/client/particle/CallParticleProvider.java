package net.quepierts.thatskyinteractions.feature.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.quepierts.thatskyinteractions.feature.particle.CallParticleOption;
import net.quepierts.thatskyinteractions.feature.particle.CallParticleType;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public final class CallParticleProvider implements ParticleProvider<CallParticleOption> {

    private final SpriteSet spriteSet;

    public CallParticleProvider(final @NonNull SpriteSet spriteSet) {
        this.spriteSet = spriteSet;
    }

    @Override
    public @Nullable Particle createParticle(
            final CallParticleOption    option,
            final @NonNull ClientLevel  level,
            final double                x,
            final double                y,
            final double                z,
            final double                xAux,
            final double                yAux,
            final double                zAux,
            final @NonNull RandomSource random
    ) {
        final var entity = level.getEntity(option.id());
        if (entity instanceof Player player) {
            return new CallParticle(
                    level,
                    x,
                    y,
                    z,
                    this.spriteSet.first(),
                    player
            );
        }
        return null;
    }
}
