package net.quepierts.thatskyinteractions.feature.client.gui.screen;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.core.model.ui.Alignment;
import net.quepierts.thatskyinteractions.core.model.ui.HPos;
import net.quepierts.thatskyinteractions.feature.client.gui.component.control.Button;
import net.quepierts.thatskyinteractions.feature.client.gui.component.control.Control;
import net.quepierts.thatskyinteractions.feature.client.gui.component.control.TextBlock;
import net.quepierts.thatskyinteractions.feature.client.gui.component.layout.HBox;
import net.quepierts.thatskyinteractions.feature.client.gui.component.layout.VBox;
import net.quepierts.thatskyinteractions.feature.client.gui.component.visual.GeneralRenderOps;
import net.quepierts.thatskyinteractions.feature.client.gui.component.visual.GeneralVisualNodes;
import net.quepierts.thatskyinteractions.feature.client.gui.component.visual.HoverNode;
import net.quepierts.thatskyinteractions.feature.client.gui.component.visual.VisualNode;
import net.quepierts.thatskyinteractions.feature.client.gui.component.visual.button.ButtonRenderOps;
import net.quepierts.thatskyinteractions.feature.client.gui.component.visual.button.SqueezeButtonNode;
import net.quepierts.thatskyinteractions.feature.client.gui.controller.ConfirmScreenController;
import net.quepierts.thatskyinteractions.feature.gui.ConfirmData;

public class ConfirmScreen extends AnimatableScreen<ConfirmData, ConfirmScreenController> {

    public static final Identifier ICON_CONFIRM = ThatSkyInteractions.location("textures/gui/confirm.png");
    public static final Identifier ICON_CANCEL  = ThatSkyInteractions.location("textures/gui/cancel.png");

    public ConfirmScreen(
            final ConfirmData   data
    ) {
        super(Component.translatable("screen.thatskyinteractions.confirm"), data);
    }

    @Override
    protected ConfirmScreenController createController(
            final ConfirmData   data
    ) {
        return new ConfirmScreenController(data);
    }

    @Override
    protected Control createView() {

        final var controller    = this.getController();
        final var model         = controller.getModel();

        final var vBox  = new VBox(
                this.tween(),
                0, 0, 280, 140,
                Component.empty()
        );
        vBox.getPadding().set(40, 20);
        vBox.setSpacing(10);
        vBox.setAlignment(Alignment.CENTER);
        vBox.setVisualNode(GeneralVisualNodes.base(0x80000000, 12.0f));

        final var icon  = new Control(
                this.tween(),
                0, 0, 32, 32,
                Component.empty()
        );
        icon.setVisualNode(GeneralVisualNodes.texture(model.icon(), 32, 32));

        final var msg   = new TextBlock(
                this.tween(),
                0, 0,
                2,
                model.message()
        );
        msg.setAlignment(HPos.CENTER);
        msg.getMargin().bottom = 10;

        final var hBox  = new HBox(
                this.tween(),
                0, 0, 0, 0,
                Component.empty()
        );

        hBox.setSpacing(48);

        final var confirm   = new Button(
                this.tween(),
                0, 0,
                36, 36,
                Component.translatable("gui.thatskyinteractions.confirm.confirm")
        );
        confirm.setVisualNode(VisualNode.combine(
                GeneralVisualNodes.base(0x80000000, 5.0f),
                SqueezeButtonNode.of(
                        GeneralRenderOps.texture(ICON_CONFIRM, 32, 32)
                ),
                HoverNode.of(ButtonRenderOps.HOVER)
        ));
        confirm.setOnClick(controller::confirm);

        final var cancel    = new Button(
                this.tween(),
                0, 0,
                36, 36,
                Component.translatable("gui.thatskyinteractions.confirm.cancel")
        );
        cancel.setVisualNode(VisualNode.combine(
                GeneralVisualNodes.base(0x80000000, 5.0f),
                SqueezeButtonNode.of(
                        GeneralRenderOps.texture(ICON_CANCEL, 32, 32)
                ),
                HoverNode.of(ButtonRenderOps.HOVER)
        ));
        cancel.setOnClick(controller::cancel);

        hBox.addChildren(confirm, confirm);
        hBox.fit();

        vBox.addChildren(icon, msg, hBox);
        vBox.fit();

        vBox.setPosition(
                this.width / 2 - vBox.getWidth() / 2,
                this.height / 2 - vBox.getHeight() / 2
        );
        vBox.layout();

        return vBox;
    }

    @Override
    protected void repositionElements() {

        final var root      = this.getRoot();
        final var width     = root.getWidth();
        final var height    = root.getHeight();

        // put layout in the center
        root.setPosition(
                this.width / 2 - width / 2,
                this.height / 2 - height / 2
        );

        super.repositionElements();
    }
}
