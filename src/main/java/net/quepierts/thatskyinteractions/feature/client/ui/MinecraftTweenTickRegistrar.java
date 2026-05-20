package net.quepierts.thatskyinteractions.feature.client.ui;

import net.quepierts.thatskyinteractions.feature.client.ClientTickHandler;
import net.quepierts.thatskyinteractions.infra.animation.tween.backend.TweenTickHandler;
import net.quepierts.thatskyinteractions.infra.animation.tween.backend.TweenTickRegistrar;
import org.jspecify.annotations.NonNull;

public class MinecraftTweenTickRegistrar implements TweenTickRegistrar {

    @Override
    public void register(final @NonNull TweenTickHandler handler) {
        ClientTickHandler.register(handler::tick);
    }

}
