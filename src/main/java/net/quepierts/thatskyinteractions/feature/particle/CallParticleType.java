package net.quepierts.thatskyinteractions.feature.particle;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.jspecify.annotations.NonNull;

public class CallParticleType extends ParticleType<CallParticleOption> {

    public CallParticleType() {
        super(false);
    }

    @Override
    public @NonNull MapCodec<CallParticleOption> codec() {
        return CallParticleOption.CODEC;
    }

    @Override
    public @NonNull StreamCodec<? super RegistryFriendlyByteBuf, CallParticleOption> streamCodec() {
        return CallParticleOption.STREAM_CODEC;
    }
}
