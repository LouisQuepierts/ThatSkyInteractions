package net.quepierts.thatskyinteractions.feature.particle;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;
import net.quepierts.thatskyinteractions.feature.registry.TsiParticleTypes;
import org.jspecify.annotations.NonNull;

public record CallParticleOption(
        int id
) implements ParticleOptions {

    public static final MapCodec<CallParticleOption> CODEC
            = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.INT.fieldOf("id").forGetter(CallParticleOption::id)
    ).apply(instance, CallParticleOption::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, CallParticleOption> STREAM_CODEC
            = StreamCodec.composite(
            ByteBufCodecs.VAR_INT,
            CallParticleOption::id,
            CallParticleOption::new
    );

    public static CallParticleOption of(final @NonNull Player player) {
        return new CallParticleOption(player.getId());
    }

    @Override
    public @NonNull ParticleType<?> getType() {
        return TsiParticleTypes.CALL.get();
    }
}
