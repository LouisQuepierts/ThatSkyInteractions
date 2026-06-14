package net.quepierts.thatskyinteractions.feature.client.gui.component.sky.expression;

import net.minecraft.network.chat.Component;
import net.quepierts.thatskyinteractions.feature.client.gui.component.attribute.AttributeKey;
import net.quepierts.thatskyinteractions.feature.client.gui.component.sky.TsiButton;
import net.quepierts.thatskyinteractions.infra.animation.tween.TweenScope;

import java.util.function.IntSupplier;

public class ExpressionButton extends TsiButton {

    public static final AttributeKey<IntSupplier> ATTRIBUTE_LEVELS      = new AttributeKey<>("levels");
    public static final AttributeKey<IntSupplier> ATTRIBUTE_SELECTED    = new AttributeKey<>("selected");

    private final int levels;
    private int counter;

    public ExpressionButton(
            final TweenScope tween,
            final Component message,
            final int levels
    ) {
        super(tween, 0, 0, 32, 32, message);
        this.levels = levels;

        this.setAttribute(ATTRIBUTE_LEVELS, () -> this.levels);
        this.setAttribute(ATTRIBUTE_SELECTED, this::getSelected);
    }

    @Override
    public void onTick(final float delta) {
        if (this.levels == 0) {
            return;
        }

        if (!this.isPressed()) {
            this.counter = 0;
            return;
        }

        this.counter ++;
    }

    public int getSelected() {
        return (this.levels == 0 ? 0 : (this.counter / 80) % this.levels) + 1;
    }
}
