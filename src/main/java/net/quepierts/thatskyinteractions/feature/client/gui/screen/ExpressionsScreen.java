package net.quepierts.thatskyinteractions.feature.client.gui.screen;

import dev.anvilcraft.lib.v2.rendering.sdf.SdfGraphics;
import lombok.Setter;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.quepierts.thatskyinteractions.feature.client.gui.component.control.Button;
import net.quepierts.thatskyinteractions.feature.client.gui.component.layout.GridPane;
import net.quepierts.thatskyinteractions.feature.client.gui.component.layout.VBox;
import net.quepierts.thatskyinteractions.feature.client.gui.component.visual.VisualNode;
import net.quepierts.thatskyinteractions.infra.animation.tween.TweenHandle;
import net.quepierts.thatskyinteractions.infra.animation.tween.ease.Eases;
import net.quepierts.thatskyinteractions.infra.animation.tween.interpolate.Interpolators;
import net.quepierts.thatskyinteractions.core.model.ui.Alignment;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public final class ExpressionsScreen extends AnimatableScreen {

    private @Nullable TweenHandle slide;

    private int sliderWide          = 160;

    @Setter
    private float transition        = 0.0f;

    public ExpressionsScreen() {
        super(Component.translatable("menu.thatskyinteractions.expressions"));
    }

    @Override
    protected void init() {
        super.init();

        VisualNode visualNode = (control, graphics, mouseX, mouseY, delta) -> {
            final var x = control.getX() + control.getWidth() / 2;
            final var y = control.getY() + control.getHeight() / 2;

            final var sdf = SdfGraphics.getInstance()
                    .reset()
                    .center(true)
                    .round(6.0f)
                    .color(0x80101010)
                    .box(
                            x, y,
                            control.getWidth(),
                            control.getHeight()
                    )
                    .draw(graphics);

            final var over = sdf.collide(mouseX, mouseY, 0.1f);

            if (over) {
                sdf.color(0xfffffee0)
                        .round(5.0f)
                        .box(
                                x, y,
                                control.getWidth() - 2,
                                control.getHeight() - 2
                        )
                        .stroke(1.0f)
                        .light(2.5f)
                        .draw(graphics);
            }
        };

        this.slide = this.tween().to(
                this::setTransition,
                0.0f,
                1.0f,
                0.5f,
                Interpolators.FLOAT,
                Eases.QUAD_OUT
        );

        var vBox     = new VBox(0, 0, this.sliderWide, this.height, Component.empty());
        vBox.setAlignment(Alignment.TOP_CENTER);
        final var padding = vBox.getPadding();
        padding.top = 16;

        var grid = new GridPane(0, 0, 0, 0, Component.empty());
        grid.setAlignment(Alignment.TOP_CENTER);
        grid.setHgap(4);
        grid.setVgap(4);

        final var maxColumn = 4;
        var row             = 0;
        var column          = 0;

        for (int i = 0; i < 23; i++) {
            var button = new Button(0, 0, 24, 24, Component.empty());
            button.setVisualNodes(visualNode);

            // calculate row and column
            if (column == maxColumn) {
                row++;
                column = 0;
            }

            grid.add(
                    button,
                    column,
                    row
            );

            column++;
        }

        grid.fit();

        vBox.addChild(grid);
        vBox.layout();
        this.addRenderableWidget(vBox);
    }

    @Override
    public void extractAnimatableRenderState(
            final @NonNull GuiGraphicsExtractor graphics,
            final int mouseX,
            final int mouseY,
            final float delta
    ) {

        final var pose = graphics.pose();
        final var x = this.width - this.transition * this.sliderWide;

        pose.pushMatrix();
        pose.translate(x, 0);
        // at the right side
        graphics.fill(
                0,
                0,
                this.sliderWide,
                this.height,
                0xc0101010
        );

        super.extractAnimatableRenderState(graphics, (int) (mouseX - x), mouseY, delta);

        pose.popMatrix();

    }

    @Override
    public void onClose() {
        this.slide = this.tween().to(
                this::setTransition,
                this.transition,
                0.0f,
                0.5f,
                Interpolators.FLOAT,
                Eases.QUAD_OUT
        );

        super.onClose();
    }

    @Override
    public boolean isAnimating() {
        return this.slide != null && !this.slide.isFinished();
    }

    @Override
    public void extractBackground(
            final @NonNull GuiGraphicsExtractor graphics,
            final int mouseX,
            final int mouseY,
            final float delta
    ) {

    }
}
