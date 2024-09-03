package net.quepierts.thatskyinteractions.client.distributor;

import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.quepierts.simpleanimator.api.animation.keyframe.VariableHolder;
import net.quepierts.simpleanimator.core.client.ClientAnimator;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

@OnlyIn(Dist.CLIENT)
public abstract class EffectDistributor {
    protected static final RandomSource RANDOM = RandomSource.create();
    public abstract String name();

    public abstract Vector3f position(@NotNull ClientAnimator animator);

    public abstract boolean shouldSkipDistribute(VariableHolder var);

    public abstract void distribute(@NotNull Vector3f pos, @NotNull Player player, @NotNull ClientAnimator var);
}
