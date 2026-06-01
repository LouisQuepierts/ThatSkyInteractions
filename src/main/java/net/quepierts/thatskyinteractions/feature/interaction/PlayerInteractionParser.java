package net.quepierts.thatskyinteractions.feature.interaction;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import lombok.experimental.UtilityClass;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.quepierts.thatskyinteractions.core.interaction.model.InteractionDefinitionEntry;
import net.quepierts.thatskyinteractions.core.interaction.model.InteractionDefinition;

import java.util.ArrayList;

@UtilityClass
public class PlayerInteractionParser {

    public static final Codec<InteractionDefinitionEntry> ENTRY_CODEC =
            RecordCodecBuilder.create(instance -> instance.group(
                    Codec.STRING.fieldOf("requester").forGetter(InteractionDefinitionEntry::requester),
                    Codec.STRING.fieldOf("receiver").forGetter(InteractionDefinitionEntry::receiver)
            ).apply(instance, InteractionDefinitionEntry::new));

    public static final Codec<InteractionDefinition> DEFINITION_CODEC =
            RecordCodecBuilder.create(instance -> instance.group(
                    Codec.INT.optionalFieldOf("levels", 1).forGetter(InteractionDefinition::levels),
                    Codec.STRING.optionalFieldOf("icon", "auto").forGetter(InteractionDefinition::icon),
                    ENTRY_CODEC.listOf().fieldOf("interactions").forGetter(InteractionDefinition::interactions)
            ).apply(instance, InteractionDefinition::new));

    public static final StreamCodec<ByteBuf, InteractionDefinitionEntry> ENTRY_STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.STRING_UTF8,
                    InteractionDefinitionEntry::requester,
                    ByteBufCodecs.STRING_UTF8,
                    InteractionDefinitionEntry::receiver,
                    InteractionDefinitionEntry::new
            );

    public static final StreamCodec<ByteBuf, InteractionDefinition> DEFINITION_STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.INT,
                    InteractionDefinition::levels,
                    ByteBufCodecs.STRING_UTF8,
                    InteractionDefinition::icon,
                    ByteBufCodecs.collection(
                            ArrayList::new,
                            ENTRY_STREAM_CODEC
                    ),
                    InteractionDefinition::interactions,
                    InteractionDefinition::new
            );

}
