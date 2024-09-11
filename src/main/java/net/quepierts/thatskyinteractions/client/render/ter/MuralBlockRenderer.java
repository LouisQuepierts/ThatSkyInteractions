package net.quepierts.thatskyinteractions.client.render.ter;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.quepierts.thatskyinteractions.block.entity.MuralBlockEntity;
import net.quepierts.thatskyinteractions.client.registry.RenderTypes;
import net.quepierts.thatskyinteractions.client.render.bloom.BloomBufferSource;
import net.quepierts.thatskyinteractions.registry.Items;
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

        PoseStack.Pose pose = poseStack.last();
        Matrix4f matrix4f = pose.pose();

        final float x = sizeF.x() / 32f;
        final float y = sizeF.y() / 32f;

        Vector3f normal = pose.transformNormal(0, 1, 0, new Vector3f());
        Vector3f v00 = matrix4f.transformPosition(-x,   -y, 0, new Vector3f());
        Vector3f v01 = matrix4f.transformPosition(-x,    y, 0, new Vector3f());
        Vector3f v11 = matrix4f.transformPosition( x,    y, 0, new Vector3f());
        Vector3f v10 = matrix4f.transformPosition( x,   -y, 0, new Vector3f());

        VertexConsumer vertexConsumer = RenderTypes.getBufferSource().getBuffer(RenderTypes.BLOOM.apply(mural.getTextureLocation(), false), this.bloomRenderer.getFinalTarget());
        vertexConsumer.addVertex(v00.x, v00.y, v00.z, 0xffffffff, 0, 1, combinedOverlay, combinedLight, normal.x, normal.y, normal.z);
        vertexConsumer.addVertex(v01.x, v01.y, v01.z, 0xffffffff, 0, 0, combinedOverlay, combinedLight, normal.x, normal.y, normal.z);
        vertexConsumer.addVertex(v11.x, v11.y, v11.z, 0xffffffff, 1, 0, combinedOverlay, combinedLight, normal.x, normal.y, normal.z);
        vertexConsumer.addVertex(v10.x, v10.y, v10.z, 0xffffffff, 1, 1, combinedOverlay, combinedLight, normal.x, normal.y, normal.z);

        poseStack.popPose();
        LocalPlayer player = Minecraft.getInstance().player;
        if (player != null && !player.isSpectator() && player.isShiftKeyDown()) {
            ItemStack itemStack = player.getItemInHand(InteractionHand.MAIN_HAND);

            if (itemStack.is(Items.MURAL)) {
                this.renderHighLight(poseStack, 0xffffffff, combinedLight, combinedOverlay);
            }
        }

        this.bloomRenderer.setApplyBloom();
    }

    @NotNull
    @Override
    public AABB getRenderBoundingBox(MuralBlockEntity blockEntity) {
        return blockEntity.getAabb();
    }
}
