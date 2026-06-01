package net.quepierts.thatskyinteractions.feature.animation.bedrock;

import com.google.common.collect.ImmutableMap;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.core.animation.model.bedrock.BedrockAnimation;
import net.quepierts.thatskyinteractions.core.animation.model.bedrock.BedrockAnimationDefinition;
import net.quepierts.thatskyinteractions.feature.data.DataSyncManager;
import net.quepierts.thatskyinteractions.feature.data.event.RegisterSyncManagerEvent;
import org.jspecify.annotations.NonNull;

import java.util.Map;

@Slf4j
@EventBusSubscriber(modid = ThatSkyInteractions.MODID)
public final class BedrockAnimationManager extends DataSyncManager<BedrockAnimationDefinition> {

    private static final String FOLDER
            = "animation/source";

    @Getter
    private static final BedrockAnimationManager instance
            = new BedrockAnimationManager();

    private Map<Identifier, BedrockAnimationDefinition> definitions = Map.of();
    private Map<Identifier, BedrockAnimation>           animations  = Map.of();

    private BedrockAnimationManager() {
        super(
                BedrockAnimationParser.ANIMATION_DEFINITION_CODEC,
                BedrockAnimationParser.ANIMATION_DEFINITION_STREAM_CODEC,
                FOLDER
        );
    }

    @SubscribeEvent
    public static void onRegisterSyncManager(final RegisterSyncManagerEvent event) {
        event.register(instance);
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

    public BedrockAnimationDefinition getDefinition(Identifier identifier) {
        return this.definitions.get(identifier);
    }

    public BedrockAnimation getAnimation(Identifier identifier) {
        return this.animations.get(identifier);
    }

}
