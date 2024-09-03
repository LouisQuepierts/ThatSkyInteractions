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
public class HugEffectDistributor extends EffectDistributor {
    @Override
    public String name() {
        return "hugEffect";
    }

    @Override
    public Vector3f position(@NotNull ClientAnimator animator) {
        return new Vector3f(0, 1.5f, 0.3f);
    }

    @Override
    public boolean shouldSkipDistribute(VariableHolder var) {
        return !var.getAsBoolean();
    }

    @Override
    public void distribute(@NotNull Vector3f pos, @NotNull Player player, @NotNull ClientAnimator animator) {
        Level level = player.level();
        level.addParticle(Particles.HEART.get(), pos.x, pos.y, pos.z, 0, 0, 0);
    }
}
