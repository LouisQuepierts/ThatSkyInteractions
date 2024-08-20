package net.quepierts.thatskyinteractions.client.gui.layer;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.GameType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.client.gui.Palette;
import net.quepierts.thatskyinteractions.client.gui.animate.AbstractScreenAnimation;
import net.quepierts.thatskyinteractions.client.gui.animate.AnimateUtils;
import net.quepierts.thatskyinteractions.client.gui.animate.LerpNumberAnimation;
import net.quepierts.thatskyinteractions.client.gui.animate.ScreenAnimator;
import net.quepierts.thatskyinteractions.client.gui.holder.FloatHolder;
import net.quepierts.thatskyinteractions.data.Currency;
import org.jetbrains.annotations.NotNull;

import java.util.EnumMap;
import java.util.EnumSet;

@OnlyIn(Dist.CLIENT)
public class CandleInfoLayer implements LayeredDraw.Layer {
    public static final ResourceLocation LOCATION = ThatSkyInteractions.getLocation("candle_info");
    public static final CandleInfoLayer INSTANCE = new CandleInfoLayer();
    public static final int OBJECT_POOL_SIZE = 64;
    private final Minecraft minecraft = Minecraft.getInstance();

    private final EnumMap<Currency, Entry> map;
    private final ObjectList<Entry> entries;

    private final AnimationObject[] objectPool = new AnimationObject[OBJECT_POOL_SIZE];

    private final EnumSet<Currency> freeze;

    CandleInfoLayer() {
        this.map = new EnumMap<>(Currency.class);
        this.entries = new ObjectArrayList<>();
        this.freeze = EnumSet.noneOf(Currency.class);

        for (Currency value : Currency.values()) {
            Entry entry = new Entry(value);
            this.map.put(value, entry);
            this.entries.add(entry);
        }

        for (int i = 0; i < OBJECT_POOL_SIZE; i++) {
            objectPool[i] = new AnimationObject();
        }
    }
    @SuppressWarnings("all")
    @Override
    public void render(GuiGraphics guiGraphics, @NotNull DeltaTracker deltaTracker) {
        if (this.minecraft.gameMode.getPlayerMode() == GameType.SPECTATOR)
            return;

        Inventory inventory = this.minecraft.player.getInventory();

        RenderSystem.enableBlend();
        PoseStack pose = guiGraphics.pose();
        pose.pushPose();

        for (AnimationObject animate : objectPool) {
            if (animate.isRunning()) {
                animate.render(guiGraphics);
            }
        }

        pose.translate(8, -24, 0);
        pose.scale(1.5f, 1.5f, 1.0f);

        for (Entry entry : this.entries) {
            entry.render(guiGraphics, inventory);
            pose.translate(20f, 0f, 0f);
        }

        pose.popPose();
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.disableBlend();
    }

    public void show(boolean show, Currency... currency) {
        if (currency.length == 0) {
            for (Entry value : this.entries) {
                value.setShow(show);
            }
        } else {
            for (Currency item : currency) {
                Entry entry = this.map.get(item);
                if (entry != null)
                    entry.setShow(show);
            }
        }
    }

    public void shrink(Currency currency, int i) {
        if (freeze.contains(currency))
            return;

        Entry entry = this.map.get(currency);
        if (entry != null) {
            this.play(i, false, entry);
            entry.shrink(i);
        }
    }

    public void setShrink(Currency currency, int i) {
        if (freeze.contains(currency))
            return;

        Entry entry = this.map.get(currency);
        if (entry != null) {
            this.play(i - entry.shrink, false, entry);
            entry.setShrink(i);
        }
    }

    public void refund(Currency currency) {
        if (freeze.contains(currency))
            return;

        Entry entry = this.map.get(currency);
        if (entry != null) {
            this.play(entry.shrink, true, entry);
            entry.refund();
        }
    }

    public void freeze(Currency currency) {
        this.freeze.add(currency);
    }

    public void unfreeze(Currency currency) {
        this.freeze.remove(currency);
    }

    private void play(int i, boolean backward, Entry entry) {
        if (i <= 0)
            return;
        final int right = (int)(
                minecraft.mouseHandler.xpos()
                        * (double)minecraft.getWindow().getGuiScaledWidth()
                        / (double)minecraft.getWindow().getScreenWidth()
        );
        final int down = (int)(
                minecraft.mouseHandler.ypos()
                        * (double)minecraft.getWindow().getGuiScaledHeight()
                        / (double)minecraft.getWindow().getScreenHeight()
        );

        for (AnimationObject object : this.objectPool) {
            if (object.isRunning())
                continue;

            object.reset(
                    ThatSkyInteractions.RANDOM.nextFloat() * 2.0f,
                    this.getEntryX(entry), 8.0f,
                    right, down,
                    entry.currency.icon, backward);

            if (i > 1) {
                ScreenAnimator.GLOBAL.play(object, ThatSkyInteractions.RANDOM.nextFloat() * 0.25f);
            } else {
                ScreenAnimator.GLOBAL.play(object);
            }

            i --;
            if (i <= 0)
                return;
        }
    }

    private int getEntryX(Entry entry) {
        return entries.indexOf(entry) * 32 + 8;
    }

    public class Entry {
        private final FloatHolder enterHolder;
        private final FloatHolder amountHolder;

        private final LerpNumberAnimation enterAnimation;
        private final LerpNumberAnimation amountAnimation;

        private final Currency currency;

        private boolean show = false;
        private int counter = 0;
        private int shrink = 0;

        public Entry(Currency target) {
            this.currency = target;

            this.enterHolder = new FloatHolder(0.0f);
            this.amountHolder = new FloatHolder(0.0f);
            this.enterAnimation = new LerpNumberAnimation(this.enterHolder, AnimateUtils.Lerp::smooth, 0, 0, 0.5f);
            this.amountAnimation = new LerpNumberAnimation(this.amountHolder, AnimateUtils.Lerp::sCurve, 0, 0, 1.0f);
        }

        public void render(GuiGraphics guiGraphics, Inventory inventory) {
            float enter = this.enterHolder.getValue();
            int value = (int) this.amountHolder.getValue();
            int count = inventory.countItem(currency.item) - this.shrink;

            if (count != this.amountAnimation.getDest()) {
                if (this.enterAnimation.getDest() != 1f) {
                    this.show();
                } else {
                    this.amountAnimation.reset(value, count);
                    ScreenAnimator.GLOBAL.play(this.amountAnimation);
                    this.counter = 40;
                }
            }

            if (enter == 0) {
                return;
            }

            if (!show && !this.amountAnimation.isRunning() && !this.enterAnimation.isRunning()) {
                if (counter > 0) {
                    counter --;
                } else {
                    this.hide();
                }
            }

            RenderSystem.setShaderColor(1, 1, 1, enter);
            PoseStack pose = guiGraphics.pose();
            pose.pushPose();
            float y = enter * 12;
            pose.translate(0f, y * 3, 0f);
            if (this.amountAnimation.isRunning()) {
                pose.scale(1.0f, 1.0f + Mth.sin((this.amountHolder.getValue() - value) * Mth.PI * 4) * 0.1f, 1.0f);
            }
            pose.translate(0f, -y, 0f);
            guiGraphics.blit(currency.icon, 0, 0, 16, 16, 0, 0, 16, 16, 16, 16);
            if (value > 0) {
                pose.translate(12, 16, 0);
                pose.scale(0.5f, 0.5f, 1.0f);
                guiGraphics.drawString(minecraft.font, String.valueOf(value), 0, 0, Palette.NORMAL_TEXT_COLOR);
            }
            pose.popPose();
            RenderSystem.setShaderColor(1, 1, 1, 1);
        }

        private void show() {
            this.enterAnimation.reset(this.enterHolder.get(), 1f);
            ScreenAnimator.GLOBAL.play(this.enterAnimation);
        }

        private void hide() {
            this.enterAnimation.reset(this.enterHolder.get(), 0f);
            ScreenAnimator.GLOBAL.play(this.enterAnimation);
        }

        public boolean isHidden() {
            return this.enterHolder.getValue() != 1.0f;
        }

        public void setShow(boolean shouldShow) {
            this.show = shouldShow;

            if (shouldShow) {
                this.show();
            } else {
                this.hide();
            }
        }

        public void shrink(int amount) {
            this.shrink += amount;
        }

        public void refund() {
            this.shrink = 0;
        }

        public void setShrink(int i) {
            this.shrink = i;
        }
    }

    private static class AnimationObject extends AbstractScreenAnimation {
        public static final float LENGTH = .75f;
        private float curve = 1.0f;
        private float x;
        private float y;
        private float left;
        private float right;
        private float up;
        private float down;
        private boolean backward = false;
        private ResourceLocation icon;
        public AnimationObject() {
            super(LENGTH);
        }

        @Override
        protected void run(float time) {
            if (backward) {
                time = LENGTH - time;
            }
            this.x = Mth.clamp(time / LENGTH, 0.0f, 1.0f);
            this.y = (1 - curve) * (this.x * this.x) + this.x * curve;
        }

        public float getX() {
            return x;
        }

        public float getY() {
            return y;
        }

        public void reset(float curve, float left, float up, float right, float down, ResourceLocation icon, boolean backward) {
            this.curve = curve;
            this.icon = icon;
            this.backward = backward;
            this.left = left;
            this.up = up;
            this.right = right;
            this.down = down;
        }

        public void render(GuiGraphics guiGraphics) {
            float alpha = Palette.getShaderAlpha();

            double offX = AnimateUtils.Lerp.linear(this.left, this.right, this.x);
            double offY = AnimateUtils.Lerp.linear(this.up, this.down, this.y);

            float sin = Mth.sin(this.x * Mth.PI);
            Palette.setShaderAlpha(sin * sin);
            guiGraphics.pose().translate(offX, offY, 0.0);
            guiGraphics.blit(this.icon, 0, 0, 24, 24, 0, 0, 16, 16, 16, 16);
            guiGraphics.pose().translate(-offX, -offY, 0.0);
            Palette.setShaderAlpha(alpha);
        }
    }
}
