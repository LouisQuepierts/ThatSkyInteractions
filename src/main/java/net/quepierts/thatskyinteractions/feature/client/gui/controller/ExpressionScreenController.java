package net.quepierts.thatskyinteractions.feature.client.gui.controller;

import net.quepierts.thatskyinteractions.feature.client.ClientPlayerExpressionSystem;
import net.quepierts.thatskyinteractions.feature.expression.PlayerExpressionManager;

public final class ExpressionScreenController extends ScreenController<Void> {
    public ExpressionScreenController() {
        super(null);
    }

    public void onButtonClicked(final int index, final int level) {
        final var manager       = PlayerExpressionManager.getInstance();
        final var ordinal       = manager.ordinal();

        if (index >= ordinal.size()) {
            return;
        }

        final var identifier    = ordinal.get(index);

        if (identifier == null) {
            return;
        }

        ClientPlayerExpressionSystem.perform(identifier, level);
    }

    @Override
    public void onTick(final float delta) {
        super.onTick(delta);
    }
}
