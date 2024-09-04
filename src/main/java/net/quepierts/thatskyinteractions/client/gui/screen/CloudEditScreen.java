package net.quepierts.thatskyinteractions.client.gui.screen;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FastColor;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.quepierts.thatskyinteractions.block.entity.CloudBlockEntity;
import net.quepierts.thatskyinteractions.block.entity.ColoredCloudBlockEntity;
import net.quepierts.thatskyinteractions.client.gui.component.Vector3fEditBox;
import org.joml.Vector3f;

@OnlyIn(Dist.CLIENT)
public class CloudEditScreen extends Screen {
    private static final Vector3f MIN_SIZE = new Vector3f(1);
    private static final Vector3f MAX_SIZE = new Vector3f(64);
    private static final Vector3f MIN_OFFSET = new Vector3f(-64);
    private static final Vector3f MAX_OFFSET = new Vector3f(64);
    private static final Vector3f MIN_COLOR = new Vector3f(0);
    private static final Vector3f MAX_COLOR = new Vector3f(255);
    private final CloudBlockEntity cloud;
    private Vector3fEditBox offset;
    private Vector3fEditBox size;
    private Vector3fEditBox color;

    protected CloudEditScreen(Component title, CloudBlockEntity cloud) {
        super(title);
        this.cloud = cloud;
    }

    @Override
    protected void init() {
        this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, (p_251194_) -> {
            this.onDone();
        }).bounds(this.width / 2 - 100, this.height / 4 + 144, 200, 20).build());
        this.size = new Vector3fEditBox(this.font, 0, 0, 300, 40, this.cloud.getSizeF(), MIN_SIZE, MAX_SIZE);
        this.size.init(this::addRenderableWidget);

        this.offset = new Vector3fEditBox(this.font, 0, 60, 300, 40, this.cloud.getOffsetF(), MIN_OFFSET, MAX_OFFSET);
        this.offset.init(this::addRenderableWidget);

        if (this.cloud instanceof ColoredCloudBlockEntity colored) {
            this.color = new Vector3fEditBox(this.font, 0, 120, 300, 40, colored.getOffsetF(), MIN_COLOR, MAX_COLOR);
            this.color.init(this::addRenderableWidget);
        }
    }

    private void onDone() {
        Vector3f offset = this.offset.getVector3f();
        Vector3f size = this.size.getVector3f();
        this.cloud.setOffset((int) offset.x, (int) offset.y, (int) offset.z);
        this.cloud.setSize((int) size.x, (int) size.y, (int) size.z);

        if (this.cloud instanceof ColoredCloudBlockEntity colored) {
            Vector3f color = this.color.getVector3f();
            colored.setColor(FastColor.ARGB32.color((int) color.x, (int) color.y, (int) color.z));
        }

        this.cloud.markUpdate();
    }
}
