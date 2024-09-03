package net.quepierts.thatskyinteractions.client.distributor;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.quepierts.simpleanimator.api.animation.keyframe.VariableHolder;
import net.quepierts.simpleanimator.core.client.ClientAnimator;
import net.quepierts.thatskyinteractions.client.registry.Particles;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

@OnlyIn(Dist.CLIENT)
public class HitEffectDistributor extends EffectDistributor {
    private static final int PARTICLE_AMOUNT = 20;
    @Override
    public String name() {
        return "hitEffect";
    }

    @Override
    public Vector3f position(@NotNull ClientAnimator animator) {
        //return new Vector3f(0, 1.5f, 0.3f);
        return animator.getVariable("effectPos")
                .getAsVector3f()
                .div(16f, 16f, -16f);
    }

    @Override
    public boolean shouldSkipDistribute(VariableHolder var) {
        return !var.getAsBoolean();
    }

    @Override
    public void distribute(@NotNull Vector3f pos, @NotNull Player player, @NotNull ClientAnimator animator) {
        Level level = player.level();

        for (int i = 0; i < PARTICLE_AMOUNT; i++) {
            level.addParticle(Particles.CIRCLE.get(), pos.x, pos.y, pos.z,
                    (RANDOM.nextFloat() - 0.5) * 0.5,
                    (RANDOM.nextFloat() - 0.5) * 0.5,
                    (RANDOM.nextFloat() - 0.5) * 0.5
            );
        }
    }
}
