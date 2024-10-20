package net.quepierts.thatskyinteractions.client.render.pipeline;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;

import java.util.Set;

public class VBORenderSystem {

    public static void renderBlockEntities(
            BlockEntityRenderDispatcher dispatcher,
            Set<BlockEntity> entities,
            Camera camera,
            PoseStack poseStack,
            Frustum frustum
    ) {
        Vec3 position = camera.getPosition();
        double d0 = position.x;
        double d1 = position.y;
        double d2 = position.z;
        for (BlockEntity blockEntity : entities) {
            BlockEntityRenderer<BlockEntity> entityRenderer = dispatcher.getRenderer(blockEntity);

            if (entityRenderer instanceof VBORenderer<BlockEntity> renderer) {
                if (frustum.isVisible(renderer.getRenderBoundingBox(blockEntity))) {
                    BlockPos blockPos = blockEntity.getBlockPos();
                    poseStack.pushPose();
                    poseStack.translate((double)blockPos.getX() - d0, (double)blockPos.getY() - d1, (double)blockPos.getZ() - d2);

                    if (shouldRender(blockEntity, renderer, camera)) {
                        tryRender(blockEntity, renderer, poseStack);
                    }

                    poseStack.popPose();
                }
            }
        }
    }

    private static void tryRender(
            BlockEntity blockEntity,
            VBORenderer<BlockEntity> renderer,
            PoseStack poseStack
    ) {
        try {
            renderer.renderVBO(blockEntity, poseStack);
        } catch (Throwable var5) {
            Throwable throwable = var5;
            CrashReport crashreport = CrashReport.forThrowable(throwable, "Rendering Block Entity");
            CrashReportCategory crashreportcategory = crashreport.addCategory("Block Entity Details");
            blockEntity.fillCrashReportCategory(crashreportcategory);
            throw new ReportedException(crashreport);
        }
    }

    private static boolean shouldRender(
            BlockEntity blockEntity,
            VBORenderer<BlockEntity> renderer,
            Camera camera
    ) {
        return blockEntity.hasLevel()
                && blockEntity.getType().isValid(blockEntity.getBlockState())
                && renderer.shouldRender(blockEntity, camera.getPosition());
    }
}
