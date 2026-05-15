package net.quepierts.thatskyinteractions.feature.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.experimental.UtilityClass;
import net.quepierts.thatskyinteractions.feature.animation.HumanoidAnimationState;

import java.util.function.Function;

@UtilityClass
public class HumanoidAnimationDataComponent {

    public static final Codec<HumanoidAnimationState> CODEC = Codec.INT.xmap(
            _ -> HumanoidAnimationState._default(),
            _ -> 0
    );

}
