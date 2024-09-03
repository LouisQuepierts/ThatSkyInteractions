package net.quepierts.thatskyinteractions.client.render.cloud;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexBuffer;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.quepierts.thatskyinteractions.block.ICloud;
import net.quepierts.thatskyinteractions.client.registry.PostEffects;
import net.quepierts.thatskyinteractions.client.registry.Shaders;
import net.quepierts.thatskyinteractions.client.util.RenderUtils;
import org.joml.Matrix4f;

import java.util.Map;

@SuppressWarnings("unused")
@OnlyIn(Dist.CLIENT)
public class CloudRenderer {
    public static final int CULL_XP = 1;
    public static final int CULL_XN = 2;
    public static final int CULL_YP = 4;
    public static final int CULL_YN = 8;
    public static final int CULL_ZP = 16;
    public static final int CULL_ZN = 32;
    public static final int INVISIBLE = 63;

    public static final int SINGLE_CLOUD_SIZE = 4;
    private final Map<ICloud, ObjectList<CloudData>> clouds = new Object2ObjectOpenHashMap<>();
    private VertexBuffer buffer;
    private boolean rebuildClouds;
    private ClientLevel level;

    public CloudRenderer() {
    }

    public void onClientTick(final ClientTickEvent.Post event) {
        if (clouds.isEmpty()) {
            return;
        }

        clouds.entrySet().removeIf(next -> next.getKey().isRemoved());
    }

    public void addCloud(ICloud iCloud, CloudData data) {
        ObjectList<CloudData> list = this.clouds.computeIfAbsent(iCloud, o -> new ObjectArrayList<>());
        list.clear();
        data.split(list);
        this.rebuildClouds = true;
    }

    public void removeCloud(ICloud iCloud) {
        this.clouds.remove(iCloud);
        this.rebuildClouds = true;
    }

    public void setLevel(ClientLevel level) {
        this.level = level;
        this.rebuildClouds = false;
        //this.debug();
    }

    public void renderClouds(PoseStack poseStack, Matrix4f frustumMatrix, Matrix4f projectionMatrix, float partialTick, Vec3 cameraPosition) {
        double dx = cameraPosition.x;
        double dy = cameraPosition.y;
        double dz = cameraPosition.z;
        float fx = (float) dx;
        float fy = (float)(dy);
        float fz = (float)(dz);
        Vec3 cloudColor = this.level.getCloudColor(partialTick);

        if (this.rebuildClouds) {
            this.rebuildClouds = false;
            if (this.buffer != null) {
                this.buffer.close();
            }

            MeshData meshData = this.buildClouds(Tesselator.getInstance(), dx, dy, dz, cloudColor);
            if (meshData != null) {
                this.buffer = new VertexBuffer(VertexBuffer.Usage.STATIC);
                this.buffer.bind();
                this.buffer.upload(meshData);
                VertexBuffer.unbind();
            } else {
                this.buffer = null;
            }
        }

        if (this.buffer != null) {
            RenderTarget target = PostEffects.getCloudTarget();
            RenderTarget mainRenderTarget = Minecraft.getInstance().getMainRenderTarget();

            final int width = target.viewWidth;
            final int height = target.viewHeight;

            target.clear(Minecraft.ON_OSX);
            RenderUtils.blitDepth(mainRenderTarget, target, width, height);

            if (this.buffer != null) {
                poseStack.pushPose();
                poseStack.mulPose(frustumMatrix);

                RenderSystem.disableCull();
                RenderSystem.enableDepthTest();
                ShaderInstance shaderinstance = Shaders.getCloudShader();
                RenderSystem.setupShaderLights(shaderinstance);
                FogRenderer.levelFogColor();
                if (shaderinstance.GAME_TIME != null) {
                    shaderinstance.GAME_TIME.set(RenderSystem.getShaderGameTime());
                }

                if (shaderinstance.CHUNK_OFFSET != null) {
                    shaderinstance.CHUNK_OFFSET.set(
                            -fx,
                            -fy,
                            -fz
                    );
                }

                this.buffer.bind();
                this.buffer.drawWithShader(poseStack.last().pose(), projectionMatrix, shaderinstance);
                PostChain cloudEffect = PostEffects.getCloudEffect();
                target.bindWrite(false);
                this.buffer.drawWithShader(poseStack.last().pose(), projectionMatrix, shaderinstance);
                cloudEffect.process(partialTick);

                RenderSystem.enableCull();
                RenderSystem.disableDepthTest();

                poseStack.popPose();
            }

            mainRenderTarget.bindWrite(false);
            RenderSystem.setShaderColor(1, 1, 1, 1);

            RenderUtils.bloomBlit(target, mainRenderTarget, width, height, 1.0f);
            //target.blitToScreen(width, height);
        }
    }

    public void postRender(PoseStack poseStack, Matrix4f frustumMatrix, Matrix4f projectionMatrix, float partialTick, Vec3 cameraPosition) {

    }

    private MeshData buildClouds(Tesselator tesselator, double x, double y, double z, Vec3 cloudColor) {
        CloudMeshBuilder builder = new CloudMeshBuilder(tesselator, 0, 0, 0, cloudColor);
        return builder.build(this.clouds.values());
    }

    public void reset() {
        if (this.buffer != null) {
            this.buffer.close();
            this.buffer = null;
        }

        this.clouds.clear();
        this.rebuildClouds = true;
    }

}
