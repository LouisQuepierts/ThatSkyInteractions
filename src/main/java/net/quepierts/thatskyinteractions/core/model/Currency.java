package net.quepierts.thatskyinteractions.core.model;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum Currency {
    WHITE_CANDLE("white_candle"),
    ASCENDED_CANDLE("ascended_candle");

    private static final Map<String, Currency> MAP;
    private static final Currency[] ORDINAL;
    private final String id;

    public static Currency parse(String string) {
        return MAP.get(string);
    }

    public static Currency parse(int ordinal) {
        return ORDINAL[ordinal];
    }

    static {
        MAP = Map.of(
                WHITE_CANDLE.id, WHITE_CANDLE,
                ASCENDED_CANDLE.id, ASCENDED_CANDLE
        );
        ORDINAL = Currency.values();
    }

}
