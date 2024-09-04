package net.quepierts.thatskyinteractions.client.render.ter;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.SkullModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Item;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.block.entity.SimpleCloudBlockEntity;
import net.quepierts.thatskyinteractions.client.registry.PostEffects;
import net.quepierts.thatskyinteractions.client.registry.RenderTypes;
import net.quepierts.thatskyinteractions.client.render.cloud.CloudData;
import net.quepierts.thatskyinteractions.client.render.cloud.CloudRenderer;
import net.quepierts.thatskyinteractions.registry.Items;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import org.joml.Vector3i;

import java.util.Collections;

@OnlyIn(Dist.CLIENT)
public class SimpleCloudBlockRenderer implements BlockEntityRenderer<SimpleCloudBlockEntity> {
    private static final ResourceLocation HIGHLIGHT = ThatSkyInteractions.getLocation("textures/entity/wing_of_light.png");
    private final ModelPart.Cube cube;
    private CloudRenderer renderer;

    public SimpleCloudBlockRenderer(BlockEntityRendererProvider.Context context) {
        CubeListBuilder builder = CubeListBuilder.create().addBox(4, 4, 4, 8, 8, 8);
        this.cube = builder.getCubes().getFirst().bake(64, 64);
    }

    @Override
    public void render(
            @NotNull SimpleCloudBlockEntity SimpleCloudBlockEntity,
            float partialTick,
            @NotNull PoseStack poseStack,
            @NotNull MultiBufferSource multiBufferSource,
            int combinedLight,
            int combinedOverlay
    ) {
        if (this.renderer == null) {
            this.renderer = ThatSkyInteractions.getInstance().getClient().getCloudRenderer();
        }

        if (SimpleCloudBlockEntity.shouldRecompile()) {
            Vector3i size0 = SimpleCloudBlockEntity.getSize();
            Vector3i offset = SimpleCloudBlockEntity.getOffset();
            BlockPos pos = SimpleCloudBlockEntity.getBlockPos();
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
            this.renderer.addCloud(SimpleCloudBlockEntity, new CloudData(position, size, 0));
            SimpleCloudBlockEntity.setShouldRecompile(false);
        }

        LocalPlayer player = Minecraft.getInstance().player;

        if (player == null) {
            return;
        }

        Item item = player.getItemInHand(InteractionHand.MAIN_HAND).getItem();
        boolean simple = item == Items.SIMPLE_CLOUD.get();
        boolean expand = item == Items.CLOUD_EXPAND.get();
        boolean reduce = item == Items.CLOUD_REDUCE.get();
        if (simple || expand || reduce) {
            poseStack.pushPose();

            int color = 0xffffffff;
            if (expand) {
                color = 0xff00ff00;
            } else if (reduce) {
                color = 0xffff0000;
            }

            VertexConsumer vertexConsumer = RenderTypes.getBufferSource().getBuffer(RenderTypes.BLOOM.apply(RenderTypes.TEXTURE, false));
            this.cube.compile(poseStack.last(), vertexConsumer, combinedLight, combinedOverlay, color);
            //highlight.renderToBuffer(poseStack, vertexConsumer, combinedLight, combinedOverlay);

            PostEffects.setApplyBloom();
            poseStack.popPose();
        }
    }

    @Override
    public int getViewDistance() {
        return 96;
    }

    @Override
    public boolean shouldRender(@NotNull SimpleCloudBlockEntity blockEntity, @NotNull Vec3 cameraPos) {
        boolean render = BlockEntityRenderer.super.shouldRender(blockEntity, cameraPos);
        if (!render) {
            if (this.renderer == null) {
                this.renderer = ThatSkyInteractions.getInstance().getClient().getCloudRenderer();
            }

            blockEntity.setShouldRecompile(true);
            this.renderer.removeCloud(blockEntity);
        }
        return render;
    }

    @Override
    public boolean shouldRenderOffScreen(@NotNull SimpleCloudBlockEntity blockEntity) {
        return true;
    }
}
