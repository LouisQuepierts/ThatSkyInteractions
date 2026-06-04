package net.quepierts.thatskyinteractions.feature.friendship;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.ints.IntObjectPair;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayFIFOQueue;
import lombok.AccessLevel;
import lombok.Getter;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter(AccessLevel.PRIVATE)
public final class FriendshipTreeData {

    public static final Codec<FriendshipTreeData> CODEC
            = RecordCodecBuilder.create(instance -> instance.group(
                    Identifier.CODEC.fieldOf("type").forGetter(FriendshipTreeData::getType),
                    UUIDUtil.CODEC.fieldOf("friend").forGetter(FriendshipTreeData::getFriend),
                    Codec.unboundedMap(
                            Codec.STRING,
                            Codec.BYTE.xmap(
                                    State::byOrdinal,
                                    State::toByte
                            )
                    ).fieldOf("states").forGetter(FriendshipTreeData::getStates)
            ).apply(instance, FriendshipTreeData::new));

    public static final StreamCodec<ByteBuf, FriendshipTreeData> STREAM_CODEC
            = StreamCodec.composite(
                    Identifier.STREAM_CODEC,
                    FriendshipTreeData::getType,
                    UUIDUtil.STREAM_CODEC,
                    FriendshipTreeData::getFriend,
                    ByteBufCodecs.map(
                            HashMap::new,
                            ByteBufCodecs.STRING_UTF8,
                            ByteBufCodecs.BYTE.map(
                                    State::byOrdinal,
                                    State::toByte
                            )
                    ),
                    FriendshipTreeData::getStates,
                    FriendshipTreeData::new
            );

    private transient final State[]                 flatMapping;
    @Getter // TODO: redirect when data reloaded
    private transient final FriendshipTree          structure;

    @Getter
    private final Identifier                        type;
    @Getter
    private final UUID                              friend;
    private final Object2ObjectMap<String, State>   states;

    public FriendshipTreeData(
            @NonNull final Identifier           type,
            @NonNull final UUID                 friend
    ) {
        this.type           = type;
        this.friend         = friend;

        final var manager   = FriendshipTreeManager.getInstance();
        final var structure = manager.get(type);

        this.structure      = structure;
        this.flatMapping    = new State[structure.size()];
        Arrays.fill(this.flatMapping, State.LOCKED);
        this.flatMapping[0] = State.UNLOCKED;

        this.states         = new Object2ObjectOpenHashMap<>();
    }

    private FriendshipTreeData(
            @NonNull final Identifier           type,
            @NonNull final UUID                 friend,
            @NonNull final Map<String, State>   states
    ) {
        this.type           = type;
        this.friend         = friend;

        final var manager   = FriendshipTreeManager.getInstance();
        final var structure = manager.get(type);
        final var lookup    = structure.getLookup();

        this.structure      = structure;
        this.flatMapping    = new State[structure.size()];
        this.states         = new Object2ObjectOpenHashMap<>(states);

        for (final var entry : states.entrySet()) {
            final var name  = entry.getKey();
            final var value = entry.getValue();

            final var idx   = lookup.find(name);
            if (idx != -1) {
                this.flatMapping[idx] = value;
            }
        }
    }

    public State getState(final int index) {
        return this.flatMapping[index];
    }

    public boolean isLocked(final int index) {
        return this.isValid(index) && this.getState(index) == State.LOCKED;
    }

    public boolean isUnlockable(final int index) {
        return this.isValid(index) && this.getState(index) == State.UNLOCKABLE;
    }

    public boolean isUnlocked(final int index) {
        return this.isValid(index) && this.getState(index) == State.UNLOCKED;
    }

    public boolean unlock(final int index) {
        if (!this.isUnlockable(index)) {
            return false;
        }

        this.flatMapping[index] = State.UNLOCKED;
        this.states             .put(this.structure.get(index).getId(), State.UNLOCKED);

        this                    .update(index);

        return true;
    }

    public State getState(final @NonNull String name) {
        return this.states.get(name);
    }

    public void reset() {
        Arrays.fill(this.flatMapping, State.LOCKED);
        this.flatMapping[0] = State.UNLOCKED;

        this.states.clear();
        this.states.put(this.structure.getRoot().getId(), State.UNLOCKED);

        this.update(0);
    }

    public boolean isEmpty() {
        return this.states.isEmpty();
    }

    public boolean isCompleted() {
        var completed = true;
        for (final var value : this.states.values()) {
            if (value != State.UNLOCKED) {
                completed = false;
                break;
            }
        }
        return completed;
    }

    @Override
    public boolean equals(final Object obj) {
        return (obj instanceof FriendshipTreeData data) && data.friend.equals(this.friend);
    }

    @Override
    public int hashCode() {
        return this.friend.hashCode();
    }

    private boolean isValid(final int index) {
        return index >= 0 && index < this.structure.size();
    }

    private void update(final int src) {

        final var queue = new ObjectArrayFIFOQueue<IntObjectPair<State>>();
        queue           .enqueue(
                        IntObjectPair.of(
                                src,
                                State.byUnlocked(this.isUnlocked(src))
                        )
        );

        while (!queue.isEmpty()) {
            final var pair  = queue.dequeue();
            final var index = pair.leftInt();
            final var state = State.byUnlocked(this.isUnlocked(index), pair.right());

            final var node  = this.structure.get(index);
            final var put   = node.getUnlockCost().isFree() ? State.UNLOCKED : state;
            this            .put(index, put);

            final var next  = state.next();

            if (node.hasMiddle()) {
                queue.enqueue(
                        IntObjectPair.of(
                                node.getMiddle(),
                                next
                        )
                );
            }

            if (node.hasLeft()) {
                queue.enqueue(
                        IntObjectPair.of(
                                node.getLeft(),
                                next
                        )
                );
            }

            if (node.hasRight()) {
                queue.enqueue(
                        IntObjectPair.of(
                                node.getRight(),
                                next
                        )
                );
            }
        }

    }

    private void put(
            final int   index,
            final State state
    ) {
        this.flatMapping[index] = state;

        final var name          = this.structure.get(index).getId();
        if (state == State.LOCKED) {
            this.states.remove(name);
        } else {
            this.states.put(name, state);
        }
    }

    public enum State {
        LOCKED,
        UNLOCKABLE,
        UNLOCKED;

        private static final State[] VALUES = values();

        public static State byOrdinal(
                final byte id
        ) {
            return VALUES[id];
        }

        public static State byUnlocked(
                final boolean   unlocked,
                final State     $default
        ) {
            return unlocked ? UNLOCKED : $default;
        }

        public static State byUnlocked(
                final boolean unlocked
        ) {
            return byUnlocked(unlocked, LOCKED);
        }

        public State next() {
            return VALUES[(ordinal() + 1) % VALUES.length];
        }

        public boolean hasNext() {
            return this != UNLOCKED;
        }

        public byte toByte() {
            return (byte) ordinal();
        }
    }

}
