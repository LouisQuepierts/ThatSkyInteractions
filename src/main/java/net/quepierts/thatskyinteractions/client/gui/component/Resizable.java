package net.quepierts.thatskyinteractions.client.gui.component;

import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public interface Resizable {
    void resize(@NotNull Minecraft minecraft, int width, int height);
}
