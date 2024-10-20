package net.quepierts.thatskyinteractions.client.render.pipeline;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;

public interface VBORenderer<T extends BlockEntity> extends BlockEntityRenderer<T> {
    default void render(
            @NotNull T var1,
            float var2,
            @NotNull PoseStack var3,
            @NotNull MultiBufferSource var4,
            int var5,
            int var6
    ) {}

    void renderVBO(
            @NotNull T blockEntity,
            @NotNull PoseStack poseStack
    );

}
