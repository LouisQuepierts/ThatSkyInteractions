package net.quepierts.thatskyinteractions.feature.client.gui.component.control;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.quepierts.thatskyinteractions.core.model.ui.HPos;
import net.quepierts.thatskyinteractions.feature.client.gui.ColorStack;
import net.quepierts.thatskyinteractions.infra.animation.tween.TweenScope;
import org.jspecify.annotations.NonNull;

public class TextBlock extends Control {

    private final   Component[]     lines;
    private final   int[]           widths;
    private final   int             spacing;

    @Setter
    @Getter
    private         HPos            alignment   = HPos.LEFT;


    public TextBlock(
            final TweenScope    tween,
            final int           x,
            final int           y,
            final int           spacing,
            final Component...  lines
    ) {
        super(tween, x, y, 0, 0, lines[0]);

        this.lines              = lines;
        this.widths             = new int[lines.length + 1];
        this.spacing            = spacing;

        final var font          = Minecraft.getInstance().font;
        final var lineHeight    = font.lineHeight + spacing;

        final var height        = lineHeight * lines.length - spacing;
        var width               = 0;

        for (int i = 0; i < lines.length; i++) {
            final var line      = lines[i];
            final var lineWidth = font.width(line);
            this.widths[i]      = lineWidth;
            if (lineWidth > width) {
                width           = lineWidth;
            }
        }

        this.setSize(width, height);

    }

    @Override
    protected void extractControlRenderState(
            final @NonNull GuiGraphicsExtractor graphics,
            final @NonNull ColorStack           colors,
            final int                           mouseX,
            final int                           mouseY,
            final float                         delta
    ) {
        super.extractControlRenderState(
                graphics,
                colors,
                mouseX,
                mouseY,
                delta
        );

        // draw lines
        final var font          = Minecraft.getInstance().font;
        final var lineHeight    = font.lineHeight + this.spacing;

        switch (this.alignment) {
            case LEFT: {
                for (int i = 0; i < this.lines.length; i++) {
                    final var line = this.lines[i];
                    final var x = this.getX();
                    final var y = this.getY() + i * lineHeight;

                    graphics.text(
                            font,
                            line,
                            x,
                            y,
                            0xFFFFFFFF
                    );
                }
                break;
            }
            case CENTER: {
                final var baseX = this.getX() + this.getWidth() / 2;
                for (int i = 0; i < this.lines.length; i++) {
                    final var line = this.lines[i];
                    final var x = baseX - this.widths[i] / 2;
                    final var y = this.getY() + i * lineHeight;

                    graphics.text(
                            font,
                            line,
                            x,
                            y,
                            0xFFFFFFFF
                    );
                }
                break;
            }
            case RIGHT: {
                final var baseX = this.getX() + this.getWidth();
                for (int i = 0; i < this.lines.length; i++) {
                    final var line = this.lines[i];
                    final var x = baseX - this.widths[i];
                    final var y = this.getY() + i * lineHeight;

                    graphics.text(
                            font,
                            line,
                            x,
                            y,
                            0xFFFFFFFF
                    );
                }
                break;
            }
        }
    }
}
