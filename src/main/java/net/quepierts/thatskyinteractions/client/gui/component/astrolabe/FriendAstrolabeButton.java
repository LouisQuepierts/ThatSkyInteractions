package net.quepierts.thatskyinteractions.client.gui.component.astrolabe;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.client.data.ClientTSIDataCache;
import net.quepierts.thatskyinteractions.client.gui.Palette;
import net.quepierts.thatskyinteractions.client.gui.animate.ScreenAnimator;
import net.quepierts.thatskyinteractions.client.gui.holder.FloatHolder;
import net.quepierts.thatskyinteractions.client.gui.layer.AnimateScreenHolderLayer;
import net.quepierts.thatskyinteractions.client.gui.screen.FriendScreen;
import net.quepierts.thatskyinteractions.client.util.RenderUtils;
import net.quepierts.thatskyinteractions.common.data.astrolabe.FriendAstrolabeInstance;

@OnlyIn(Dist.CLIENT)
public class FriendAstrolabeButton extends AstrolabeButton {
    private static final ResourceLocation RECEIVED_OVERLAY = ThatSkyInteractions.getLocation("textures/gui/astrolabe_received.png");
    private final FriendAstrolabeWidget parent;
    private final float rand;
    private FriendAstrolabeInstance.NodeData data;
    public FriendAstrolabeButton(
            int x, int y,
            Component message,
            ScreenAnimator animator,
            FriendAstrolabeWidget parent,
            FloatHolder alpha,
            FriendAstrolabeInstance.NodeData data
    ) {
        super(x, y, 12, message, animator, alpha);
        this.parent = parent;
        this.data = data;
        this.rand = ThatSkyInteractions.RANDOM.nextFloat();
    }

    public void setData(FriendAstrolabeInstance.NodeData data) {
        this.data = data;
    }

    @Override
    public void onPress() {
        if (this.data == null)
            return;

        if (!this.data.hasFlag(FriendAstrolabeInstance.Flag.RECEIVED)) {
            AnimateScreenHolderLayer.INSTANCE.push(new FriendScreen(
                    this.data.getFriendData()
            ));
        } else {
            ClientTSIDataCache cache = ThatSkyInteractions.getInstance().getClient().getCache();
            cache.gainLight(this.data.getFriendData().getUuid(), true);
        }
    }

    @Override
    protected void render(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        float alpha = Palette.getShaderAlpha();

        PoseStack pose = guiGraphics.pose();
        pose.pushPose();

        if (this.data == null)  {
            if (this.hasLinked()) {
                pose.translate(2, 2, 0);
                Palette.mulShaderAlpha(this.alpha.getValue());
                RenderUtils.drawGlowingRing(guiGraphics, 0, 0, 4, 0.12f, 0xff25223d);
                Palette.setShaderAlpha(alpha);
            }
        } else {
            boolean sent = data.hasFlag(FriendAstrolabeInstance.Flag.SENT);
            if (sent) {
                pose.translate(6.0f, 6.0f, 0.0f);
                pose.mulPose(Axis.ZN.rotationDegrees(parent.getRotate()));
                pose.translate(-12.0f, -12.0f, 0.0f);
                RenderUtils.drawDoubleCrossHalo(
                        guiGraphics, 0, 0, 24,
                        0.7f + Mth.sin((rand + ScreenAnimator.GLOBAL.time()) * 12) * 0.02f + this.alpha.getValue() * 0.2f,
                        1.8f, 0xffa2c5de
                );
            } else {
                Palette.mulShaderAlpha(this.alpha.getValue());
                RenderUtils.drawGlowingRing(guiGraphics, 0, 0, 6, 0.08f, 0xff25223d);

                pose.translate(6.0f, 6.0f, 0.0f);
                pose.mulPose(Axis.ZN.rotationDegrees(parent.getRotate()));
                RenderUtils.drawCrossLightSpot(
                        guiGraphics, -6, -6, 12,
                        0.7f + Mth.sin((rand + ScreenAnimator.GLOBAL.time()) * 12) * 0.02f + this.alpha.getValue() * 0.2f,
                        1.2f, 0xffa4e5f7
                );
                pose.translate(-12.0f, -12.0f, -0.0f);
                Palette.setShaderAlpha(alpha);
            }

            if (data.hasFlag(FriendAstrolabeInstance.Flag.RECEIVED)) {
                pose.translate(9.0f, 9.0f, 0.0f);
                Palette.mulShaderAlpha(this.alpha.getValue());
                float delta = rand + ScreenAnimator.GLOBAL.time();
                final float p8 = Mth.PI / 8f;
                final float d00 = 6f;
                final float d45 = 6f * Mth.sin(p8 * 2);
                final float dv = 5f;

                drawParticle(guiGraphics, delta + p8, 0, -d00, 0, -dv, 0xffa4e5f7);
                drawParticle(guiGraphics, delta + p8 * 15f, d45, -d45, dv, -dv, 0xffa4e5f7);
                drawParticle(guiGraphics, delta + p8 * 8f, d00, 0, dv, 0, 0xffa4e5f7);
                drawParticle(guiGraphics, delta + p8 * 12f, d45, d45, dv, dv, 0xffa4e5f7);

                drawParticle(guiGraphics, delta + p8 * 5f, 0, d00, 0, dv, 0xffa4e5f7);
                drawParticle(guiGraphics, delta + p8 * 3f, -d45, d45, -dv, dv, 0xffa4e5f7);
                drawParticle(guiGraphics, delta + p8 * 10f, -d00, 0, -dv, 0, 0xffa4e5f7);
                drawParticle(guiGraphics, delta + p8 * 14f, -d45, -d45, -dv, -dv, 0xffa4e5f7);


                pose.translate(-9.0f, -9.0f, 0.0f);
            }

            Palette.setShaderAlpha(alpha);
        }

        pose.popPose();
    }

    private void drawParticle(GuiGraphics guiGraphics, float delta, float x0, float y0, float dx, float dy, int color) {
        float sin = Mth.sin(delta);
        if (sin > 0) {
            float dp = Mth.abs(Mth.sin(delta / 2.0f));
            RenderUtils.drawLightSpot(guiGraphics, x0 + dx * dp, y0 + dy * dp, 6, sin, color);
        }
    }
}
