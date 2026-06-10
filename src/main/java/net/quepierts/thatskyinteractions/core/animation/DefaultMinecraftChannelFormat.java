package net.quepierts.thatskyinteractions.core.animation;

import lombok.experimental.UtilityClass;
import net.quepierts.veynir.backend.channel.ChannelFormat;
import net.quepierts.veynir.backend.channel.ChannelFormatElement;
import net.quepierts.veynir.backend.channel.ChannelLayout;

@UtilityClass
public class DefaultMinecraftChannelFormat {

    public static final ChannelFormat   HUMANOID = ChannelFormat.builder()
            .add("cursor", ChannelFormatElement.CURSOR)
            .add("mask", ChannelFormatElement.MASK)
            .build();

    public static final int             ATTRIBUTE_SIZE  = HUMANOID.getAttributeSize();
    public static final int             OFFSET_CURSOR   = HUMANOID.getOffset(0);
    public static final int             OFFSET_MASK     = HUMANOID.getOffset(1);

}
