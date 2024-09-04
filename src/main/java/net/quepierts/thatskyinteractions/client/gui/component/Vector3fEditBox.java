package net.quepierts.thatskyinteractions.client.gui.component;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.joml.Vector3f;

import java.util.function.Consumer;

@OnlyIn(Dist.CLIENT)
public class Vector3fEditBox {
    private final ScrollableEditBox editX;
    private final ScrollableEditBox editY;
    private final ScrollableEditBox editZ;

    public Vector3fEditBox(Font font, int x, int y, int width, int height, Vector3f def, Vector3f min, Vector3f max) {
        int part = width / 3;
        this.editX = new ScrollableEditBox(font, Component.empty(), x, y, part, height, def.x, min.x, max.x);
        this.editY = new ScrollableEditBox(font, Component.empty(), x + part, y, part, height, def.y, min.y, max.y);
        this.editZ = new ScrollableEditBox(font, Component.empty(), x + part * 2, y, part, height, def.z, min.z, max.z);
    }

    public void init(Consumer<AbstractWidget> consumer) {
        consumer.accept(this.editX);
        consumer.accept(this.editY);
        consumer.accept(this.editZ);
    }

    public Vector3f getVector3f() {
        return new Vector3f(
                editX.getFloatValue(),
                editY.getFloatValue(),
                editZ.getFloatValue()
        );
    }

    public void set(Vector3f vector3f) {
        this.editX.setFloatValue(vector3f.x);
        this.editY.setFloatValue(vector3f.y);
        this.editZ.setFloatValue(vector3f.z);
    }
}
