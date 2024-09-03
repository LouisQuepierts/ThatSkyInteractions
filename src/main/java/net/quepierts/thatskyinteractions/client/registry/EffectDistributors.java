package net.quepierts.thatskyinteractions.client.registry;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.quepierts.thatskyinteractions.client.distributor.CandleFlameEffectDistributor;
import net.quepierts.thatskyinteractions.client.distributor.HitEffectDistributor;
import net.quepierts.thatskyinteractions.client.distributor.HugEffectDistributor;

@OnlyIn(Dist.CLIENT)
public class EffectDistributors {
    public static final CandleFlameEffectDistributor CANDLE_FLAME_EFFECT_DISTRIBUTOR = new CandleFlameEffectDistributor();
    public static final HugEffectDistributor HEART_EFFECT_DISTRIBUTOR = new HugEffectDistributor();
    public static final HitEffectDistributor HIGH_FIVE_EFFECT_DISTRIBUTOR = new HitEffectDistributor();
}
