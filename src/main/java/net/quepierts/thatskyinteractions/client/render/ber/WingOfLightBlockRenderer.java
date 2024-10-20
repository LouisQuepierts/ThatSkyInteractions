package net.quepierts.thatskyinteractions.client.render.ber;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.block.entity.WingOfLightBlockEntity;
import net.quepierts.thatskyinteractions.client.gui.layer.World2ScreenWidgetLayer;
import net.quepierts.thatskyinteractions.client.registry.RenderTypes;
import net.quepierts.thatskyinteractions.client.render.bloom.BloomRenderer;
import net.quepierts.thatskyinteractions.client.render.pipeline.VertexBufferManager;
import net.quepierts.thatskyinteractions.data.TSIUserData;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

@OnlyIn(Dist.CLIENT)
public class WingOfLightBlockRenderer implements BlockEntityRenderer<WingOfLightBlockEntity> {
    private final Minecraft minecraft;
    private final BloomRenderer renderer;

    public WingOfLightBlockRenderer(BlockEntityRendererProvider.Context context) {
        this.minecraft = Minecraft.getInstance();
        this.renderer = ThatSkyInteractions.getInstance().getClient().getBloomRenderer();
    }

    @Override
    public void render(
            @NotNull WingOfLightBlockEntity wingOfLightBlockEntity,
            float partialTick,
            @NotNull PoseStack poseStack,
            @NotNull MultiBufferSource multiBufferSource,
            int combinedLight,
            int combinedOverlay
    ) {
        TSIUserData userData = ThatSkyInteractions.getInstance().getClient().getCache().getUserData();

        if (userData == null || userData.isPickedUp(wingOfLightBlockEntity)) {
            return;
        }

        BlockPos pos = wingOfLightBlockEntity.getBlockPos();

        if (this.minecraft.player == null)
            return;
        float distanceSqr = (float) this.minecraft.player.distanceToSqr(pos.getX(), pos.getY(), pos.getZ());

        World2ScreenWidgetLayer.INSTANCE.addWorldPositionObject(wingOfLightBlockEntity.getUUID(), wingOfLightBlockEntity.provideW2SWidget(distanceSqr));

        Matrix4f transformation = new Matrix4f();
        transformation.translate(
                pos.getX() + 0.5f,
                pos.getY(),
                pos.getZ() + 0.5f)
                .scale(0.95f)
                .rotateY(-wingOfLightBlockEntity.getYRot());

        this.renderer.batchRender(
                VertexBufferManager.BODY,
                transformation,
                RenderTypes.TEXTURE
        );

        transformation.translate(0, 1.5f, 0)
                .rotateX(-wingOfLightBlockEntity.getXRot());

        this.renderer.batchRender(
                VertexBufferManager.HEAD,
                transformation,
                RenderTypes.TEXTURE
        );
    }

    @NotNull
    @Override
    public AABB getRenderBoundingBox(WingOfLightBlockEntity blockEntity) {
        BlockPos pos = blockEntity.getBlockPos();
        return new AABB(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 2, pos.getZ() + 1);
    }
}
