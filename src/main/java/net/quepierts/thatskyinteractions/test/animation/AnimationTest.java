package net.quepierts.thatskyinteractions.test.animation;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.feature.animation.AnimationReloadedEvent;
import net.quepierts.thatskyinteractions.feature.animation.BedrockAnimationCompiler;
import net.quepierts.thatskyinteractions.feature.animation.DefaultMinecraftAnimationPipeline;
import net.quepierts.thatskyinteractions.infra.animation.backend.sampler.AnimationSampler;
import net.quepierts.thatskyinteractions.infra.animation.backend.source.AnimationSource;

@Log4j2
@EventBusSubscriber
public class AnimationTest {

    private static final Identifier IDENTIFIER = ThatSkyInteractions.location("wave");

    @Getter
    private static AnimationSource source;

    @Getter
    private static AnimationSampler sampler;

    @SubscribeEvent
    public static void onAnimationReloaded(final AnimationReloadedEvent event) {
        var manager     = event.getManager();
        var animation   = manager.get(IDENTIFIER);

        if (animation   != null) {
            source      = BedrockAnimationCompiler.compile(animation.animations().get("main"));
            sampler     = source.link(DefaultMinecraftAnimationPipeline.HUMANOID_TIMELINE);
        } else {
            log.warn("Could not find animation [{}]", IDENTIFIER);
        }
    }

}
