package net.quepierts.thatskyinteractions.client.render.ber;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.client.gui.layer.World2ScreenWidgetLayer;
import net.quepierts.thatskyinteractions.client.reference.RenderTypes;
import net.quepierts.thatskyinteractions.client.render.pipeline.BloomRenderDispatch;
import net.quepierts.thatskyinteractions.client.render.pipeline.VertexBufferManager;
import net.quepierts.thatskyinteractions.common.block.entity.WingOfLightBlockEntity;
import net.quepierts.thatskyinteractions.common.data.attachment.UserDataAttachment;
import net.quepierts.thatskyinteractions.common.data.attachment.component.PickupComponent;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

@OnlyIn(Dist.CLIENT)
public class WingOfLightBlockRenderer implements BlockEntityRenderer<WingOfLightBlockEntity> {
    private final Minecraft minecraft;
    private final BloomRenderDispatch renderer;

    public WingOfLightBlockRenderer(BlockEntityRendererProvider.Context context) {
        this.minecraft = Minecraft.getInstance();
        this.renderer = ThatSkyInteractions.getInstance().getClient().getBloomRenderDispatch();
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
        LocalPlayer player = this.minecraft.player;

        if (player == null) {
            return;
        }

        UserDataAttachment attachment = UserDataAttachment.getAttachment(player);
        PickupComponent pickupComponent = attachment.getPickup();

        boolean picked = pickupComponent.isPickedUp(wingOfLightBlockEntity);
        BlockPos pos = wingOfLightBlockEntity.getBlockPos();

        if (!picked) {
            float distanceSqr = (float) player.distanceToSqr(pos.getX(), pos.getY(), pos.getZ());
            World2ScreenWidgetLayer.INSTANCE.addWorldPositionObject(wingOfLightBlockEntity.getUUID(), wingOfLightBlockEntity.provideW2SWidget(distanceSqr));
        }

        if (wingOfLightBlockEntity.isDirty()) {
            this.renderer.removeRenderAction(wingOfLightBlockEntity);

            if (picked) {
                return;
            }

            Matrix4f transformation = new Matrix4f();
            transformation.translate(
                            pos.getX() + 0.5f,
                            pos.getY(),
                            pos.getZ() + 0.5f)
                    .scale(0.95f)
                    .rotateY(-wingOfLightBlockEntity.getYRot());

            this.renderer.addRenderAction(
                    wingOfLightBlockEntity,
                    VertexBufferManager.BODY,
                    transformation,
                    RenderTypes.TEXTURE,
                    0xFFFFFFFF
            );

            transformation.translate(0, 1.5f, 0)
                    .rotateX(-wingOfLightBlockEntity.getXRot());

            this.renderer.addRenderAction(
                    wingOfLightBlockEntity,
                    VertexBufferManager.HEAD,
                    transformation,
                    RenderTypes.TEXTURE,
                    0xFFFFFFFF
            );

            wingOfLightBlockEntity.setDirty(false);
        }
    }

    @NotNull
    @Override
    public AABB getRenderBoundingBox(WingOfLightBlockEntity blockEntity) {
        BlockPos pos = blockEntity.getBlockPos();
        return new AABB(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 2, pos.getZ() + 1);
    }

    @Override
    public boolean shouldRender(@NotNull WingOfLightBlockEntity blockEntity, @NotNull Vec3 cameraPos) {
        boolean shouldRender = BlockEntityRenderer.super.shouldRender(blockEntity, cameraPos);

        if (!shouldRender) {
            this.renderer.removeRenderAction(blockEntity);
            blockEntity.setDirty(true);
        }

        return shouldRender;
    }

    @Override
    public int getViewDistance() {
        return 128;
    }
}
