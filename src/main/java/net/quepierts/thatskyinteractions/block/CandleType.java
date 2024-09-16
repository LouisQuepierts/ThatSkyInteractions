package net.quepierts.thatskyinteractions.block;

public enum CandleType {
    T_STANDARD(2, 8, false),
    T_3x3(3, 9, false),
    T_4x4(4, 8, false),
    T_5x5A(5, 10, false),
    T_5x5B(5, 12, false),
    T_6x6A(6, 16, false),
    T_6x6B(6, 18, false),
    T_8x8(8, 22, false),
    T_FRAMED_STANDARD(2, 8, true),
    T_FRAMED_3x3(3, 9, true),
    T_FRAMED_4x4(4, 8, true),
    T_FRAMED_5x5A(5, 10, true),
    T_FRAMED_5x5B(5, 12, true),
    T_FRAMED_6x6A(6, 16, true),
    T_FRAMED_6x6B(6, 18, true),
    T_FRAMED_8x8(8, 22, true);

    private final int size;
    private final int height;
    private final boolean framed;

    CandleType(int size, int height, boolean framed) {
        this.size = size;
        this.height = height;
        this.framed = framed;
    }

    public int getSize() {
        return size;
    }

    public int getHeight() {
        return height;
    }

    public boolean isFramed() {
        return framed;
    }
}
