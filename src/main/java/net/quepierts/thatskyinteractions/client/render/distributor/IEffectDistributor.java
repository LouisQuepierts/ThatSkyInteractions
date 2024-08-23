package net.quepierts.thatskyinteractions.client.render.distributor;

import net.minecraft.client.player.AbstractClientPlayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.quepierts.simpleanimator.api.animation.keyframe.VariableHolder;
import org.joml.Vector3f;

@OnlyIn(Dist.CLIENT)
public interface IEffectDistributor {
    String name();

    Vector3f position();

    void distribute(Vector3f pos, AbstractClientPlayer player, VariableHolder var);
}
