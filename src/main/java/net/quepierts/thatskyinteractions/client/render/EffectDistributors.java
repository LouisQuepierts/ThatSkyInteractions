package net.quepierts.thatskyinteractions.client.render;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.quepierts.thatskyinteractions.client.render.distributor.CandleFlameEffectDistributor;

@OnlyIn(Dist.CLIENT)
public class EffectDistributors {
    public static final CandleFlameEffectDistributor CANDLE_FLAME_EFFECT_DISTRIBUTOR = new CandleFlameEffectDistributor();

}
