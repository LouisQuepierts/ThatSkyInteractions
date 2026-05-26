package net.quepierts.thatskyinteractions.core.model.friendship;

import net.quepierts.thatskyinteractions.core.model.Currency;

public record Cost(
        Currency currency,
        int price
) {
    public static final Cost FREE = new Cost(Currency.WHITE_CANDLE, 0);
}
