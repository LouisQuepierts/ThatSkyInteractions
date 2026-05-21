package net.quepierts.thatskyinteractions.feature.animation.bedrock;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.neoforged.bus.api.Event;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public final class BedrockAnimationReloadedEvent extends Event {

    private final BedrockAnimationManager manager;

}
