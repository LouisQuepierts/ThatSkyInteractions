package net.quepierts.thatskyinteractions.common.data.astrolabe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.UUIDUtil;

import java.util.UUID;

public record LitRequest(
        UUID sender,
        long requestDay
) {
    public static final Codec<LitRequest> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            UUIDUtil.CODEC.fieldOf("target").forGetter(LitRequest::sender),
            Codec.LONG.fieldOf("day").forGetter(LitRequest::requestDay)
    ).apply(instance, LitRequest::new));

    @Override
    public boolean equals(Object obj) {
        return obj instanceof LitRequest request && request.sender.equals(this.sender);
    }

    @Override
    public int hashCode() {
        return this.sender.hashCode();
    }
}
