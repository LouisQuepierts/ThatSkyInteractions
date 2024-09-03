package net.quepierts.thatskyinteractions.client.registry;

import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;

@OnlyIn(Dist.CLIENT)
public class Particles {
    public static final DeferredRegister<ParticleType<?>> REGISTER = DeferredRegister.create(
            BuiltInRegistries.PARTICLE_TYPE, ThatSkyInteractions.MODID
    );

    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> SHORTER_FLAME;
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> STAR;
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> HEART;
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> CIRCLE;

    static {
        SHORTER_FLAME = REGISTER.register(
                "shorter_flame",
                () -> new SimpleParticleType(false)
        );

        STAR = REGISTER.register(
                "star",
                () -> new SimpleParticleType(false)
        );

        HEART = REGISTER.register(
                "heart",
                () -> new SimpleParticleType(false)
        );

        CIRCLE = REGISTER.register(
                "circle",
                () -> new SimpleParticleType(false)
        );
    }
}
