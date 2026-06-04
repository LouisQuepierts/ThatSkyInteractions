package net.quepierts.thatskyinteractions.feature.client.gui.component.layout;

import lombok.Getter;
import net.minecraft.network.chat.Component;
import net.quepierts.thatskyinteractions.feature.client.gui.component.control.Control;
import net.quepierts.thatskyinteractions.core.model.ui.Alignment;
import net.quepierts.thatskyinteractions.core.model.ui.HPos;
import net.quepierts.thatskyinteractions.core.model.ui.VPos;
import net.quepierts.thatskyinteractions.core.property.EnumProperty;
import net.quepierts.thatskyinteractions.core.property.FloatProperty;
import net.quepierts.thatskyinteractions.infra.animation.tween.TweenScope;
import org.jspecify.annotations.NonNull;

import java.util.HashMap;
import java.util.Map;

public class GridPane extends Pane {

    @Getter
    private final FloatProperty hgap                            = new FloatProperty(0);
    @Getter
    private final FloatProperty vgap                            = new FloatProperty(0);
    @Getter
    private final EnumProperty<Alignment> alignment             = new EnumProperty<>(null);

    private final Map<Control, CellPosition> cellPositions      = new HashMap<>();

    private int rows;
    private int cols;

    public GridPane(
            final @NonNull TweenScope   tween,
            final int                   x,
            final int                   y,
            final int                   width,
            final int                   height,
            final Component             message
    ) {
        super(tween, x, y, width, height, message);
    }

    @Override
    // GridPane 中
    public void fit() {
        final var hgap = this.hgap.get();
        final var vgap = this.vgap.get();

        Map<Integer, Float> colWidths = new HashMap<>();
        Map<Integer, Float> rowHeights = new HashMap<>();

        for (final var child : this.getChildren()) {
            final var pos = cellPositions.get(child);
            if (pos == null) continue;

            final var margin = child.getMargin();
            final var childWidth = child.getWidth() + margin.left + margin.right;
            final var childHeight = child.getHeight() + margin.top + margin.bottom;

            float cellWidth = (childWidth - (pos.colspan - 1) * hgap) / pos.colspan;
            float cellHeight = (childHeight - (pos.rowspan - 1) * vgap) / pos.rowspan;

            for (int c = pos.columnIndex; c < pos.columnIndex + pos.colspan; c++) {
                colWidths.merge(c, cellWidth, Math::max);
            }
            for (int r = pos.rowIndex; r < pos.rowIndex + pos.rowspan; r++) {
                rowHeights.merge(r, cellHeight, Math::max);
            }
        }

        float totalWidth = colWidths.values().stream().reduce(0f, Float::sum);
        totalWidth += Math.max(0, colWidths.size() - 1) * hgap;

        float totalHeight = rowHeights.values().stream().reduce(0f, Float::sum);
        totalHeight += Math.max(0, rowHeights.size() - 1) * vgap;

        final var padding = this.getPadding();

        this.setSize(
                (int) (totalWidth + padding.left + padding.right),
                (int) (totalHeight + padding.top + padding.bottom)
        );
    }


    public void add(
            final Control   child,
            final int       columnIndex,
            final int       rowIndex
    ) {
        add(child, columnIndex, rowIndex, 1, 1);
    }

    public void add(
            Control         child,
            int             columnIndex,
            int             rowIndex,
            int             colspan,
            int             rowspan
    ) {
        this.cellPositions.put(child, new CellPosition(columnIndex, rowIndex, colspan, rowspan));

        this.rows = Math.max(this.rows, rowIndex + rowspan);
        this.cols = Math.max(this.cols, columnIndex + colspan);

        this.addChild(child);
    }

    public void remove(Control child) {
        this.cellPositions.remove(child);
        this.removeChild(child);
    }

    public void setHgap(float hgap) {
        this.hgap.set(hgap);
    }

    public void setVgap(float vgap) {
        this.vgap.set(vgap);
    }

    public void setAlignment(Alignment alignment) {
        this.alignment.set(alignment);
    }

    @Override
    public void layout() {
        final var x             = this.getX();
        final var y             = this.getY();
        final var width         = this.getWidth();
        final var height        = this.getHeight();
        final var hgap          = this.hgap.get();
        final var vgap          = this.vgap.get();
        final var padding       = this.getPadding();

        if (getChildren().isEmpty() || rows == 0 || cols == 0) {
            return;
        }

        final var contentLeft   = x + padding.left;
        final var contentTop    = y + padding.top;
        final var contentWidth  = width - padding.left - padding.right;
        final var contentHeight = height - padding.top - padding.bottom;

        float totalGapWidth     = (cols - 1) * hgap;
        float columnWidth       = (contentWidth - totalGapWidth) / cols;

        float totalGapHeight    = (rows - 1) * vgap;
        float rowHeight         = (contentHeight - totalGapHeight) / rows;

        float totalWidth        = cols * columnWidth + (cols - 1) * hgap;
        float totalHeight       = rows * rowHeight + (rows - 1) * vgap;

        float startX            = contentLeft;
        float startY            = contentTop;

        final var alignment     = this.alignment.get();
        if (alignment != null) {
            final var hpos = alignment.getHPos();
            if (hpos == HPos.CENTER) {
                startX = contentLeft + (contentWidth - totalWidth) / 2;
            } else if (hpos == HPos.RIGHT) {
                startX = contentLeft + (contentWidth - totalWidth);
            }

            final var vpos = alignment.getVPos();
            if (vpos == VPos.CENTER) {
                startY = contentTop + (contentHeight - totalHeight) / 2;
            } else if (vpos == VPos.BOTTOM) {
                startY = contentTop + (contentHeight - totalHeight);
            }
        }

        for (final var child : this.getChildren()) {
            final var pos = cellPositions.get(child);
            if (pos == null) continue;

            final var margin = child.getMargin();
            final var childWidth = child.getWidth();
            final var childHeight = child.getHeight();

            float cellStartX = startX + pos.columnIndex * (columnWidth + hgap);
            float cellStartY = startY + pos.rowIndex * (rowHeight + vgap);

            float cellWidth = pos.colspan * columnWidth + (pos.colspan - 1) * hgap;
            float cellHeight = pos.rowspan * rowHeight + (pos.rowspan - 1) * vgap;

            float childX = cellStartX + (cellWidth - childWidth) / 2;
            float childY = cellStartY + (cellHeight - childHeight) / 2;

            child.setPosition((int) childX, (int) childY);

            if (child instanceof Layout layout) {
                layout.layout();
            }
        }
    }

    private record CellPosition(
            int columnIndex,
            int rowIndex,
            int colspan,
            int rowspan
    ) { }

    public void addRow(int rowIndex, Control... children) {
        for (int i = 0; i < children.length; i++) {
            if (children[i] != null) {
                add(children[i], i, rowIndex);
            }
        }
    }

    public void addColumn(int columnIndex, Control... children) {
        for (int i = 0; i < children.length; i++) {
            if (children[i] != null) {
                add(children[i], columnIndex, i);
            }
        }
    }
}
