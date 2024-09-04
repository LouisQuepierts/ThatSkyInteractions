package net.quepierts.thatskyinteractions.client.render.cloud;

import com.mojang.blaze3d.vertex.*;
import it.unimi.dsi.fastutil.objects.ObjectList;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.Collection;

@OnlyIn(Dist.CLIENT)
class CloudMeshBuilder {
    private final BufferBuilder bufferbuilder;
    private final float x;
    private final float y;
    private final float z;

    private final float red;
    private final float green;
    private final float blue;

    CloudMeshBuilder(Tesselator tesselator, double x, double y, double z, Vec3 cloudColor) {
        this.bufferbuilder = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR_NORMAL);
        this.x = (float) x;
        this.y = (float) y;
        this.z = (float) z;

        this.red = (float) cloudColor.x;
        this.green = (float) cloudColor.y;
        this.blue = (float) cloudColor.z;
    }

    public MeshData build(Collection<ObjectList<CloudData>> clouds) {
        clouds.stream()
                .flatMap(Collection::stream)
                .forEach(this::addCloud);
        return this.bufferbuilder.build();
    }

    private void addCloud(CloudData cloud) {
        float alpha = 1.0f;
        boolean renderXP = (cloud.cullFlag() & CloudRenderer.CULL_XP) == 0;
        boolean renderXN = (cloud.cullFlag() & CloudRenderer.CULL_XN) == 0;
        boolean renderYP = (cloud.cullFlag() & CloudRenderer.CULL_YP) == 0;
        boolean renderYN = (cloud.cullFlag() & CloudRenderer.CULL_YN) == 0;
        boolean renderZP = (cloud.cullFlag() & CloudRenderer.CULL_ZP) == 0;
        boolean renderZN = (cloud.cullFlag() & CloudRenderer.CULL_ZN) == 0;
        
        float x1 = cloud.position().x + 0.03f - x;
        float y1 = cloud.position().y + 0.03f - y;
        float z1 = cloud.position().z + 0.03f - z;
        float x2 = cloud.position().x + cloud.size().x + 0.03f - x;
        float y2 = cloud.position().y + cloud.size().y + 0.03f - y;
        float z2 = cloud.position().z + cloud.size().z + 0.03f - z;

        float red, blue, green;
        if (cloud.color() == CloudData.DEFAULT_COLOR) {
            red = this.red;
            blue = this.blue;
            green = this.green;
        } else {
            red = cloud.color().x;
            blue = cloud.color().y;
            green = cloud.color().z;
        }
        
        if (renderXP) {
            this.bufferbuilder.addVertex(x2, y1, z1).setUv(0, 0).setColor(red, blue, green, alpha).setNormal(1.0f, 0.0f, 0.0f);
            this.bufferbuilder.addVertex(x2, y1, z2).setUv(1, 0).setColor(red, blue, green, alpha).setNormal(1.0f, 0.0f, 0.0f);
            this.bufferbuilder.addVertex(x2, y2, z2).setUv(1, 1).setColor(red, blue, green, alpha).setNormal(1.0f, 0.0f, 0.0f);
            this.bufferbuilder.addVertex(x2, y2, z1).setUv(0, 1).setColor(red, blue, green, alpha).setNormal(1.0f, 0.0f, 0.0f);
        }

        if (renderXN) {
            this.bufferbuilder.addVertex(x1, y1, z2).setUv(0, 0).setColor(red, blue, green, alpha).setNormal(-1.0f, 0.0f, 0.0f);
            this.bufferbuilder.addVertex(x1, y1, z1).setUv(1, 0).setColor(red, blue, green, alpha).setNormal(-1.0f, 0.0f, 0.0f);
            this.bufferbuilder.addVertex(x1, y2, z1).setUv(1, 1).setColor(red, blue, green, alpha).setNormal(-1.0f, 0.0f, 0.0f);
            this.bufferbuilder.addVertex(x1, y2, z2).setUv(0, 1).setColor(red, blue, green, alpha).setNormal(-1.0f, 0.0f, 0.0f);
        }

        if (renderYP) {
            this.bufferbuilder.addVertex(x1, y2, z1).setUv(0, 0).setColor(red, green, blue, alpha).setNormal(0.0f, 1.0f, 0.0f);
            this.bufferbuilder.addVertex(x2, y2, z1).setUv(1, 0).setColor(red, green, blue, alpha).setNormal(0.0f, 1.0f, 0.0f);
            this.bufferbuilder.addVertex(x2, y2, z2).setUv(1, 1).setColor(red, green, blue, alpha).setNormal(0.0f, 1.0f, 0.0f);
            this.bufferbuilder.addVertex(x1, y2, z2).setUv(0, 1).setColor(red, green, blue, alpha).setNormal(0.0f, 1.0f, 0.0f);
        }

        if (renderYN) {
            this.bufferbuilder.addVertex(x1, y1, z1).setUv(0, 0).setColor(red, green, blue, alpha).setNormal(0.0f, -1.0f, 0.0f);
            this.bufferbuilder.addVertex(x1, y1, z2).setUv(1, 0).setColor(red, green, blue, alpha).setNormal(0.0f, -1.0f, 0.0f);
            this.bufferbuilder.addVertex(x2, y1, z2).setUv(1, 1).setColor(red, green, blue, alpha).setNormal(0.0f, -1.0f, 0.0f);
            this.bufferbuilder.addVertex(x2, y1, z1).setUv(0, 1).setColor(red, green, blue, alpha).setNormal(0.0f, -1.0f, 0.0f);
        }

        if (renderZP) {
            this.bufferbuilder.addVertex(x2, y1, z2).setUv(0, 0).setColor(red, blue, green, alpha).setNormal(0.0f, 0.0f, 1.0f);
            this.bufferbuilder.addVertex(x1, y1, z2).setUv(1, 0).setColor(red, blue, green, alpha).setNormal(0.0f, 0.0f, 1.0f);
            this.bufferbuilder.addVertex(x1, y2, z2).setUv(1, 1).setColor(red, blue, green, alpha).setNormal(0.0f, 0.0f, 1.0f);
            this.bufferbuilder.addVertex(x2, y2, z2).setUv(0, 1).setColor(red, blue, green, alpha).setNormal(0.0f, 0.0f, 1.0f);
        }

        if (renderZN) {
            this.bufferbuilder.addVertex(x1, y1, z1).setUv(0, 0).setColor(red, blue, green, alpha).setNormal(0.0f, 0.0f, -1.0f);
            this.bufferbuilder.addVertex(x2, y1, z1).setUv(1, 0).setColor(red, blue, green, alpha).setNormal(0.0f, 0.0f, -1.0f);
            this.bufferbuilder.addVertex(x2, y2, z1).setUv(1, 1).setColor(red, blue, green, alpha).setNormal(0.0f, 0.0f, -1.0f);
            this.bufferbuilder.addVertex(x1, y2, z1).setUv(0, 1).setColor(red, blue, green, alpha).setNormal(0.0f, 0.0f, -1.0f);
        }
    }
}
