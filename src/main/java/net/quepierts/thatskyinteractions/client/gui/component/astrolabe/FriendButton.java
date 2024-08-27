package net.quepierts.thatskyinteractions.client.gui.component.astrolabe;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.client.gui.layer.AnimateScreenHolderLayer;
import net.quepierts.thatskyinteractions.client.gui.screen.FriendScreen;
import net.quepierts.thatskyinteractions.client.util.RenderUtils;
import net.quepierts.thatskyinteractions.client.gui.Palette;
import net.quepierts.thatskyinteractions.client.gui.animate.ScreenAnimator;
import net.quepierts.thatskyinteractions.client.gui.holder.FloatHolder;
import net.quepierts.thatskyinteractions.data.astrolabe.FriendAstrolabeInstance;

@OnlyIn(Dist.CLIENT)
public class FriendButton extends AstrolabeButton {
    private final AstrolabeWidget parent;
    private final FriendAstrolabeInstance.NodeData data;
    private final float rand;
    public FriendButton(
            int x, int y,
            Component message,
            ScreenAnimator animator,
            AstrolabeWidget parent,
            FloatHolder alpha,
            FriendAstrolabeInstance.NodeData data
    ) {
        super(x, y, 12, message, animator, alpha);
        this.parent = parent;
        this.data = data;
        this.rand = ThatSkyInteractions.RANDOM.nextFloat();
    }

    @Override
    public void onPress() {
        AnimateScreenHolderLayer.INSTANCE.push(new FriendScreen(
                this.data.getFriendData()
        ));
    }

    @Override
    protected void render(GuiGraphics guiGraphics) {
        float alpha = Palette.getShaderAlpha();
        boolean sent = data.hasFlag(FriendAstrolabeInstance.Flag.SENT);

        PoseStack pose = guiGraphics.pose();
        pose.pushPose();
        if (sent) {
            pose.translate(6.0f, 6.0f, 0.0f);
            pose.mulPose(Axis.ZN.rotationDegrees(parent.getRotate()));
            pose.translate(-12.0f, -12.0f, 0.0f);
            RenderUtils.drawDoubleCrossHalo(
                    guiGraphics, 0, 0, 24,
                    0.7f + Mth.sin((rand + ScreenAnimator.GLOBAL.time()) * 12) * 0.02f + this.alpha.getValue() * 0.2f,
                    1.5f, 0xffa2c5de
            );
        } else {
            Palette.mulShaderAlpha(this.alpha.getValue());
            RenderUtils.drawGlowingRing(guiGraphics, 0, 0, 6, 0.08f, 0xff25223d);
            Palette.setShaderAlpha(alpha);

            pose.translate(6.0f, 6.0f, 0.0f);
            pose.mulPose(Axis.ZN.rotationDegrees(parent.getRotate()));
            RenderUtils.drawCrossLightSpot(
                    guiGraphics, -6, -6, 12,
                    0.7f + Mth.sin((rand + ScreenAnimator.GLOBAL.time()) * 12) * 0.02f + this.alpha.getValue() * 0.2f,
                    1.2f, 0xffa4e5f7
            );
        }

        pose.popPose();
    }
}
