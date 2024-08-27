package net.quepierts.thatskyinteractions.client.gui.component.tree;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.client.util.RenderUtils;
import net.quepierts.thatskyinteractions.client.gui.Palette;
import net.quepierts.thatskyinteractions.client.gui.animate.AnimateUtils;
import net.quepierts.thatskyinteractions.client.gui.animate.LerpNumberAnimation;
import net.quepierts.thatskyinteractions.client.gui.animate.ScreenAnimator;
import net.quepierts.thatskyinteractions.client.gui.animate.ShakeNumberAnimation;
import net.quepierts.thatskyinteractions.client.gui.component.CulledRenderable;
import net.quepierts.thatskyinteractions.client.gui.component.button.BounceButton;
import net.quepierts.thatskyinteractions.client.gui.holder.DoubleHolder;
import net.quepierts.thatskyinteractions.client.gui.holder.FloatHolder;
import net.quepierts.thatskyinteractions.data.Currency;
import net.quepierts.thatskyinteractions.data.tree.NodeState;
import org.joml.Vector4f;

@OnlyIn(Dist.CLIENT)
public class TreeNodeButton extends BounceButton implements CulledRenderable {
    protected static final ResourceLocation BG_LIGHT_SPOT = ThatSkyInteractions.getLocation("textures/gui/light.png");

    public final String id;
    public final int price;
    public final Currency currency;
    public final NodeState state;
    private boolean loaded = false;
    private final FloatHolder enter = new FloatHolder(0.0f);
    protected final DoubleHolder shake = new DoubleHolder(0.0f);
    protected final ShakeNumberAnimation shakeAnimation = new ShakeNumberAnimation(this.shake, -1, 1, 4, 0.3f);

    public TreeNodeButton(String id, int x, int price, Component message, int y, ResourceLocation icon, ScreenAnimator animator, NodeState state) {
        super(x, y, 32, message, animator, icon);
        this.id = id;
        this.price = price;
        this.currency = Currency.NORMAL_CANDLE;
        this.state = state;
    }

    public TreeNodeButton(String id, int x, int y, int price, ScreenAnimator animator, ResourceLocation icon, Component message, Currency currency, NodeState state) {
        super(x, y, 32, message, animator, icon);
        this.id = id;
        this.price = price;
        this.currency = currency;
        this.state = state;
    }

    @Override
    public void onPress() {

    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        if (!loaded) {
            animator.play(new LerpNumberAnimation(this.enter, AnimateUtils.Lerp::smooth, 0.0, 1.0, 0.5f));
            loaded = true;
        }

        float alpha = Palette.getShaderAlpha();
        Palette.mulShaderAlpha(this.enter.getValue());

        super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);

        Palette.setShaderAlpha(alpha);
    }

    @Override
    protected void renderIcon(GuiGraphics guiGraphics, int begin) {
        double v = shake.get();
        guiGraphics.pose().translate(v, 0.0, 0.0);

        Palette.useColor(state);
        RenderUtils.blitIcon(guiGraphics, this.getIcon(), begin, begin, this.getWidth(), this.getHeight());
        Palette.reset();

        guiGraphics.pose().translate(-v, 0.0, 0.0);

        if (this.state == NodeState.UNLOCKABLE && this.price > 0) {
            guiGraphics.blit(this.currency.icon, begin + 16, begin + 28, 0, 0, 16, 16, 16, 16);
            guiGraphics.drawString(Minecraft.getInstance().font, String.valueOf(this.price), begin + 28, begin + 40, Palette.NORMAL_TEXT_COLOR);
        }
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, int begin) {
        Palette.useColor(state);
        guiGraphics.blit(BG_LIGHT_SPOT, begin, begin, 32, 32, 0, 0, 32, 32, 32, 32);
        Palette.reset();
    }

    @Override
    public void onClick(double mouseX, double mouseY) {}

    public void onClickLocked() {
        this.animator.play(shakeAnimation);
        Minecraft.getInstance().getSoundManager().play(
                SimpleSoundInstance.forUI(
                        SoundEvents.STONE_BUTTON_CLICK_OFF,
                        1.0f
                )
        );
    }

    public void onClickUnlockable(int count) {
        this.animator.play(this.clickAnimation);
        Minecraft.getInstance().getSoundManager().play(
                SimpleSoundInstance.forUI(
                        SoundEvents.EXPERIENCE_ORB_PICKUP,
                        count * 0.1F + 0.6F
                )
        );
    }

    public void onClickUnlocked() {
        this.animator.play(this.clickAnimation);
        Minecraft.getInstance().getSoundManager().play(
                SimpleSoundInstance.forUI(
                        SoundEvents.EXPERIENCE_ORB_PICKUP,
                        (ThatSkyInteractions.RANDOM.nextFloat() - ThatSkyInteractions.RANDOM.nextFloat()) * 0.35F + 0.9F
                )
        );
    }

    @Override
    public boolean shouldRender(Vector4f region) {
        return CulledRenderable.intersects(region, this.getX(), this.getY(), this.getWidth(), this.getHeight() + 48);
    }

    public void renderUnlockMessageInvite(GuiGraphics guiGraphics, PoseStack pose, int width, int height) {
    }

    public void renderUnlockMessageAccept(GuiGraphics guiGraphics, PoseStack pose, int width, int height) {

    }

    public NodeState getNodeState() {
        return this.state;
    }

    public Currency getCurrency() {
        return this.currency;
    }

    public int getPrice() {
        return price;
    }
}
