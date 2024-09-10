package net.quepierts.thatskyinteractions.client.render.cloud;

import com.google.gson.JsonSyntaxException;
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
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceProvider;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.block.ICloud;
import net.quepierts.thatskyinteractions.client.registry.Shaders;
import net.quepierts.thatskyinteractions.client.util.RenderUtils;
import org.joml.Matrix4f;

import java.io.IOException;
import java.util.Map;

@SuppressWarnings("unused")
@OnlyIn(Dist.CLIENT)
public class CloudRenderer {
    public static final ResourceLocation CLOUD_EFFECT_LOCATION = ThatSkyInteractions.getLocation("shaders/post/cloud.json");

    public static final int CULL_XP = 1;
    public static final int CULL_XN = 2;
    public static final int CULL_YP = 4;
    public static final int CULL_YN = 8;
    public static final int CULL_ZP = 16;
    public static final int CULL_ZN = 32;
    public static final int INVISIBLE = 63;

    public static final int SINGLE_CLOUD_SIZE = 2;
    private final Map<ICloud, ObjectList<CloudData>> normalClouds = new Object2ObjectOpenHashMap<>();
    private VertexBuffer normalBuffer;
    private boolean rebuildNormalClouds;
    private ClientLevel level;

    private PostChain effect;
    private RenderTarget finalTarget;
    private RenderTarget maskTarget;
//    private RenderTarget originTarget;
//    private RenderTarget depthTarget;

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

    public void removeCloud(ICloud iCloud) {
        if (this.normalClouds.containsKey(iCloud)) {
            this.normalClouds.remove(iCloud);
            this.rebuildNormalClouds = true;
        }
    }

    public void setLevel(ClientLevel level) {
        this.level = level;
        this.rebuildNormalClouds = false;
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

        if (this.normalBuffer != null) {
            RenderTarget mainRenderTarget = Minecraft.getInstance().getMainRenderTarget();

            final int width = this.finalTarget.viewWidth;
            final int height = this.finalTarget.viewHeight;

            this.finalTarget.clear(Minecraft.ON_OSX);
            RenderUtils.blitDepth(mainRenderTarget, this.finalTarget, width, height);

            if (this.normalBuffer != null) {
                poseStack.pushPose();
                poseStack.mulPose(frustumMatrix);

                RenderSystem.disableCull();
                RenderSystem.enableDepthTest();

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

                    this.finalTarget.bindWrite(false);
                    this.normalBuffer.drawWithShader(poseStack.last().pose(), projectionMatrix, cloudShader);
                }

                RenderSystem.enableCull();
                RenderSystem.disableDepthTest();

//                this.depthTarget.bindWrite(false);
//                RenderUtils.blitDepthToScreen(this.finalTarget, width, height);

                this.effect.process(partialTick);

                poseStack.popPose();
            }

            mainRenderTarget.bindWrite(false);
//            RenderUtils.blitDepthToScreen(width, height);
            RenderUtils.bloomBlit(this.finalTarget, mainRenderTarget, width, height, 1.0f);
//            this.finalTarget.blitToScreen(width, height);
//            this.originTarget.blitToScreen(width, height);
        }
    }

    private MeshData buildClouds(Tesselator tesselator, double x, double y, double z, Vec3 cloudColor) {
        CloudMeshBuilder builder = new CloudMeshBuilder(tesselator, 0, 0, 0, cloudColor);
        return builder.build(this.normalClouds.values());
    }

    public void setup(ResourceProvider provider) {
        Minecraft minecraft = Minecraft.getInstance();
        TextureManager textureManager = minecraft.getTextureManager();

        int width = minecraft.getWindow().getWidth();
        int height = minecraft.getWindow().getHeight();

        if (this.effect != null) {
            this.effect.close();
        }

        try {
            this.effect = new PostChain(
                    textureManager, provider,
                    minecraft.getMainRenderTarget(),
                    CLOUD_EFFECT_LOCATION
            );
            this.effect.resize(width, height);
            this.finalTarget = this.effect.getTempTarget("final");
            this.maskTarget = this.effect.getTempTarget("mask");
//            this.depthTarget = this.effect.getTempTarget("depth");
//            this.originTarget = this.effect.getTempTarget("origin");
        }catch (IOException ioexception) {
            ThatSkyInteractions.LOGGER.warn("Failed to load shader: {}", CLOUD_EFFECT_LOCATION, ioexception);
        } catch (JsonSyntaxException jsonsyntaxexception) {
            ThatSkyInteractions.LOGGER.warn("Failed to parse shader: {}", CLOUD_EFFECT_LOCATION, jsonsyntaxexception);
        }
    }

    public void resize(int width, int height) {
        if (this.effect != null) {
            this.effect.resize(width, height);
        }
    }

    public void reset() {
        if (this.normalBuffer != null) {
            this.normalBuffer.close();
            this.normalBuffer = null;
        }

        this.normalClouds.clear();
    }

    public RenderTarget getFinalTarget() {
        return this.finalTarget;
    }
}
