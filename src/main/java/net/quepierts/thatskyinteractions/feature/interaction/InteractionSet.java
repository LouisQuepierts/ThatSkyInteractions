package net.quepierts.thatskyinteractions.feature.interaction;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;

import java.util.List;

public record InteractionSet(
        Identifier          icon,
        List<Interaction>   interactions
) {

    public static final Codec<InteractionSet> CODEC
            = RecordCodecBuilder.create(instance -> instance.group(
                Identifier.CODEC.fieldOf("icon").forGetter(InteractionSet::icon),
                Interaction.CODEC.listOf(1, 16).fieldOf("interactions").forGetter(InteractionSet::interactions)
            ).apply(instance, InteractionSet::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, InteractionSet> STREAM_CODEC
            = StreamCodec.composite(
                    Identifier.STREAM_CODEC,
                    InteractionSet::icon,
                    ByteBufCodecs.<RegistryFriendlyByteBuf, Interaction>list().apply(Interaction.STREAM_CODEC),
                    InteractionSet::interactions,
                    InteractionSet::new
            );

    public boolean leveled() {
        return this.interactions.size() > 1;
    }

    public int levels() {
        return this.interactions.size() == 1 ? 0 : this.interactions.size();
    }

}
