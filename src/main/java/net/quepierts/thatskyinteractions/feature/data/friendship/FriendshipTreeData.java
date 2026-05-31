package net.quepierts.thatskyinteractions.feature.data.friendship;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.objects.Object2ByteMap;
import it.unimi.dsi.fastutil.objects.Object2ByteOpenHashMap;
import lombok.Getter;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.quepierts.thatskyinteractions.core.model.PlayerPair;
import net.quepierts.thatskyinteractions.feature.data.PlayerPairParser;
import org.jspecify.annotations.NonNull;

import java.util.HashMap;
import java.util.Map;

@Getter
public final class FriendshipTreeData {

    public static final Codec<FriendshipTreeData> CODEC
            = RecordCodecBuilder.create(instance -> instance.group(
                    Identifier.CODEC.fieldOf("type").forGetter(FriendshipTreeData::getType),
                    PlayerPairParser.CODEC.fieldOf("relation").forGetter(FriendshipTreeData::getRelation),
                    Codec.unboundedMap(
                            Codec.STRING,
                            Codec.BYTE
                    ).fieldOf("states").forGetter(FriendshipTreeData::getStates)
            ).apply(instance, FriendshipTreeData::new));

    public static final StreamCodec<ByteBuf, FriendshipTreeData> STREAM_CODEC
            = StreamCodec.composite(
                    Identifier.STREAM_CODEC,
                    FriendshipTreeData::getType,
                    PlayerPairParser.STREAM_CODEC,
                    FriendshipTreeData::getRelation,
                    ByteBufCodecs.map(
                            HashMap::new,
                            ByteBufCodecs.STRING_UTF8,
                            ByteBufCodecs.BYTE
                    ),
                    FriendshipTreeData::getStates,
                    FriendshipTreeData::new
            );

    private transient final FriendshipTree      model;

    private final Identifier                    type;
    private final PlayerPair                    relation;
    private final Object2ByteMap<String>        states;

    private FriendshipTreeData(
            @NonNull final Identifier               type,
            @NonNull final PlayerPair               relation,
            @NonNull final Map<String, Byte>        states
    ) {
        this.type           = type;
        this.relation       = relation;

        final var manager   = FriendshipTreeManager.getInstance();
        this.model          = manager.get(type);
        this.states         = new Object2ByteOpenHashMap<>(states);
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
    }

}
