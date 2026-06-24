package net.quepierts.thatskyinteractions.feature.bond;

import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.lang.ref.WeakReference;
import java.util.Optional;
import java.util.UUID;

public class PlayerCarryRelation {

    private static final WeakReference<Player> NULL = new WeakReference<>(null);

    private @NonNull WeakReference<Player> carried = NULL;
    private @NonNull WeakReference<Player> carrier = NULL;

    public void carry(
            final @NonNull Player   carried
    ) {
        this.carried = new WeakReference<>(carried);
    }

    public void ride(
            final @NonNull Player   carrier
    ) {
        this.carrier = new WeakReference<>(carrier);
    }

    public boolean canCarry(
            final @NonNull Player   player
    ) {
        return this.carried.get() == null && this.carrier.get() != player;
    }

    public boolean canRide(
            final @NonNull Player   player
    ) {
        return this.carrier.get() == null && this.carried.get() != player;
    }

    public void unCarry() {
        this.carried = NULL;
    }

    public void unRide() {
        this.carrier = NULL;
    }

    public boolean isCarried(
            final @NonNull Player   player
    ) {
        return this.carried.get() == player;
    }

    public boolean isCarrier(
            final @NonNull Player   player
    ) {
        return this.carrier.get() == player;
    }

    public boolean isCarrying() {
        return this.carried.get() != null;
    }

    public boolean isBeingCarried() {
        return this.carrier.get() != null;
    }

    public @Nullable Player getCarried() {
        return this.carried.get();
    }

    public @Nullable Player getCarrier() {
        return this.carrier.get();
    }

    public Serialized serialize() {
        final var carrier = this.carrier.get();
        final var carried = this.carried.get();

        return new Serialized(
                carried != null ? Optional.of(carried.getUUID()) : Optional.empty(),
                carrier != null ? Optional.of(carrier.getUUID()) : Optional.empty()
        );
    }

    public void deserialize(
            final @NonNull Serialized   serialized,
            final @NonNull Level        level
    ) {
        this.unCarry();
        this.unRide();
        serialized.carried.ifPresent(uuid -> {
            var player = level.getPlayerByUUID(uuid);
            if (player != null) {
                this.carry(player);
            }
        });
        serialized.carrier.ifPresent(uuid -> {
            var player = level.getPlayerByUUID(uuid);
            if (player != null) {
                this.ride(player);
            }
        });
    }

    public record Serialized(
            @NonNull Optional<UUID> carried,
            @NonNull Optional<UUID> carrier
    ) {

        public static final StreamCodec<ByteBuf, Serialized> STREAM_CODEC
                = StreamCodec.composite(
                    ByteBufCodecs.optional(UUIDUtil.STREAM_CODEC),
                    Serialized::carried,
                    ByteBufCodecs.optional(UUIDUtil.STREAM_CODEC),
                    Serialized::carrier,
                    Serialized::new
                );

    }

}
