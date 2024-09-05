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
    private final Map<ICloud, ObjectList<CloudData>> normalClouds = new Object2ObjectOpenHashMap<>();
    private final Map<ICloud, ObjectList<CloudData>> colorClouds = new Object2ObjectOpenHashMap<>();
    private VertexBuffer normalBuffer;
    private VertexBuffer coloredBuffer;
    private boolean rebuildNormalClouds;
    private boolean rebuildColoredClouds;
    private ClientLevel level;

    public CloudRenderer() {
    }

    public void onClientTick(final ClientTickEvent.Post event) {
        if (normalClouds.isEmpty()) {
            return;
        }

        normalClouds.entrySet().removeIf(next -> next.getKey().isRemoved());
    }

    public void addCloud(ICloud iCloud, CloudData data) {
        ObjectList<CloudData> list = this.normalClouds.computeIfAbsent(iCloud, o -> new ObjectArrayList<>());
        list.clear();
        data.split(list);
        this.rebuildNormalClouds = true;
    }

    public void addColoredCloud(ICloud iCloud, CloudData data) {
        ObjectList<CloudData> list = this.colorClouds.computeIfAbsent(iCloud, o -> new ObjectArrayList<>());
        list.clear();
        data.split(list);
        this.rebuildColoredClouds = true;
    }

    public void removeCloud(ICloud iCloud) {
        if (this.normalClouds.containsKey(iCloud)) {
            this.normalClouds.remove(iCloud);
            this.rebuildNormalClouds = true;
        } else if (this.colorClouds.containsKey(iCloud)) {
            this.colorClouds.remove(iCloud);
            this.rebuildColoredClouds = true;
        }
    }

    public void removeColoredCloud(ICloud iCloud) {
        if (this.colorClouds.containsKey(iCloud)) {
            this.colorClouds.remove(iCloud);
            this.rebuildColoredClouds = true;
        }
    }

    public void setLevel(ClientLevel level) {
        this.level = level;
        this.rebuildNormalClouds = false;
        this.rebuildColoredClouds = false;
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

        if (this.rebuildNormalClouds) {
            this.rebuildNormalClouds = false;
            if (this.normalBuffer != null) {
                this.normalBuffer.close();
            }

            MeshData meshData = this.buildClouds(Tesselator.getInstance(), dx, dy, dz, cloudColor);
            if (meshData != null) {
                this.normalBuffer = new VertexBuffer(VertexBuffer.Usage.STATIC);
                this.normalBuffer.bind();
                this.normalBuffer.upload(meshData);
                VertexBuffer.unbind();
            } else {
                this.normalBuffer = null;
            }
        }

        if (this.rebuildColoredClouds) {
            this.rebuildColoredClouds = false;
            if (this.coloredBuffer != null) {
                this.coloredBuffer.close();
            }

            MeshData meshData = this.buildColoredClouds(Tesselator.getInstance(), cloudColor);
            if (meshData != null) {
                this.coloredBuffer = new VertexBuffer(VertexBuffer.Usage.STATIC);
                this.coloredBuffer.bind();
                this.coloredBuffer.upload(meshData);
                VertexBuffer.unbind();
            } else {
                this.coloredBuffer = null;
            }
        }

        if (this.normalBuffer != null) {
            RenderTarget target = PostEffects.getCloudTarget();
            RenderTarget mainRenderTarget = Minecraft.getInstance().getMainRenderTarget();

            final int width = target.viewWidth;
            final int height = target.viewHeight;

            target.clear(Minecraft.ON_OSX);
            RenderUtils.blitDepth(mainRenderTarget, target, width, height);

            if (this.normalBuffer != null || this.coloredBuffer != null) {
                poseStack.pushPose();
                poseStack.mulPose(frustumMatrix);

                PostChain cloudEffect = PostEffects.getCloudEffect();

                RenderSystem.disableCull();
                RenderSystem.enableDepthTest();

                target.bindWrite(false);

                if (this.normalBuffer != null) {
                    ShaderInstance cloudShader = Shaders.getCloudShader();
                    RenderSystem.setupShaderLights(cloudShader);
                    FogRenderer.levelFogColor();
                    if (cloudShader.GAME_TIME != null) {
                        cloudShader.GAME_TIME.set(RenderSystem.getShaderGameTime());
                    }


                    if (cloudShader.CHUNK_OFFSET != null) {
                        cloudShader.CHUNK_OFFSET.set(
                                -fx,
                                -fy,
                                -fz
                        );
                    }

                    this.normalBuffer.bind();
                    this.normalBuffer.drawWithShader(poseStack.last().pose(), projectionMatrix, cloudShader);
                }

                if (this.coloredBuffer != null) {
                    ShaderInstance coloredCloudShader = Shaders.getColoredCloudShader();
                    RenderSystem.setupShaderLights(coloredCloudShader);
                    if (coloredCloudShader.GAME_TIME != null) {
                        coloredCloudShader.GAME_TIME.set(RenderSystem.getShaderGameTime());
                    }


                    if (coloredCloudShader.CHUNK_OFFSET != null) {
                        coloredCloudShader.CHUNK_OFFSET.set(
                                -fx,
                                -fy,
                                -fz
                        );
                    }

                    this.coloredBuffer.bind();
                    this.coloredBuffer.drawWithShader(poseStack.last().pose(), projectionMatrix, coloredCloudShader);
                }

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

    private MeshData buildClouds(Tesselator tesselator, double x, double y, double z, Vec3 cloudColor) {
        CloudMeshBuilder builder = new CloudMeshBuilder(tesselator, 0, 0, 0, cloudColor);
        return builder.build(this.normalClouds.values());
    }

    private MeshData buildColoredClouds(Tesselator tesselator, Vec3 cloudColor) {
        CloudMeshBuilder builder = new CloudMeshBuilder(tesselator, 0, 0, 0, cloudColor);
        return builder.build(this.colorClouds.values());
    }

    public void reset() {
        if (this.normalBuffer != null) {
            this.normalBuffer.close();
            this.normalBuffer = null;
        }

        if (this.coloredBuffer != null) {
            this.coloredBuffer.close();
            this.coloredBuffer = null;
        }

        this.normalClouds.clear();
        this.colorClouds.clear();
    }

}
