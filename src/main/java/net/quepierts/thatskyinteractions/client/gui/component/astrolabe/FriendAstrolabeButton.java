package net.quepierts.thatskyinteractions.client.gui.component.astrolabe;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.client.ClientHelper;
import net.quepierts.thatskyinteractions.client.gui.Palette;
import net.quepierts.thatskyinteractions.client.gui.animate.AnimateUtils;
import net.quepierts.thatskyinteractions.client.gui.animate.LerpNumberAnimation;
import net.quepierts.thatskyinteractions.client.gui.animate.ScreenAnimator;
import net.quepierts.thatskyinteractions.client.gui.holder.FloatHolder;
import net.quepierts.thatskyinteractions.client.gui.layer.AnimateScreenHolderLayer;
import net.quepierts.thatskyinteractions.client.gui.screen.FriendScreen;
import net.quepierts.thatskyinteractions.client.util.RenderUtils;
import net.quepierts.thatskyinteractions.common.data.FriendData;
import net.quepierts.thatskyinteractions.common.data.astrolabe.FriendAstrolabeInstance;
import net.quepierts.thatskyinteractions.common.data.astrolabe.node.AstrolabeNode;
import org.joml.Vector2f;

@OnlyIn(Dist.CLIENT)
public class FriendAstrolabeButton extends AstrolabeButton {
    private static final float D00 = 6f;
    private static final float D45 = 6f * Mth.sin(Mth.HALF_PI / 2);
    private static final float DV = 4f;
    private static final float DV1 = 6f;
    private static final float PI8 = Mth.HALF_PI * 1.6f;

    private final FriendAstrolabeWidget parent;
    private final float rand;
    private final float[] amplifier = new float[8];

    private final LerpNumberAnimation claimAnimation;
    private final FloatHolder claim;
    private final AstrolabeNode.DescriptionPosition descriptionPosition;

    private FriendAstrolabeInstance.NodeData data;
    public FriendAstrolabeButton(
            int x, int y,
            AstrolabeNode.DescriptionPosition descriptionPosition,
            Component message,
            ScreenAnimator animator,
            FriendAstrolabeWidget parent,
            FloatHolder alpha,
            FriendAstrolabeInstance.NodeData data
    ) {
        super(x, y, 12, message, animator, alpha);
        this.parent = parent;
        this.descriptionPosition = descriptionPosition;
        this.data = data;
        this.rand = ThatSkyInteractions.RANDOM.nextFloat();

        this.claim = new FloatHolder(0.0f);
        this.claimAnimation = new LerpNumberAnimation(this.claim, AnimateUtils.Lerp::linear, 0, Mth.PI, 1);

        if (data != null) {
            for (int i = 0; i < this.amplifier.length; i++) {
                this.amplifier[i] = 1 + ThatSkyInteractions.RANDOM.nextFloat();
            }
        }
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
            this.animator.play(this.claimAnimation);
            ClientHelper.gainLight(this.data.getFriendData().getUuid());
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
            boolean sent = this.data.hasFlag(FriendAstrolabeInstance.Flag.SENT);
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

            if (this.data.hasFlag(FriendAstrolabeInstance.Flag.RECEIVED)) {
                pose.translate(9.0f, 9.0f, 0.0f);
                Palette.mulShaderAlpha(this.alpha.getValue());
                float delta = rand * Mth.HALF_PI + ScreenAnimator.GLOBAL.time();

                drawParticle(guiGraphics, delta + this.amplifier[0] * PI8, 0, -D00, 0, -DV, 0xffa4e5f7);
                drawParticle(guiGraphics, delta + this.amplifier[1] * PI8, D45, -D45, DV, -DV, 0xffa4e5f7);
                drawParticle(guiGraphics, delta + this.amplifier[2] * PI8, D00, 0, DV, 0, 0xffa4e5f7);
                drawParticle(guiGraphics, delta + this.amplifier[3] * PI8, D45, D45, DV, DV, 0xffa4e5f7);

                drawParticle(guiGraphics, delta + this.amplifier[4] * PI8, 0, D00, 0, DV, 0xffa4e5f7);
                drawParticle(guiGraphics, delta + this.amplifier[5] * PI8, -D45, D45, -DV, DV, 0xffa4e5f7);
                drawParticle(guiGraphics, delta + this.amplifier[6] * PI8, -D00, 0, -DV, 0, 0xffa4e5f7);
                drawParticle(guiGraphics, delta + this.amplifier[7] * PI8, -D45, -D45, -DV, -DV, 0xffa4e5f7);

                pose.translate(-9.0f, -9.0f, 0.0f);
            } else if (claimAnimation.isRunning()) {

                float delta = this.claim.getValue();
                pose.translate(9.0f, 9.0f, 0.0f);
                drawParticle(guiGraphics, delta * this.amplifier[0], 0, -D00, 0, -DV1 * 2f, 0xffa4e5f7);
                drawParticle(guiGraphics, delta * this.amplifier[1], D45, -D45, DV1, -DV1, 0xffa4e5f7);
                drawParticle(guiGraphics, delta * this.amplifier[2], D00, 0, DV1 * 2f, 0, 0xffa4e5f7);
                drawParticle(guiGraphics, delta * this.amplifier[3], D45, D45, DV1, DV1, 0xffa4e5f7);

                drawParticle(guiGraphics, delta * this.amplifier[4], 0, D00, 0, DV1 * 2f, 0xffa4e5f7);
                drawParticle(guiGraphics, delta * this.amplifier[5], -D45, D45, -DV1, DV1, 0xffa4e5f7);
                drawParticle(guiGraphics, delta * this.amplifier[6], -D00, 0, -DV1 * 2f, 0, 0xffa4e5f7);
                drawParticle(guiGraphics, delta * this.amplifier[7], -D45, -D45, -DV1, -DV1, 0xffa4e5f7);

                pose.translate(-9.0f, -9.0f, 0.0f);
            }

            Font font = Minecraft.getInstance().font;
            FriendData data = this.data.getFriendData();
            String nickname = data.getNickname();
            Palette.mulShaderAlpha(this.alpha.getValue());
            switch (this.descriptionPosition) {
                case UP:
                    guiGraphics.drawCenteredString(font, nickname, 12, -4, Palette.NORMAL_TEXT_COLOR);
                    break;
                case DOWN:
                    guiGraphics.drawCenteredString(font, nickname, 12, 20, Palette.NORMAL_TEXT_COLOR);
                    break;
                case LEFT:
                    int textWidth = font.width(nickname);
                    guiGraphics.drawString(font, nickname, -textWidth, 4, Palette.NORMAL_TEXT_COLOR);
                    break;
                case RIGHT:
                    guiGraphics.drawString(font, nickname, 24, 4, Palette.NORMAL_TEXT_COLOR);
                    break;
            }

            if (Vector2f.distanceSquared(this.getX(), this.getY(), mouseX, mouseY) < 32) {
                RenderSystem.enableBlend();
                RenderUtils.drawRing(guiGraphics, 6, 6, 6, 0.05f, Palette.NORMAL_TEXT_COLOR);
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
