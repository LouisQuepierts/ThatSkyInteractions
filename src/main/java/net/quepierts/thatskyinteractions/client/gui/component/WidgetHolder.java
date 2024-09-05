package net.quepierts.thatskyinteractions.client.gui.component;

import net.minecraft.client.gui.components.AbstractWidget;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public interface WidgetHolder {
    void addToParent(@NotNull Consumer<AbstractWidget> consumer);
}
