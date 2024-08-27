package net.quepierts.thatskyinteractions.client.gui.component;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;

public class RoundInputField extends EditBox {
    public RoundInputField(Font font, int width, int height, Component message) {
        super(font, width, height, message);
    }
}
