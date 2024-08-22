package net.quepierts.thatskyinteractions.client.render.distributor;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.quepierts.simpleanimator.api.animation.keyframe.VariableHolder;
import net.quepierts.thatskyinteractions.client.Particles;
import org.joml.Vector3f;

public class CandleFlameEffectDistributor implements IEffectDistributor {
    @Override
    public String name() {
        return "heldCandle";
    }

    @Override
    public Vector3f position() {
        return new Vector3f(-0.0625F, 0, 0.5625F);
    }

    @Override
    public void distribute(Vector3f pos, AbstractClientPlayer player, VariableHolder var) {
        Level level = player.level();

        level.addParticle(Particles.SHORTER_FLAME.get(), pos.x, pos.y, pos.z, 0, 0, 0);
    }
}
