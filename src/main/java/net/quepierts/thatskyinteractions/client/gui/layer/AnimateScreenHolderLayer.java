package net.quepierts.thatskyinteractions.client.gui.layer;

import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.objects.*;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.client.gui.screen.AnimatableScreen;
import net.quepierts.thatskyinteractions.client.gui.screen.AnimatedScreen;

@OnlyIn(Dist.CLIENT)
public class AnimateScreenHolderLayer implements LayeredDraw.Layer {
    public static final ResourceLocation LOCATION = ThatSkyInteractions.getLocation("animate_screens_holder");
    public static final AnimateScreenHolderLayer INSTANCE = new AnimateScreenHolderLayer();
    private final ObjectSet<AnimatableScreen> screens = new ObjectOpenHashSet<>();
    private final ObjectList<AnimatableScreen> open = new ObjectArrayList<>();

    AnimateScreenHolderLayer() {}

    @Override
    public void render(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        //guiGraphics.drawString(Minecraft.getInstance().font, "thatskyinteraction-dev-1.1.0 Teacon 2024", 1, 1, 0xffffffff);
        if (!open.isEmpty()) {
            for (AnimatableScreen screen : open) {
                screen.enter();
                this.screens.add(screen);
            }
            open.clear();
        }

        if (this.screens.isEmpty())
            return;

        Minecraft minecraft = Minecraft.getInstance();
        int i = (int)(
                minecraft.mouseHandler.xpos()
                        * (double)minecraft.getWindow().getGuiScaledWidth()
                        / (double)minecraft.getWindow().getScreenWidth()
        );
        int j = (int)(
                minecraft.mouseHandler.ypos()
                        * (double)minecraft.getWindow().getGuiScaledHeight()
                        / (double)minecraft.getWindow().getScreenHeight()
        );

        ObjectIterator<AnimatableScreen> iterator = screens.iterator();
        float partialTick = deltaTracker.getGameTimeDeltaPartialTick(false);
        while (iterator.hasNext()) {
            AnimatableScreen screen = iterator.next();
            screen.getAnimator().tick();
            screen.irender(guiGraphics, i, j, partialTick);

            if (!screen.getAnimator().isRunning())
                iterator.remove();
        }

        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.disableBlend();
    }
    
    public void open(AnimatableScreen screen) {
        this.open.add(screen);
    }

    public void remove(AnimatableScreen screen) {
        screens.remove(screen);
    }

    public void close(AnimatableScreen screen) {
        if (this.screens.contains(screen)) {
            screen.hide();
            screen.getAnimator().stop();
        }
    }

    public void push(AnimatableScreen screen) {
        if (this.screens.contains(screen))
            return;

        if (screen instanceof Screen) {
            Screen current = Minecraft.getInstance().screen;
            if (current == null) {
                Minecraft.getInstance().setScreen((Screen) screen);
            } else if (current instanceof AnimatableScreen animatableScreen) {
                animatableScreen.hide();
                Minecraft.getInstance().pushGuiLayer((Screen) screen);
            }

            this.open.add(screen);
        }
    }

    public void pop(AnimatedScreen screen) {
        if (Minecraft.getInstance().screen == screen) {
            this.close(screen);
        }
        Minecraft.getInstance().popGuiLayer();
        if (Minecraft.getInstance().screen instanceof AnimatableScreen last) {
            last.enter();
        }
    }

    public void reset() {
        this.screens.clear();
        this.open.clear();
    }
}
