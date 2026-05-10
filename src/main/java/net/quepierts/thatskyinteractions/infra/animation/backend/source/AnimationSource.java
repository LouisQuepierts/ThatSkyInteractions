package net.quepierts.thatskyinteractions.infra.animation.backend.source;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.quepierts.thatskyinteractions.infra.util.LocationLookup;

@Getter
@RequiredArgsConstructor
public abstract class AnimationSource {

    private final LocationLookup channels;

}
