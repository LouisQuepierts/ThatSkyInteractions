package net.quepierts.thatskyinteractions.client.render.pipeline;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.vertex.VertexBuffer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.joml.Matrix4f;

@OnlyIn(Dist.CLIENT)
public interface IRenderAction {
    void apply(VertexBuffer buffer, Matrix4f projectionMatrix, Matrix4f frustrumMatrix);
}
