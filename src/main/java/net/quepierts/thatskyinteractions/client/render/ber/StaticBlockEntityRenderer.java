package net.quepierts.thatskyinteractions.client.render.ber;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.quepierts.thatskyinteractions.client.render.section.StaticModelRenderer;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public interface StaticBlockEntityRenderer<T extends BlockEntity> extends BlockEntityRenderer<T> {
    default void render(
            @NotNull T blockEntity,
            float partialTick,
            @NotNull PoseStack poseStack,
            @NotNull MultiBufferSource multiBufferSource,
            int combinedLight,
            int combinedOverlay
    ) {}

    default boolean shouldRender(@NotNull T blockEntity, @NotNull Vec3 cameraPos) {
        return false;
    }

    void render(
            @NotNull T blockEntity,
            @NotNull StaticModelRenderer renderer,
            @NotNull BlockPos blockPos,
            @NotNull PoseStack poseStack
    );
}
