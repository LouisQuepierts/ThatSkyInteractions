package net.quepierts.thatskyinteractions.client.render.pipeline;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.CubeDefinition;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.RandomSource;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@OnlyIn(Dist.CLIENT)
public class VertexBufferManager implements PreparableReloadListener {
    public static final ModelResourceLocation QUAD = ThatSkyInteractions.getModelLocation("quad");
//    public static final ResourceLocation GRID = ThatSkyInteractions.getLocation("grid");
    public static final ModelResourceLocation CUBE = ThatSkyInteractions.getModelLocation("cube");
    public static final ModelResourceLocation BODY = ThatSkyInteractions.getModelLocation("body");
    public static final ModelResourceLocation HEAD = ThatSkyInteractions.getModelLocation("head");


    private static final RandomSource RAND = RandomSource.create(42L);
    private static final PoseStack ZERO = new PoseStack();
    private final Map<ModelResourceLocation, VertexBuffer> buffers;

    private boolean shouldReload = false;

    public VertexBufferManager() {
        this.buffers = new Object2ObjectOpenHashMap<>();
    }

    public void upload(@NotNull ModelResourceLocation location, @NotNull MeshData meshData) {
        if (!RenderSystem.isOnRenderThread()) {
            RenderSystem.recordRenderCall(() -> {
                _upload(location, meshData);
            });
        } else {
            _upload(location, meshData);
        }
    }

    private void _upload(@NotNull ModelResourceLocation location, @NotNull MeshData meshData) {
        if (this.buffers.containsKey(location)) {
            VertexBuffer buffer = this.buffers.get(location);
            buffer.close();
        }
        VertexBuffer buffer = new VertexBuffer(VertexBuffer.Usage.STATIC);
        buffer.bind();
        buffer.upload(meshData);
        VertexBuffer.unbind();
        this.buffers.put(location, buffer);
    }

    public void tick() {
        if (this.shouldReload) {
            this.reload();
            this.shouldReload = false;
        }
    }

    @Nullable
    public VertexBuffer get(@NotNull ModelResourceLocation location) {
        return this.buffers.get(location);
    }

    public void cleanup() {
        for (VertexBuffer buffer : this.buffers.values()) {
            buffer.close();
        }

        this.buffers.clear();
    }

    public boolean contains(ModelResourceLocation meshLocation) {
        return this.buffers.containsKey(meshLocation);
    }

    public static MeshData modelPart2Mesh(@NotNull ModelPart part) {
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferBuilder = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR_NORMAL);
        part.render(ZERO, bufferBuilder, 15728880, OverlayTexture.NO_OVERLAY);
        return bufferBuilder.build();
    }

    public static MeshData cube2Mesh(@NotNull ModelPart.Cube cube) {
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferBuilder = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR_NORMAL);
        cube.compile(ZERO.last(), bufferBuilder, 15728880, OverlayTexture.NO_OVERLAY, 0xffffffff);
        return bufferBuilder.build();
    }

    public static MeshData blockModel2Mesh(@NotNull BlockState state) {
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferBuilder = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR_NORMAL);
        BakedModel model = Minecraft.getInstance().getBlockRenderer().getBlockModel(state);

        List<BakedQuad> quads = model.getQuads(state, null, RAND, ModelData.EMPTY, null);
        for (BakedQuad quad : quads) {
            bufferBuilder.putBulkData(ZERO.last(), quad, 1.0f, 1.0f, 1.0f, 1.0f, 15728880, OverlayTexture.NO_OVERLAY);
        }
        return bufferBuilder.build();
    }

    private void initBuiltin() {
        final Tesselator tesselator = Tesselator.getInstance();
        final BufferBuilder builder = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR_NORMAL);
        builder.addVertex(-0.5f, -0.5f, 0).setColor(0xffffffff).setUv(0, 1).setNormal(0, 0, 1);
        builder.addVertex(-0.5f, 0.5f, 0).setColor(0xffffffff).setUv(0, 0).setNormal(0, 0, 1);
        builder.addVertex(0.5f, 0.5f, 0).setColor(0xffffffff).setUv(1, 0).setNormal(0, 0, 1);
        builder.addVertex(0.5f, -0.5f, 0).setColor(0xffffffff).setUv(1, 1).setNormal(0, 0, 1);
        this.upload(QUAD, Objects.requireNonNull(builder.build()));

        /*final BufferBuilder gridBuilder = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR_NORMAL);
        final int diff = 100;
        for (int i = 0; i < diff; i++) {
            float z = (float) i / diff - 0.5f;
            gridBuilder.addVertex(-0.5f, -0.5f, z).setColor(0xffffffff).setUv(0, 1).setNormal(0, 0, 1);
            gridBuilder.addVertex(-0.5f, 0.5f, z).setColor(0xffffffff).setUv(0, 0).setNormal(0, 0, 1);
            gridBuilder.addVertex(0.5f, 0.5f, z).setColor(0xffffffff).setUv(1, 0).setNormal(0, 0, 1);
            gridBuilder.addVertex(0.5f, -0.5f, z).setColor(0xffffffff).setUv(1, 1).setNormal(0, 0, 1);
        }
        this.upload(GRID, Objects.requireNonNull(gridBuilder.build()));*/

        final CubeListBuilder cubeListBuilder = CubeListBuilder.create().addBox(4, 4, 4, 8, 8, 8);
        final ModelPart.Cube cube = cubeListBuilder.getCubes().getFirst().bake(64, 64);
        final BufferBuilder cubeBuilder = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR_NORMAL);
        cube.compile(ZERO.last(), cubeBuilder, 15728880, OverlayTexture.NO_OVERLAY, 0xffffffff);
        this.upload(CUBE, Objects.requireNonNull(cubeBuilder.build()));

        this.buildBody(tesselator);
        this.buildHead(tesselator);

        ThatSkyInteractions.getInstance().getClient().onUploadVertexBuffers(this);
    }

    private void buildBody(final Tesselator tesselator) {
        final CubeListBuilder builder = CubeListBuilder.create();
        builder.texOffs(16, 16).addBox(-4.0F, 12.0F, -2.0F, 8.0F, 12.0F, 4.0F);
        builder.texOffs(40, 16).addBox(-8.0F, 12.0F, -2.0F, 4.0F, 12.0F, 4.0F);
        builder.texOffs(40, 16).addBox(4.0F, 12.0F, -2.0F, 4.0F, 12.0F, 4.0F);
        builder.texOffs(0, 16).addBox(-4.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F);
        builder.texOffs(0, 16).addBox( 0.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F);

        final BufferBuilder cubeBuilder = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR_NORMAL);
        for (CubeDefinition definition : builder.getCubes()) {
            definition.bake(64, 64).compile(ZERO.last(), cubeBuilder, 15728880, OverlayTexture.NO_OVERLAY, 0xffffffff);
        }
        this.upload(BODY, Objects.requireNonNull(cubeBuilder.build()));
    }

    private void buildHead(final Tesselator tesselator) {
        final CubeListBuilder builder = CubeListBuilder.create();
        builder.addBox(-4.0F, 0.0F, -4.0F, 8.0F, 8.0F, 8.0F);

        final BufferBuilder cubeBuilder = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR_NORMAL);
        for (CubeDefinition definition : builder.getCubes()) {
            definition.bake(64, 64).compile(ZERO.last(), cubeBuilder, 15728880, OverlayTexture.NO_OVERLAY, 0xffffffff);
        }
        this.upload(HEAD, Objects.requireNonNull(cubeBuilder.build()));
    }

    @NotNull
    @Override
    public CompletableFuture<Void> reload(@NotNull PreparationBarrier preparationBarrier, @NotNull ResourceManager resourceManager, @NotNull ProfilerFiller profilerFiller, @NotNull ProfilerFiller profilerFiller1, @NotNull Executor executor, @NotNull Executor executor1) {
        this.setReload();
        return CompletableFuture.completedFuture(null);
    }

    public void setReload() {
        this.shouldReload = true;
    }

    private void reload() {
        this.cleanup();
        this.initBuiltin();
    }
}
