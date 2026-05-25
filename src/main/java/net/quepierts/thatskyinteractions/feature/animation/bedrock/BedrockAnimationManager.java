package net.quepierts.thatskyinteractions.feature.animation.bedrock;

import com.google.common.collect.ImmutableMap;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.Identifier;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.core.animation.model.bedrock.BedrockAnimation;
import net.quepierts.thatskyinteractions.core.animation.model.bedrock.BedrockAnimationDefinition;
import net.quepierts.thatskyinteractions.feature.data.DataSyncManager;
import net.quepierts.thatskyinteractions.feature.data.DataSyncSystem;
import org.jspecify.annotations.NonNull;

import java.util.Map;
import java.util.function.IntFunction;

@Slf4j
public final class BedrockAnimationManager extends DataSyncManager<BedrockAnimationDefinition> {

    public static final Identifier IDENTIFIER
            = ThatSkyInteractions.location("animation/source");

    private static final String FOLDER
            = "animation/source";

    @Getter
    private static final BedrockAnimationManager instance
            = DataSyncSystem.register(BedrockAnimationManager::new);

    private static final StreamCodec<ByteBuf, Map<Identifier, BedrockAnimationDefinition>> STREAM_CODEC
            = ByteBufCodecs.map(
                    (IntFunction<Map<Identifier, BedrockAnimationDefinition>>) Object2ObjectOpenHashMap::new,
                    ByteBufCodecs.STRING_UTF8.map(
                            Identifier::parse,
                            Identifier::toString
                    ),
                    BedrockAnimationParser.ANIMATION_DEFINITION_STREAM_CODEC
            );

    private Map<Identifier, BedrockAnimationDefinition> definitions = Map.of();
    private Map<Identifier, BedrockAnimation>           animations  = Map.of();

    private BedrockAnimationManager() {
        super(
                BedrockAnimationParser.ANIMATION_DEFINITION_CODEC,
                FileToIdConverter.json(FOLDER),
                IDENTIFIER
        );
    }

    @Override
    protected void apply(@NonNull final Map<Identifier, BedrockAnimationDefinition> preparations) {
        var builder         = ImmutableMap.<Identifier, BedrockAnimationDefinition>builder();
        var builder2        = ImmutableMap.<Identifier, BedrockAnimation>builder();

        for (final var entry : preparations.entrySet()) {
            final var identifier = entry.getKey();
            builder.put(identifier, entry.getValue());

            for (final var entry2 : entry.getValue().animations().entrySet()) {
                builder2.put(
                        identifier.withSuffix("." + entry2.getKey()),
                        entry2.getValue()
                );
            }
        }

        this.definitions    = builder.build();
        this.animations     = builder2.build();

        log.info("Loaded {} bedrock animation sources from {} files", this.animations.size(), this.definitions.size());
    }

    @Override
    protected @NonNull StreamCodec<ByteBuf, Map<Identifier, BedrockAnimationDefinition>> getStreamCodec() {
        return STREAM_CODEC;
    }

    public BedrockAnimationDefinition getDefinition(Identifier identifier) {
        return this.definitions.get(identifier);
    }

    public BedrockAnimation getAnimation(Identifier identifier) {
        return this.animations.get(identifier);
    }

}
