package net.quepierts.thatskyinteractions.infra.animation.tween.interpolate;

public interface Interpolator1i extends Interpolator<Integer> {

    int interpolate(int from, int to, float progress);

    @Override
    default Integer interpolate(Integer from, Integer to, float progress) {
        return this.interpolate(from.intValue(), to.intValue(), progress);
    }

}
