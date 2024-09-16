package net.quepierts.thatskyinteractions.block;

public enum CandleType {
    CANDLE(2, 8),
    CANDLE_3x3(3, 9),
    CANDLE_4x4A(4, 8),
    CANDLE_4x4B(4, 10),
    CANDLE_5x5(5, 12),
    CANDLE_6x6A(6, 16),
    CANDLE_6x6B(6, 18),
    CANDLE_8x8(8, 22),
    CANDLE_FRAMED(8, 8),
    CANDLE_FRAMED_3x3(3, 9),
    CANDLE_FRAMED_4x4A(4, 8),
    CANDLE_FRAMED_4x4B(4, 10),
    CANDLE_FRAMED_5x5(5, 12),
    CANDLE_FRAMED_6x6A(6, 16),
    CANDLE_FRAMED_6x6B(6, 18),
    CANDLE_FRAMED_8x8(8, 22);

    private final int size;
    private final int height;

    CandleType(int size, int height) {
        this.size = size;
        this.height = height;
    }

    public int getSize() {
        return size;
    }

    public int getHeight() {
        return height;
    }
}
