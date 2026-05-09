package net.quepierts.thatskyinteractions.infra.animation.backend.buffer;

public record AnimationBufferSlice(
        AnimationBuffer buffer,
        int             offset,
        int             length
) {

    public AnimationBufferSlice slice(
            int         offset,
            int         length
    ) {
        if (    offset >= 0 &&
                length >= 0 &&
                offset + length <= this.length) {
            return  new AnimationBufferSlice(
                    this.buffer,
                    this.offset + offset,
                    length
            );
        }

        throw new IllegalArgumentException(
                "Offset of "
                        + offset
                        + " and length "
                        + length
                        + " would put new slice outside existing slice's range (of "
                        + this.offset
                        + ","
                        + this.length
                        + ")"
        );
    }

}
