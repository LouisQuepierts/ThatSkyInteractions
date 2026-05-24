package net.quepierts.thatskyinteractions.infra.animation.tween.backend.task;

import net.quepierts.thatskyinteractions.infra.animation.core.adapter.Consumer1f;
import net.quepierts.thatskyinteractions.infra.animation.tween.ease.Ease;
import net.quepierts.thatskyinteractions.infra.animation.tween.interpolate.Interpolator1f;

public final class TweenTask1f extends DefaultTimingTask {

    private final float from;
    private final float to;

    private final Ease ease;

    private final Interpolator1f interpolator;

    private final Consumer1f consumer;

    public TweenTask1f(
            final float             duration,
            final float             from,
            final float             to,
            final Ease              ease,
            final Interpolator1f    interpolator,
            final Consumer1f        consumer
    ) {
        super(duration);
        this.from                   = from;
        this.to                     = to;
        this.ease                   = ease;
        this.interpolator           = interpolator;
        this.consumer               = consumer;
    }

    @Override
    protected void _update(final float delta) {

        final var t                 = Math.min(this.elapsed / this.duration, 1.0f);
        final var eased             = this.ease.map(t);

        final var v                 = this.interpolator.interpolate(this.from, this.to, eased);
        this.consumer               .accept(v);

    }

}
