package net.quepierts.thatskyinteractions.feature.client.animation;

import lombok.Getter;
import net.minecraft.resources.Identifier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.NeoForge;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.core.model.animation.BedrockAnimation;
import net.quepierts.thatskyinteractions.feature.animation.AnimationReloadedEvent;
import net.quepierts.thatskyinteractions.feature.animation.BedrockAnimationCompiler;
import net.quepierts.thatskyinteractions.feature.animation.BedrockAnimationManager;
import net.quepierts.thatskyinteractions.infra.animation.backend.source.AnimationSource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@EventBusSubscriber(value = Dist.CLIENT, modid = ThatSkyInteractions.MODID)
public final class ClientAnimationManager {

    @Getter
    private static final ClientAnimationManager instance    = new ClientAnimationManager();
    private final Map<Identifier, AnimationSource> map      = new HashMap<>();

    @SubscribeEvent
    private static void onAnimationReloaded(final AnimationReloadedEvent event) {
        ClientAnimationManager.instance.reload(event.getManager());
    }

    private void reload(final BedrockAnimationManager manager) {
        this.map.clear();

        var entries = new ArrayList<BakeEntry>();
        var results = new ArrayList<BakeResult>();
        NeoForge.EVENT_BUS.post(new ClientAnimationBakingEvent.Pre(
                manager,
                this,
                entries,
                results
        ));

        for (var entry : entries) {
            var source = BedrockAnimationCompiler.compile(entry.raw());
            this.map.put(entry.identifier(), source);
        }

        for (var result : results) {
            this.map.put(result.identifier(), result.source());
        }

        NeoForge.EVENT_BUS.post(new ClientAnimationBakingEvent.Post(
                manager,
                this
        ));
    }

    public AnimationSource get(Identifier identifier) {
        return this.map.get(identifier);
    }

    public Collection<Identifier> identifiers() {
        return this.map.keySet();
    }

    public record BakeEntry(
            Identifier          identifier,
            BedrockAnimation    raw
    ) {}

    public record BakeResult(
            Identifier          identifier,
            AnimationSource     source
    ) {}

}
