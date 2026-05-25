package net.quepierts.thatskyinteractions.feature.client.gui.component.control;

import lombok.Setter;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.NonNull;

public class Button extends Control {

    @Setter
    private Runnable onClick;

    public Button(
            final int       x,
            final int       y,
            final int       width,
            final int       height,
            final Component message
    ) {
        super(x, y, width, height, message);
    }

    @Override
    public void onClick(
            final @NonNull MouseButtonEvent event,
            final boolean doubleClick
    ) {
        if (this.onClick != null) {
            this.onClick.run();
        }
    }
}
