package net.quepierts.thatskyinteractions.feature.client.gui.component.sky.friendship;

import net.quepierts.thatskyinteractions.feature.client.gui.component.control.Button;
import net.quepierts.thatskyinteractions.feature.client.gui.component.control.Control;
import org.jspecify.annotations.NonNull;

import java.util.List;

public record FriendshipTreeComponents(
        @NonNull List<Control> lines,
        @NonNull List<Button> buttons,
        int height
) { }
