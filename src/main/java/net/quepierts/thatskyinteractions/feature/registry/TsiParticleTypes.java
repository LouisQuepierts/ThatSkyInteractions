package net.quepierts.thatskyinteractions.feature.registry;

import lombok.experimental.UtilityClass;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.feature.particle.CallParticleType;

import java.util.function.Supplier;

@UtilityClass
public class TsiParticleTypes {

    public static final DeferredRegister<ParticleType<?>> REGISTER
            = DeferredRegister.create(Registries.PARTICLE_TYPE, ThatSkyInteractions.MODID);

    public static final Supplier<CallParticleType> CALL
            = REGISTER.register("call", CallParticleType::new);


}
