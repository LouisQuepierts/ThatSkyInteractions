package net.quepierts.thatskyinteractions.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import lombok.Getter;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.quepierts.thatskyinteractions.client.reference.Shaders;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

public final class SdfGraphics {

    @Getter
    private static final SdfGraphics instance = new SdfGraphics();

    private float   x, y, w, h;
    private int     r, g, b, a;

    private float   smooth;
    private float   round;
    private float   rotation;

    private boolean center;

    public SdfGraphics rectangle(float x, float y, float width, float height) {
        this.x          = x;
        this.y          = y;
        this.w          = width;
        this.h          = height;

        var shader      = Shaders.GRAPHICS.getInstance();
        shader          .rect(width, height);

        return          this;
    }

    public SdfGraphics roundedHBar(float x, float y, float width, float height) {
        return          this.round(height * 0.5f)
                            .rectangle(x, y, width, height);
    }

    public SdfGraphics circle(float x, float y, float radius) {
        this.x          = x;
        this.y          = y;
        this.w          = radius * 2;
        this.h          = radius * 2;

        var shader      = Shaders.GRAPHICS.getInstance();
        shader          .circle(radius);

        return          this;
    }

    public SdfGraphics arc(float x, float y, float sweep, float radius, float width) {
        this.x          = x;
        this.y          = y;
        this.w          = radius * 2;
        this.h          = radius * 2;

        var shader      = Shaders.GRAPHICS.getInstance();
        shader          .arc(sweep, radius, width);

        return          this;
    }

    public SdfGraphics sector(float x, float y, float sweep, float radius, float width) {
        this.x          = x;
        this.y          = y;
        this.w          = radius * 2;
        this.h          = radius * 2;

        var shader      = Shaders.GRAPHICS.getInstance();
        shader          .sector(sweep, radius, width);

        return          this;
    }

    public SdfGraphics pie(float x, float y, float sweep, float radius) {
        this.x          = x;
        this.y          = y;
        this.w          = radius * 2;
        this.h          = radius * 2;

        var shader      = Shaders.GRAPHICS.getInstance();
        shader          .pie(sweep, radius);

        return          this;
    }

    public SdfGraphics color(int color) {
        this.r          = FastColor.ARGB32.red(color);
        this.g          = FastColor.ARGB32.green(color);
        this.b          = FastColor.ARGB32.blue(color);
        this.a          = FastColor.ARGB32.alpha(color);
        return          this;
    }

    public SdfGraphics color(float red, float green, float blue, float alpha) {
        this.r          = (int) (red * 255F);
        this.g          = (int) (green * 255F);
        this.b          = (int) (blue * 255F);
        this.a          = (int) (alpha * 255F);
        return          this;
    }

    public SdfGraphics color(int red, int green, int blue, int alpha) {
        this.r          = red;
        this.g          = green;
        this.b          = blue;
        this.a          = alpha;
        return          this;
    }

    public SdfGraphics smooth(float radius) {
        this.smooth     = Math.max(0.0f, radius);
        return          this;
    }

    public SdfGraphics round(float radius) {
        this.round      = Math.max(0.0f, radius);
        return          this;
    }

    public SdfGraphics rotate(float degrees) {
        this.rotation   = Mth.wrapDegrees(degrees);
        return          this;
    }

    public SdfGraphics center(boolean center) {
        this.center     = center;
        return          this;
    }

    public SdfGraphics fill(@NotNull PoseStack pose) {
        var shader      = Shaders.GRAPHICS.getInstance();
        shader          .shared(this.smooth, this.round);
        shader          .fill();

        this            ._draw(pose);
        return          this;
    }

    public SdfGraphics stroke(@NotNull PoseStack pose, float width) {
        var shader      = Shaders.GRAPHICS.getInstance();
        shader          .shared(this.smooth, this.round);
        shader          .stroke(width);

        this            ._draw(pose);
        return          this;
    }

    public SdfGraphics light(@NotNull PoseStack pose, float radius) {
        var last        = this.smooth;
        this.smooth     = radius;

        var shader      = Shaders.GRAPHICS.getInstance();
        shader          .shared(this.smooth, this.round);
        shader          .light();

        this            ._draw(pose);
        this.smooth     = last;
        return          this;
    }

    public SdfGraphics reset() {
        this.x          = 0;
        this.y          = 0;
        this.w          = 0;
        this.h          = 0;
        this.r          = 0;
        this.g          = 0;
        this.b          = 0;
        this.a          = 0;
        this.smooth     = 0;
        this.round      = 0;
        this.rotation   = 0;
        this.center     = false;
        return          this;
    }

    private void _draw(@NotNull PoseStack pose) {
        var matrix4f    = new Matrix4f(pose.last().pose());
        var ex = (this.round + this.smooth) * 2.0f;
        var width       = this.w + ex;
        var height      = this.h + ex;

        if (this.center) {
            matrix4f    .translate(this.x, this.y, 0);
        } else {
            matrix4f    .translate(this.x + this.w * 0.5f, this.y + this.h * 0.5f, 0);
        }

        matrix4f        .rotateZ(Mth.DEG_TO_RAD * this.rotation)
                        .scale(width, height, 1.0f)
                        .mul(RenderSystem.getModelViewMatrix());

        var builder     = Tesselator
                            .getInstance()
                            .begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);

        var shader      = Shaders.GRAPHICS.use();

        shader          .uModelMatrix.set(matrix4f);
        shader          .uRect.set(this.x, this.y, width, height);

        builder         .addVertex(-0.5f, -0.5f, 0).setUv(0, 0).setColor(r, g, b, a)
                        .addVertex(-0.5f, +0.5f, 0).setUv(0, 1).setColor(r, g, b, a)
                        .addVertex(+0.5f, +0.5f, 0).setUv(1, 1).setColor(r, g, b, a)
                        .addVertex(+0.5f, -0.5f, 0).setUv(1, 0).setColor(r, g, b, a);

        BufferUploader  .drawWithShader(builder.buildOrThrow());
    }


}
