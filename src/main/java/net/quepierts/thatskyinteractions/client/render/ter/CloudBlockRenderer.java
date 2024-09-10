package net.quepierts.thatskyinteractions.client.render.ter;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.util.FastColor;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.block.entity.AbstractCloudBlockEntity;
import net.quepierts.thatskyinteractions.client.render.cloud.CloudData;
import net.quepierts.thatskyinteractions.client.render.cloud.CloudRenderer;
import net.quepierts.thatskyinteractions.item.ICloudHighlight;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import org.joml.Vector3i;

@OnlyIn(Dist.CLIENT)
public class CloudBlockRenderer extends HighlightBlockEntityRenderer<AbstractCloudBlockEntity> {
    private final CloudRenderer cloudRenderer;
    private final boolean colored;

    @SuppressWarnings("unused")
    public static CloudBlockRenderer simple(BlockEntityRendererProvider.Context context) {
        return new CloudBlockRenderer(false);
    }

    @SuppressWarnings("unused")
    public static CloudBlockRenderer colored(BlockEntityRendererProvider.Context context) {
        return new CloudBlockRenderer(true);
    }

    public CloudBlockRenderer(boolean colored) {
        this.cloudRenderer = ThatSkyInteractions.getInstance().getClient().getCloudRenderer();
        this.colored = colored;
    }

    @Override
    public void render(
            @NotNull AbstractCloudBlockEntity cloud,
            float partialTick,
            @NotNull PoseStack poseStack,
            @NotNull MultiBufferSource multiBufferSource,
            int combinedLight,
            int combinedOverlay
    ) {
        if (cloud.shouldRecompile()) {
            Vector3i size0 = cloud.getSize();
            Vector3i offset = cloud.getOffset();
            BlockPos pos = cloud.getBlockPos();
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
            if (this.colored) {
                int color0 = cloud.getColor();
                Vector3f color = new Vector3f(
                        FastColor.ARGB32.red(color0),
                        FastColor.ARGB32.green(color0),
                        FastColor.ARGB32.blue(color0)
                );
                this.cloudRenderer.addCloud(cloud, new CloudData(position, size, color, 0));
            } else {
                this.cloudRenderer.addCloud(cloud, new CloudData(position, size, 0));
            }
            cloud.setShouldRecompile(false);
        }

        LocalPlayer player = Minecraft.getInstance().player;

        if (player == null || player.isSpectator()) {
            return;
        }

        ItemStack itemStack = player.getItemInHand(InteractionHand.MAIN_HAND);
        Item item = itemStack.getItem();

        if (item instanceof ICloudHighlight highlight) {
            int color = highlight.color(itemStack, cloud);
            this.renderHighLight(poseStack, color, combinedLight, combinedOverlay);
        }
    }

    @Override
    public int getViewDistance() {
        return 96;
    }

    @Override
    public boolean shouldRender(@NotNull AbstractCloudBlockEntity blockEntity, @NotNull Vec3 cameraPos) {
        boolean render = super.shouldRender(blockEntity, cameraPos);
        if (!render) {
            blockEntity.setShouldRecompile(true);
            this.cloudRenderer.removeCloud(blockEntity);
        }
        return render;
    }

    @Override
    public boolean shouldRenderOffScreen(@NotNull AbstractCloudBlockEntity blockEntity) {
        return true;
    }
}
