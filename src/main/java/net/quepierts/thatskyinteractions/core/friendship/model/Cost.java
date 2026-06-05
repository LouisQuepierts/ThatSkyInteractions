package net.quepierts.thatskyinteractions.core.friendship.model;

import net.quepierts.thatskyinteractions.core.model.Currency;

public record Cost(
        Currency currency,
        int amount
) {
    public static final Cost FREE = new Cost(Currency.WHITE_CANDLE, 0);

    public boolean isFree() {
        return this == FREE || this.amount < 1;
    }
}
