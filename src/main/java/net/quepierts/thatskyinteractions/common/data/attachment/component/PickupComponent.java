package net.quepierts.thatskyinteractions.common.data.attachment.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.quepierts.thatskyinteractions.common.block.entity.IPickable;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public record PickupComponent(
        Set<UUID> permanent,
        Set<UUID> refreshable
) implements IComponent<PickupComponent> {
    public static final Codec<PickupComponent> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            UUIDUtil.CODEC.listOf().fieldOf("permanent").forGetter(PickupComponent::getPermanent),
            UUIDUtil.CODEC.listOf().fieldOf("refreshable").forGetter(PickupComponent::getRefreshable)
    ).apply(instance, (permanent, refreshable) -> new PickupComponent(
            new ObjectOpenHashSet<>(permanent),
            new ObjectOpenHashSet<>(refreshable)
    )));

    public static final StreamCodec<ByteBuf, PickupComponent> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.<ByteBuf, UUID>list().apply(UUIDUtil.STREAM_CODEC),
            PickupComponent::getPermanent,
            ByteBufCodecs.<ByteBuf, UUID>list().apply(UUIDUtil.STREAM_CODEC),
            PickupComponent::getRefreshable,
            (permanent, refreshable) -> new PickupComponent(
                    new ObjectOpenHashSet<>(permanent),
                    new ObjectOpenHashSet<>(refreshable)
            )
    );

    public static PickupComponent createInstance() {
        return new PickupComponent(
                new ObjectOpenHashSet<>(),
                new ObjectOpenHashSet<>()
        );
    }

    public boolean isPickedUp(UUID uuid, boolean daily) {
        if (daily) {
            return this.refreshable.contains(uuid);
        } else {
            return this.permanent.contains(uuid);
        }
    }

    public boolean isPickedUp(@NotNull IPickable pickable) {
        return this.isPickedUp(pickable.getUUID(), pickable.isDailyRefresh());
    }

    public void pickUp(@NotNull IPickable pickable) {
        if (pickable.isDailyRefresh()) {
            this.refreshable.add(pickable.getUUID());
        } else {
            this.permanent.add(pickable.getUUID());
        }
    }

    public boolean tryPickUp(@NotNull IPickable pickable) {
        if (this.isPickedUp(pickable)) {
            return false;
        }

        this.pickUp(pickable);
        return true;
    }

    public void unclaim(boolean daily) {
        if (daily) {
            this.refreshable.clear();
        } else {
            this.permanent.clear();
        }
    }

    private List<UUID> getPermanent() {
        return List.copyOf(this.permanent());
    }

    private List<UUID> getRefreshable() {
        return List.copyOf(this.refreshable());
    }

    @Override
    public void setInfo(PickupComponent other) {
        this.permanent().clear();
        this.permanent().addAll(other.permanent());

        this.refreshable().clear();
        this.refreshable().addAll(other.refreshable());
    }

    @Override
    public void update() {
        this.refreshable().clear();
    }
}
