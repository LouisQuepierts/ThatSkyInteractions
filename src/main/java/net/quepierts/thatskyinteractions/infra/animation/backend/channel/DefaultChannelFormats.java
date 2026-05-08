package net.quepierts.thatskyinteractions.infra.animation.backend.channel;

import lombok.experimental.UtilityClass;

@UtilityClass
public class DefaultChannelFormats {

    public static final ChannelFormat EMPTY     = ChannelFormat.builder().build();

    public static final ChannelFormat TIMELINE  = ChannelFormat.builder()
            .add("cursor", ChannelFormatElement.CURSOR)
            .build();

}
