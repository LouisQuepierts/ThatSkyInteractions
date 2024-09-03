package net.quepierts.thatskyinteractions.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SimpleAnimatedParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CircleParticle extends SimpleAnimatedParticle {
    private static final int MAX_AGE = 60;
    protected CircleParticle(ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, SpriteSet sprites) {
        super(level, x, y, z, sprites, 0);
        float size = 0.35f + this.random.nextFloat() * 0.25f;
        this.quadSize *= size;
        float amplifier = 1 - size;
        this.xd = xSpeed * amplifier;
        this.yd = ySpeed * amplifier;
        this.zd = zSpeed * amplifier;
        this.lifetime = 60 + this.random.nextInt(12);
        this.setSpriteFromAge(sprites);
    }

    @Override
    public void tick() {
        super.tick();
        this.alpha = 1 - (float) age / this.lifetime;
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet sprites) {
            this.sprites = sprites;
        }
        @Nullable
        @Override
        public Particle createParticle(@NotNull SimpleParticleType simpleParticleType, @NotNull ClientLevel clientLevel, double x, double y, double z, double v3, double v4, double v5) {
            return new CircleParticle(clientLevel, x, y, z, v3, v4, v5, sprites);
        }
    }
}
