package net.quepierts.thatskyinteractions.feature.client.animation;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.Event;
import net.quepierts.thatskyinteractions.core.model.animation.bedrock.BedrockAnimation;
import net.quepierts.thatskyinteractions.feature.animation.bedrock.BedrockAnimationManager;
import net.quepierts.thatskyinteractions.infra.animation.backend.source.AnimationSource;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
public sealed abstract class ClientAnimationBakingEvent
        extends Event
        permits ClientAnimationBakingEvent.Post, ClientAnimationBakingEvent.Pre {

    @Getter
    private final BedrockAnimationManager   bedrockAnimationManager;

    @Getter
    private final ClientAnimationManager    clientAnimationManager;

    public static final class Pre extends ClientAnimationBakingEvent {

        private final List<ClientAnimationManager.BakeEntry>    entries;
        private final List<ClientAnimationManager.BakeResult>   results;

        public Pre(
                final BedrockAnimationManager                   bedrockAnimationManager,
                final ClientAnimationManager                    clientAnimationManager,
                final List<ClientAnimationManager.BakeEntry>    entries,
                final List<ClientAnimationManager.BakeResult>   results
        ) {
            super(bedrockAnimationManager, clientAnimationManager);
            this.entries = entries;
            this.results = results;
        }

        public void add(Identifier identifier, BedrockAnimation animation) {
            entries.add(new ClientAnimationManager.BakeEntry(identifier, animation));
        }

        public void add(Identifier identifier, String... names) {
            final var definition    = this.getBedrockAnimationManager().get(identifier);
            if (definition == null) {
                log.warn("Could not find animation [{}]", identifier);
                return;
            }

            final var animations    = definition.animations();
            if (names.length == 0) {
                for (final var entry : animations.entrySet()) {
                    final var location = identifier.withSuffix("." + entry.getKey());
                    this.entries.add(new ClientAnimationManager.BakeEntry(location, entry.getValue()));
                }
            } else {
                for (final var name : names) {
                    final var animation = animations.get(name);
                    final var location = identifier.withSuffix("." + name);

                    if (animation == null) {
                        log.warn("Could not find animation [{}]", location);
                        continue;
                    }

                    this.entries.add(new ClientAnimationManager.BakeEntry(location, animation));
                }
            }
        }

        public void add(Identifier identifier, AnimationSource source) {
            results.add(new ClientAnimationManager.BakeResult(identifier, source));
        }

    }

    public static final class Post extends ClientAnimationBakingEvent {

        public Post(
                final BedrockAnimationManager bedrockAnimationManager,
                final ClientAnimationManager clientAnimationManager
        ) {
            super(bedrockAnimationManager, clientAnimationManager);
        }
    }

}
