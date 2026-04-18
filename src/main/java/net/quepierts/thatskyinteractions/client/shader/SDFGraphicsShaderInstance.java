package net.quepierts.thatskyinteractions.client.shader;

import com.mojang.blaze3d.shaders.AbstractUniform;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceProvider;
import net.minecraft.util.Mth;
import org.joml.Vector4f;

import java.io.IOException;

public class SDFGraphicsShaderInstance extends ShaderInstance {

    public final    AbstractUniform uModelMatrix;
    public final    AbstractUniform uRect;

    public final    AbstractUniform uRenderType;
    public final    AbstractUniform uPassType;

//    public final AbstractUniform uColor;

    public final    AbstractUniform uSharedParams;
    public final    AbstractUniform uShapeParams;

    private final   Vector4f        vSharedParams   = new Vector4f();

    private         PassType        pass            = PassType.FILL;

    public SDFGraphicsShaderInstance(
            ResourceProvider provider,
            ResourceLocation location,
            VertexFormat format
    ) throws IOException {
        super(
                provider,
                location,
                format
        );

        this.uRect              = this.safeGetUniform("uRect");
        this.uModelMatrix       = this.safeGetUniform("uModelMatrix");

        this.uRenderType        = this.safeGetUniform("uRenderType");
        this.uPassType          = this.safeGetUniform("uPassType");

//        this.uColor             = this.safeGetUniform("uColor");

        this.uSharedParams     = this.safeGetUniform("uSharedParams");
        this.uShapeParams       = this.safeGetUniform("uShapeParams");
    }

    public void rect(float width, float height) {
        this.uRenderType        .set(RenderType.RECT.ordinal());
        this.uShapeParams       .set(width * 0.5f, height * 0.5f, 0.0f, 0.0f);
    }

    public void circle(float radius) {
        this.uRenderType        .set(RenderType.CIRCLE.ordinal());
        this.uShapeParams       .set(radius, 0.0f, 0.0f, 0.0f);
    }

    public void arc(float sweep, float radius, float thickness) {
        this.uRenderType        .set(RenderType.ARC.ordinal());

        var radian              = sweep * Mth.DEG_TO_RAD;
        var cos                 = Mth   .cos(radian);
        var sin                 = Mth   .sin(radian);
        this.uShapeParams       .set(cos, sin, radius - thickness * 0.5f, thickness);
    }

    public void sector(float sweep, float radius, float thickness) {
        this.uRenderType        .set(RenderType.SECTOR.ordinal());

        var radian              = sweep * Mth.DEG_TO_RAD;
        var cos                 = Mth   .cos(radian);
        var sin                 = Mth   .sin(radian);
        this.uShapeParams       .set(cos, sin, radius - thickness * 0.5f, thickness);
    }

    public void pie(float sweep, float radius) {
        this.uRenderType        .set(RenderType.PIE.ordinal());

        var radian              = sweep * Mth.DEG_TO_RAD;
        var cos                 = Mth   .cos(radian);
        var sin                 = Mth   .sin(radian);
        this.uShapeParams       .set(cos, sin, radius, 0.0f);
    }

    public void smooth(float smooth) {
        this                    ._smooth(smooth);
    }

    public void round(float radius) {
        this                    ._cornerRadius(radius);
    }

    public void fill() {
        if (this.pass           != PassType.FILL) {
            this.pass           = PassType.FILL;
            this.uPassType      .set(PassType.FILL.ordinal());
        }
    }

    public void stroke(float width) {
        this                    ._width(width);

        if (this.pass           != PassType.STROKE) {
            this.pass           = PassType.STROKE;
            this.uPassType      .set(PassType.STROKE.ordinal());
        }
    }

    public void light() {
        if (this.pass           != PassType.LIGHT) {
            this.pass           = PassType.LIGHT;
            this.uPassType      .set(PassType.LIGHT.ordinal());
        }
    }

    public void shared(float smooth, float round) {
        this.vSharedParams.x        = smooth;
        this.vSharedParams.z        = round;
        this.uSharedParams          .set(this.vSharedParams);
    }

    private void _smooth(float value) {
        this.vSharedParams.x        = value;
        this.uSharedParams          .set(this.vSharedParams);
    }

    private void _width(float value) {
        this.vSharedParams.y        = value;
        this.uSharedParams          .set(this.vSharedParams);
    }

    private void _cornerRadius(float value) {
        this.vSharedParams.z        = value;
        this.uSharedParams          .set(this.vSharedParams);
    }

    public enum RenderType {
        RECT,
//        ROUND_RECT,
        CIRCLE,
        ARC,
        SECTOR,
        PIE
    }

    public enum PassType {
        FILL,
        STROKE,
        LIGHT
    }
}
