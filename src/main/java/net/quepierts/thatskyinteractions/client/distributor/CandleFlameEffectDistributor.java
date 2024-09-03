package net.quepierts.thatskyinteractions.client.distributor;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.quepierts.simpleanimator.api.animation.keyframe.VariableHolder;
import net.quepierts.simpleanimator.core.client.ClientAnimator;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.client.registry.Particles;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

@OnlyIn(Dist.CLIENT)
public class CandleFlameEffectDistributor extends EffectDistributor {
    private static final Vector3f POSITION = new Vector3f(0.0f, -0.48f, 0.75f);

    @Override
    public String name() {
        return "heldCandle";
    }

    @Override
    public Vector3f position(@NotNull ClientAnimator animator) {
        return POSITION;
    }

    @Override
    public boolean shouldSkipDistribute(VariableHolder var) {
        return !var.getAsBoolean();
    }

    @Override
    public void distribute(@NotNull Vector3f pos, @NotNull Player player, @NotNull ClientAnimator animator) {
        Level level = player.level();

        level.addParticle(Particles.SHORTER_FLAME.get(), pos.x, pos.y, pos.z, 0, 0, 0);

        final RandomSource random = ThatSkyInteractions.RANDOM;
        float f = random.nextFloat();
        if (f < 0.3F) {
            if (f < 0.17F) {
                level.playLocalSound(
                        pos.x + 0.5,
                        pos.y + 0.5,
                        pos.z + 0.5,
                        SoundEvents.CANDLE_AMBIENT,
                        SoundSource.PLAYERS,
                        1.0F + random.nextFloat(),
                        random.nextFloat() * 0.7F + 0.3F,
                        false
                );
            }
        }
    }
}
