package net.quepierts.thatskyinteractions.client.render.distributor;

import net.minecraft.client.player.AbstractClientPlayer;
import net.quepierts.simpleanimator.api.animation.keyframe.VariableHolder;
import org.joml.Vector3f;

public interface IEffectDistributor {
    String name();

    Vector3f position();

    void distribute(Vector3f pos, AbstractClientPlayer player, VariableHolder var);
}
