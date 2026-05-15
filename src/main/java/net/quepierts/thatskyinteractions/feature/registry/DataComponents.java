package net.quepierts.thatskyinteractions.feature.registry;

import lombok.experimental.UtilityClass;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.feature.animation.HumanoidAnimationState;
import net.quepierts.thatskyinteractions.feature.component.HumanoidAnimationDataComponent;

import java.util.function.Supplier;

@UtilityClass
public class DataComponents {

    public static final DeferredRegister.DataComponents REGISTRAR
            = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, ThatSkyInteractions.MODID);

    public static final Supplier<DataComponentType<HumanoidAnimationState>> HUMANOID_ANIMATION
            = REGISTRAR.registerComponentType(
                    "humanoid_animation",
            builder -> builder.persistent(HumanoidAnimationDataComponent.CODEC)
            );


}
