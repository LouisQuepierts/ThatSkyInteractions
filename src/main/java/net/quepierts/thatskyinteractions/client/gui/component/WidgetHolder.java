package net.quepierts.thatskyinteractions.client.gui.component;

import net.minecraft.client.gui.components.AbstractWidget;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

@OnlyIn(Dist.CLIENT)
public interface WidgetHolder {
    void addToParent(@NotNull Consumer<AbstractWidget> consumer);
}
