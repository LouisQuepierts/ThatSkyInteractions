package net.quepierts.thatskyinteractions.client.render.section;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.AddSectionGeometryEvent;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.client.render.ber.StaticBlockEntityRenderer;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(bus = EventBusSubscriber.Bus.GAME, modid = ThatSkyInteractions.MODID, value = Dist.CLIENT)
public class SectionGeometryHandler {
    @SubscribeEvent
    public static void onAddSectionGeometry(final AddSectionGeometryEvent event) {
        BlockPos origin = event.getSectionOrigin().immutable();
        event.addRenderer(new Renderer(origin));
    }

    private static class Renderer implements AddSectionGeometryEvent.AdditionalSectionRenderer {
        private final BlockPos origin;

        private Renderer(BlockPos blockPos) {
            origin = blockPos;
        }

        @Override
        public void render(@NotNull AddSectionGeometryEvent.SectionRenderingContext context) {
            final BlockAndTintGetter region = context.getRegion();
            final BlockEntityRenderDispatcher renderDispatcher = Minecraft.getInstance().getBlockEntityRenderDispatcher();
            final PoseStack poseStack = context.getPoseStack();
            final StaticModelRenderer staticModelRenderer = new StaticModelRenderer(context);

            for (BlockPos pos : BlockPos.betweenClosed(
                    this.origin.getX(), this.origin.getY(), this.origin.getZ(),
                    this.origin.getX() + 15, this.origin.getY() + 15, this.origin.getZ() + 15
            )) {

                BlockEntity entity = region.getBlockEntity(pos);

                if (entity == null) {
                    continue;
                }

                BlockEntityRenderer<BlockEntity> renderer = renderDispatcher.getRenderer(entity);

                if (renderer instanceof StaticBlockEntityRenderer<BlockEntity> staticBlockEntityRenderer) {
                    poseStack.pushPose();
                    poseStack.translate(
                            pos.getX() - this.origin.getX(),
                            pos.getY() - this.origin.getY(),
                            pos.getZ() - this.origin.getZ()
                    );
                    staticBlockEntityRenderer.render(entity, staticModelRenderer, pos, poseStack);
                    poseStack.popPose();
                }
            }
        }
    }
}
