package net.quepierts.thatskyinteractions.feature.client.animation;

import lombok.extern.slf4j.Slf4j;
import net.minecraft.resources.Identifier;
import net.neoforged.fml.common.EventBusSubscriber;
import net.quepierts.thatskyinteractions.feature.animation.DefaultMinecraftAnimationPipeline;
import net.quepierts.thatskyinteractions.infra.animation.backend.sampler.AnimationSampler;
import net.quepierts.thatskyinteractions.infra.animation.backend.source.AnimationSource;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@EventBusSubscriber
public final class AnimationController {

    private final static Map<Identifier, AnimationSampler> cache = new HashMap<>();

    public static void afterClientAnimationBaked(final ClientAnimationBakingEvent.Post event) {
        cache.clear();
    }

    private Identifier          current;
    private AnimationSource     source;
    private AnimationSampler    sampler;

    private float               time;
    private boolean             playing;
    private boolean             loop;

    public void play(final Identifier identifier) {
        final var source = ClientAnimationManager.getInstance().get(identifier);

        if (source == null) {
            log.warn("Animation not found: [{}]", identifier);
            return;
        }

        this.current    = identifier;
        this.source     = source;

        final var sampler = cache.get(identifier);
        if (sampler == null) {
            this.sampler = source.link(DefaultMinecraftAnimationPipeline.HUMANOID_TIMELINE);
            cache.put(identifier, this.sampler);
        } else {
            this.sampler = sampler;
        }

        this.time       = 0;
        this.playing    = true;
    }

    public void update(float delta) {

    }

}
