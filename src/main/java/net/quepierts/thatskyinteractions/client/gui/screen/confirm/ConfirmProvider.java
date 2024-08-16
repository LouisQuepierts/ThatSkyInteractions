package net.quepierts.thatskyinteractions.client.gui.screen.confirm;

import net.minecraft.client.gui.GuiGraphics;

public interface ConfirmProvider {
    void render(GuiGraphics guiGraphics, int width, int height);

    void confirm();

    void cancel();
}
