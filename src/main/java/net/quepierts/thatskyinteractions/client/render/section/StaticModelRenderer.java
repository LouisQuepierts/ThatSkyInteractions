package net.quepierts.thatskyinteractions.client.render.section;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.event.AddSectionGeometryEvent;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.client.model.lighting.LightPipelineAwareModelBlockRenderer;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

@OnlyIn(Dist.CLIENT)
public class StaticModelRenderer {
    private static final RandomSource RANDOM = RandomSource.createNewThreadLocalInstance();

    @NotNull
    private final AddSectionGeometryEvent.SectionRenderingContext context;

    public StaticModelRenderer(
            @NotNull AddSectionGeometryEvent.SectionRenderingContext context
    ) {
        this.context = context;
    }

    public void render(RenderType renderType, BakedModel model, BlockState state, BlockPos blockPos, PoseStack poseStack) {
        LightPipelineAwareModelBlockRenderer.render(
                context.getOrCreateChunkBuffer(renderType),
                context.getQuadLighter(true),
                context.getRegion(),
                model,
                state,
                blockPos,
                poseStack,
                false,
                RANDOM,
                42L,
                OverlayTexture.NO_OVERLAY,
                ModelData.EMPTY,
                renderType
        );
    }

    public void render(
            RenderType renderType,
            BakedModel model,
            BlockState state,
            BlockPos blockPos,
            PoseStack poseStack,
            Matrix4f transformation
    ) {
        BakedModel wrapped = new TransformedBakedModelWrapper(model, transformation);
        this.render(renderType, wrapped, state, blockPos, poseStack);
    }
}
