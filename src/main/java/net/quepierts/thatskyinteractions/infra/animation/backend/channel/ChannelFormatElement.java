package net.quepierts.thatskyinteractions.infra.animation.backend.channel;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

public record ChannelFormatElement(
        int                         id,
        ChannelFormatElement.Type   type,
        int                         count
) {

    public static final ChannelFormatElement CURSOR     = new ChannelFormatElement(0, Type.INT, 1);
    public static final ChannelFormatElement WEIGHT     = new ChannelFormatElement(1, Type.FLOAT, 1);
    public static final ChannelFormatElement ENABLED    = new ChannelFormatElement(2, Type.BOOL, 1);

    public int size() {
        return this.type.getSize() * this.count;
    }

    @Getter
    @RequiredArgsConstructor(access = lombok.AccessLevel.PRIVATE)
    public enum Type {
        INT(1),
        LONG(2),
        FLOAT(1),
        DOUBLE(2),
        BOOL(1);

        private final int size;
    }
}
