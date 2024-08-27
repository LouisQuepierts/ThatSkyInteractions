package net.quepierts.thatskyinteractions.client.gui.component;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.quepierts.thatskyinteractions.client.util.RenderUtils;

public class MessageBox extends LayoutObject implements CulledRenderable {
    private final Component message;

    public MessageBox(int xPos, int yPos, int width, int height, Component message) {
        super(xPos, yPos, width, height);
        this.message = message;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int i, int i1, float v) {
        RenderUtils.fillRoundRect(guiGraphics, getX(), getY(), getWidth(), getWidth(), 0.1f, 0xc0101010);
    }
}
