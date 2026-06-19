package net.quepierts.thatskyinteractions.feature.animation.binary;

import io.netty.buffer.ByteBuf;
import lombok.experimental.UtilityClass;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.quepierts.veynir.backend.buffer.AnimationBuffer;
import net.quepierts.veynir.backend.model.Timeline;
import net.quepierts.veynir.backend.source.TimelineSource;

@UtilityClass
public class SourceParser {

    public static final byte[] HEADER_TIMELINE = {(byte) 0xf1, (byte) 0x01};

    public static final StreamCodec<ByteBuf, Timeline> TIMELINE = new StreamCodec<>() {
        @Override
        public Timeline decode(final ByteBuf byteBuf) {
            final int size          = ByteBufCodecs.VAR_INT.decode(byteBuf);
            final var starts        = new float[size];
            final var ends          = new float[size];
            final var addr0         = new int[size];
            final var addr1         = new int[size];
            final var interpolation = new byte[size];

            for (int i = 0; i < size; i++) {
                starts[i]           = byteBuf.readFloat();
                ends[i]             = byteBuf.readFloat();
                addr0[i]            = byteBuf.readInt();
                addr1[i]            = byteBuf.readInt();
            }

            byteBuf                 .readBytes(interpolation);

            return new Timeline(
                    starts,
                    ends,
                    addr0,
                    addr1,
                    interpolation,
                    size
            );
        }

        @Override
        public void encode(final ByteBuf output, final Timeline timeline) {
            final var size          = timeline.size();
            final var starts        = timeline.starts();
            final var ends          = timeline.ends();
            final var addr0         = timeline.addr0();
            final var addr1         = timeline.addr1();

            ByteBufCodecs.VAR_INT   .encode(output, size);

            for (int i = 0; i < size; i++) {
                output              .writeFloat(starts[i]);
                output              .writeFloat(ends[i]);
                output              .writeInt(addr0[i]);
                output              .writeInt(addr1[i]);
            }

            output                  .writeBytes(timeline.interpolation());
        }
    };

    public static final StreamCodec<ByteBuf, TimelineSource> SOURCE = new StreamCodec<>() {

        @Override
        public TimelineSource decode(final ByteBuf input) {

            final var       header      = new byte[HEADER_TIMELINE.length];
            input.readBytes(header);

            if (header[0] != HEADER_TIMELINE[0] || header[1] != HEADER_TIMELINE[1]) {
                throw new IllegalArgumentException("Invalid header");
            }

            final int       size        = ByteBufCodecs.VAR_INT.decode(input);
            final float     duration    = ByteBufCodecs.FLOAT.decode(input);
            final boolean   loop        = ByteBufCodecs.BOOL.decode(input);

            final var       timelines   = new Timeline[size];
            final var       channels    = new String[size];

            for (int i = 0; i < size; i++) {
                timelines[i]            = TIMELINE.decode(input);
            }

            for (int i = 0; i < size; i++) {
                channels[i]             = ByteBufCodecs.STRING_UTF8.decode(input);
            }

            final int constantsSize     = ByteBufCodecs.VAR_INT.decode(input);
            final var constants         = new AnimationBuffer(constantsSize);
            final var raw               = constants.getBuffer();

            for (var i = 0; i < constantsSize; i++) {
                raw[i]                  = input.readFloat();
            }

            return new TimelineSource(
                    channels,
                    timelines,
                    constants,
                    loop,
                    duration
            );
        }

        @Override
        public void encode(
                final ByteBuf           output,
                final TimelineSource    source
        ) {

            output                      .writeBytes(HEADER_TIMELINE);

            final var timelines         = source.getTimelines();
            final var channels          = source.getChannels();
            final var size              = timelines.length;

            ByteBufCodecs.VAR_INT       .encode(output, size);
            ByteBufCodecs.FLOAT         .encode(output, source.getDuration());
            ByteBufCodecs.BOOL          .encode(output, source.isLoop());

            for (int i = 0; i < size; i++) {
                TIMELINE.encode(output, timelines[i]);
            }

            for (final var channel : channels) {
                ByteBufCodecs           .STRING_UTF8
                                        .encode(output, channel);
            }

            final var constants         = source.getConstants();
            final var constantsSize     = constants.getSize();
            final var raw               = constants.getBuffer();
            ByteBufCodecs.VAR_INT       .encode(output, constantsSize);

            for (var i = 0; i < constantsSize; i++) {
                output                  .writeFloat(raw[i]);
            }

        }
    };

}
