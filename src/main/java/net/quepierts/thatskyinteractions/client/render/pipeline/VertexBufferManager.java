package net.quepierts.thatskyinteractions.client.render.pipeline;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@OnlyIn(Dist.CLIENT)
public class VertexBufferManager implements PreparableReloadListener {
    public static final ResourceLocation QUAD = ThatSkyInteractions.getLocation("quad");
    public static final ResourceLocation CUBE = ThatSkyInteractions.getLocation("cube");

    private static final PoseStack ZERO = new PoseStack();
    private final Map<ResourceLocation, VertexBuffer> buffers;

    private boolean shouldReload = false;

    public VertexBufferManager() {
        this.buffers = new Object2ObjectOpenHashMap<>();
    }

    public void upload(@NotNull ResourceLocation location, @NotNull MeshData meshData) {
        if (!RenderSystem.isOnRenderThread()) {
            RenderSystem.recordRenderCall(() -> {
                _upload(location, meshData);
            });
        } else {
            _upload(location, meshData);
        }
    }

    private void _upload(@NotNull ResourceLocation location, @NotNull MeshData meshData) {
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
    public VertexBuffer get(@NotNull ResourceLocation location) {
        return this.buffers.get(location);
    }

    public void cleanup() {
        for (VertexBuffer buffer : this.buffers.values()) {
            buffer.close();
        }

        this.buffers.clear();
    }

    public boolean contains(ResourceLocation meshLocation) {
        return this.buffers.containsKey(meshLocation);
    }

    public static MeshData modelPart2Mesh(ModelPart part) {
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferBuilder = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR_NORMAL);
        part.render(ZERO, bufferBuilder, 15728880, OverlayTexture.NO_OVERLAY);
        return bufferBuilder.build();
    }

    public static MeshData cube2Mesh(ModelPart.Cube cube) {
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferBuilder = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR_NORMAL);
        cube.compile(ZERO.last(), bufferBuilder, 15728880, OverlayTexture.NO_OVERLAY, 0xffffffff);
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

        final CubeListBuilder cubeListBuilder = CubeListBuilder.create().addBox(4, 4, 4, 8, 8, 8);
        final ModelPart.Cube cube = cubeListBuilder.getCubes().getFirst().bake(64, 64);
        final BufferBuilder cubeBuilder = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR_NORMAL);
        cube.compile(ZERO.last(), cubeBuilder, 15728880, OverlayTexture.NO_OVERLAY, 0xffffffff);
        this.upload(CUBE, Objects.requireNonNull(cubeBuilder.build()));
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
