package net.quepierts.thatskyinteractions.feature.interaction;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.netty.buffer.ByteBuf;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.quepierts.thatskyinteractions.feature.network.StreamCodecUtils;
import org.jspecify.annotations.NonNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class PlayerInteractionData {

    public static final Codec<PlayerInteractionData> CODEC
            = MapCodec.unitCodec(PlayerInteractionData::new);

    // todo
    public static final StreamCodec<ByteBuf, PlayerInteractionData> STREAM_CODEC
            = StreamCodecUtils.unit(PlayerInteractionData::new);

    private final Map<UUID, InteractionRequest> received;
    private InteractionRequest                  sent;

    public PlayerInteractionData() {
        this.received   = new HashMap<>();
    }

    public void sendInvite(
            final @NonNull Player other,
            final @NonNull Identifier type
    ) {

        this.sent = InteractionRequest.send(
                other.getUUID(),
                type,
                other.level().getGameTime()
        );

    }

    public void receiveInvite(
            final @NonNull Player       other,
            final @NonNull Identifier   type
    ) {

        this.received.put(
                other.getUUID(),
                InteractionRequest.receive(
                        other.getUUID(),
                        type,
                        other.level().getGameTime()
                )
        );

    }

    public void cancelSent() {

        this.sent = null;

    }

    public void cancelReceived(
            final @NonNull Player       other
    ) {

        this.received.remove(other.getUUID());

    }

    public void sendAccept(
            final @NonNull Player       other
    ) {

        if (this.hasSentRequest() && this.sent.getOther().equals(other.getUUID())) {
            this.sent = null;
        }

    }

    public void receiveAccept(
            final @NonNull Player       other
    ) {

        this.received.remove(other.getUUID());

    }

    public void tick() {

    }

    public boolean hasSentRequest() {
        return this.sent != null;
    }

    public Collection<InteractionRequest> getReceivedRequests() {
        return this.received.values();
    }

    public boolean hasReceivedRequest(
            final @NonNull UUID other
    ) {
        return this.received.containsKey(other);
    }

}
