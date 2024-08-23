package net.quepierts.thatskyinteractions.client.gui.screen.confirm;

import net.minecraft.client.gui.GuiGraphics;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface ConfirmProvider {
    void render(GuiGraphics guiGraphics, int width, int height);

    void confirm();

    void cancel();
}
