package net.quepierts.thatskyinteractions.feature.registry;

import lombok.experimental.UtilityClass;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;

@UtilityClass
public class DataComponents {

    public static final DeferredRegister.DataComponents REGISTRAR
            = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, ThatSkyInteractions.MODID);


}
