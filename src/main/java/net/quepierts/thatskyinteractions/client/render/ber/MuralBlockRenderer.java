package net.quepierts.thatskyinteractions.client.render.ber;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.quepierts.thatskyinteractions.block.entity.MuralBlockEntity;
import net.quepierts.thatskyinteractions.client.render.pipeline.VertexBufferManager;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector2f;
import org.joml.Vector3f;

@OnlyIn(Dist.CLIENT)
public class MuralBlockRenderer extends HighlightBlockEntityRenderer<MuralBlockEntity> {

    @SuppressWarnings("unused")
    public MuralBlockRenderer(BlockEntityRendererProvider.Context context) {}

    @Override
    public void render(
            @NotNull MuralBlockEntity mural,
            float partialTick,
            @NotNull PoseStack poseStack,
            @NotNull MultiBufferSource multiBufferSource,
            int combinedLight,
            int combinedOverlay
    ) {
        if (mural.isDirty()) {
            this.bloomRenderer.clearRenderAction(mural);
            mural.setDirty(false);
            BlockPos blockPos = mural.getBlockPos();
            Vector2f sizeF = mural.getSizeF();
            Vector3f offsetF = mural.getOffsetF();

            Matrix4f transformation = new Matrix4f();
            Vector3f rotate = mural.getRotateF();
            transformation.translate(
                    blockPos.getX() + offsetF.x / 16.0f + 0.5f,
                    blockPos.getY() + offsetF.y / 16.0f + 0.5f,
                    blockPos.getZ() + offsetF.z / 16.0f + 0.5f
            ).rotate(
                    new Quaternionf().rotateYXZ(
                            rotate.y * Mth.DEG_TO_RAD,
                            rotate.x * Mth.DEG_TO_RAD,
                            rotate.z * Mth.DEG_TO_RAD
                    )
            ).scale(sizeF.x / 16f, sizeF.y / 16f, 1);

            this.bloomRenderer.addRenderAction(
                    mural,
                    VertexBufferManager.QUAD,
                    transformation,
                    mural.getTextureLocation()
            );
        }

        /*LocalPlayer player = Minecraft.getInstance().player;
        if (player != null && !player.isSpectator() && player.isShiftKeyDown()) {
            ItemStack itemStack = player.getItemInHand(InteractionHand.MAIN_HAND);

            if (itemStack.is(Items.MURAL)) {
                this.renderHighLight(poseStack, 0xffffffff, combinedLight, combinedOverlay);
            }
        }*/
    }

    /*@Override
    public void renderVBO(@NotNull MuralBlockEntity mural, @NotNull PoseStack poseStack) {
        Vector2f sizeF = mural.getSizeF();
        Vector3f offsetF = mural.getOffsetF();

        poseStack.pushPose();
        poseStack.translate(
                offsetF.x / 16.0f + 0.5f,
                offsetF.y / 16.0f + 0.5f,
                offsetF.z / 16.0f + 0.5f
        );

        Vector3f rotate = mural.getRotateF();
        poseStack.mulPose(
                new Quaternionf().rotateYXZ(
                        rotate.y * Mth.DEG_TO_RAD,
                        rotate.x * Mth.DEG_TO_RAD,
                        rotate.z * Mth.DEG_TO_RAD
                )
        );

        poseStack.scale(sizeF.x / 16f, sizeF.y / 16f, 1);

        this.bloomRenderer.batchRender(
                VertexBufferManager.QUAD,
                poseStack.last().pose(),
                mural.getTextureLocation()
        );

        poseStack.popPose();
        this.bloomRenderer.setApplyBloom();
    }*/

    @NotNull
    @Override
    public AABB getRenderBoundingBox(MuralBlockEntity blockEntity) {
        return blockEntity.getAabb();
    }

    @Override
    public boolean shouldRender(@NotNull MuralBlockEntity blockEntity, @NotNull Vec3 cameraPos) {
        boolean b = super.shouldRender(blockEntity, cameraPos);
        if (!b) {
            this.bloomRenderer.removeRenderAction(blockEntity);
        }
        return b;
    }
}
