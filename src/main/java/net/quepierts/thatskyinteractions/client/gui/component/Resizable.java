package net.quepierts.thatskyinteractions.client.gui.component;

import net.minecraft.client.Minecraft;
import org.jetbrains.annotations.NotNull;

public interface Resizable {
    void resize(@NotNull Minecraft minecraft, int width, int height);
}
