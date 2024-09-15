package net.quepierts.thatskyinteractions.client.render.section;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.FaceBakery;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.ClientHooks;
import net.neoforged.neoforge.client.extensions.IBakedModelExtension;
import net.neoforged.neoforge.client.model.BakedModelWrapper;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.List;

@OnlyIn(Dist.CLIENT)
public class TransformedBakedModelWrapper extends BakedModelWrapper<BakedModel> implements IBakedModelExtension {
    private final Matrix4f transformation;
    private final Vector3f temp;
    public TransformedBakedModelWrapper(BakedModel originalModel, Matrix4f transformation) {
        super(originalModel);
        this.transformation = transformation;
        this.temp = new Vector3f();
    }

    @NotNull
    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @NotNull RandomSource rand, @NotNull ModelData extraData, @Nullable RenderType renderType) {
        return this.originalModel.getQuads(state, side, rand, extraData, renderType)
                .stream()
                .map(this::applyTransform)
                .toList();
    }

    @Deprecated
    @NotNull
    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @NotNull RandomSource rand) {
        return this.originalModel.getQuads(state, side, rand)
                .stream()
                .map(this::applyTransform)
                .toList();
    }

    private BakedQuad applyTransform(BakedQuad quad) {
        int[] origin = quad.getVertices();
        int[] transformed = new int[origin.length];
        System.arraycopy(origin, 0, transformed, 0, origin.length);

        for (int i = 0; i < transformed.length / 8; i++) {
            float x = Float.intBitsToFloat(origin[i * 8]);
            float y = Float.intBitsToFloat(origin[i * 8 + 1]);
            float z = Float.intBitsToFloat(origin[i * 8 + 2]);

            this.transformation.transformPosition(x, y, z, this.temp);

            transformed[i * 8] = Float.floatToRawIntBits(this.temp.x);
            transformed[i * 8 + 1] = Float.floatToRawIntBits(this.temp.y);
            transformed[i * 8 + 2] = Float.floatToRawIntBits(this.temp.z);
        }

        Direction direction = FaceBakery.calculateFacing(transformed);
        ClientHooks.fillNormal(transformed, direction);
        return new BakedQuad(
                transformed,
                quad.getTintIndex(),
                direction,
                quad.getSprite(),
                quad.hasAmbientOcclusion()
        );
    }
}
