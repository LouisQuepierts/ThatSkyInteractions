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
import net.quepierts.thatskyinteractions.client.gui.Palette;
import net.quepierts.thatskyinteractions.client.gui.animate.ScreenAnimator;
import net.quepierts.thatskyinteractions.client.gui.holder.FloatHolder;
import net.quepierts.thatskyinteractions.client.gui.layer.AnimateScreenHolderLayer;
import net.quepierts.thatskyinteractions.client.gui.screen.FriendScreen;
import net.quepierts.thatskyinteractions.client.util.RenderUtils;
import net.quepierts.thatskyinteractions.data.astrolabe.FriendAstrolabeInstance;

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
        AnimateScreenHolderLayer.INSTANCE.push(new FriendScreen(
                this.data.getFriendData()
        ));
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
                Palette.mulShaderAlpha(this.alpha.getValue());
                RenderUtils.blit(guiGraphics, RECEIVED_OVERLAY, 0, 0, 24, 24);
            }

            Palette.setShaderAlpha(alpha);
        }

        pose.popPose();
    }
}
