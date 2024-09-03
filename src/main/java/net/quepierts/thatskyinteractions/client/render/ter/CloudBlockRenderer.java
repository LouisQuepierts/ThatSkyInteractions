package net.quepierts.thatskyinteractions.client.render.ter;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.SkullModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Item;
import net.minecraft.world.phys.Vec3;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.block.entity.CloudBlockEntity;
import net.quepierts.thatskyinteractions.client.registry.PostEffects;
import net.quepierts.thatskyinteractions.client.registry.RenderTypes;
import net.quepierts.thatskyinteractions.client.render.cloud.CloudData;
import net.quepierts.thatskyinteractions.client.render.cloud.CloudRenderer;
import net.quepierts.thatskyinteractions.registry.Items;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import org.joml.Vector3i;

public class CloudBlockRenderer implements BlockEntityRenderer<CloudBlockEntity> {
    // WTF?
    private final SkullModel highlight;
    private CloudRenderer renderer;

    public CloudBlockRenderer(BlockEntityRendererProvider.Context context) {
        this.highlight = new SkullModel(context.getModelSet().bakeLayer(ModelLayers.PLAYER_HEAD));
    }

    @Override
    public void render(
            @NotNull CloudBlockEntity cloudBlockEntity,
            float partialTick,
            @NotNull PoseStack poseStack,
            @NotNull MultiBufferSource multiBufferSource,
            int combinedLight,
            int combinedOverlay
    ) {
        if (this.renderer == null) {
            this.renderer = ThatSkyInteractions.getInstance().getClient().getCloudRenderer();
        }

        if (cloudBlockEntity.shouldRecompile()) {
            Vector3i size0 = cloudBlockEntity.getSize();
            Vector3i offset = cloudBlockEntity.getOffset();
            BlockPos pos = cloudBlockEntity.getBlockPos();
            Vector3f position = new Vector3f(pos.getX(), pos.getY(), pos.getZ()).add(
                    offset.x / 16.0f - 0.25f,
                    offset.y / 16.0f - 0.25f,
                    offset.z / 16.0f - 0.25f
            );
            Vector3f size = new Vector3f(
                    size0.x / 16.0f,
                    size0.y / 16.0f,
                    size0.z / 16.0f
            );
            this.renderer.addCloud(cloudBlockEntity.getUUID(), new CloudData(position, size, 0));
            cloudBlockEntity.setShouldRecompile(false);
        }

        LocalPlayer player = Minecraft.getInstance().player;

        if (player == null) {
            return;
        }

        Item item = player.getItemInHand(InteractionHand.MAIN_HAND).getItem();
        if (item == Items.CLOUD.get() || item == Items.CLOUD_EXPAND.get() || item == Items.CLOUD_REDUCE.get()) {
            poseStack.pushPose();
            poseStack.translate(0.5f, 0.75f, 0.5f);

            VertexConsumer vertexConsumer = RenderTypes.getBufferSource().getBuffer(RenderTypes.WOL);
            highlight.renderToBuffer(poseStack, vertexConsumer, combinedLight, combinedOverlay);

            PostEffects.setApplyBloom();
            poseStack.popPose();
        }
    }

    @Override
    public int getViewDistance() {
        return 96;
    }

    @Override
    public boolean shouldRender(@NotNull CloudBlockEntity blockEntity, @NotNull Vec3 cameraPos) {
        boolean render = BlockEntityRenderer.super.shouldRender(blockEntity, cameraPos);
        if (!render) {
            if (this.renderer == null) {
                this.renderer = ThatSkyInteractions.getInstance().getClient().getCloudRenderer();
            }

            blockEntity.setShouldRecompile(true);
            this.renderer.removeCloud(blockEntity.getUUID());
        }
        return render;
    }

    @Override
    public boolean shouldRenderOffScreen(@NotNull CloudBlockEntity blockEntity) {
        return true;
    }
}
