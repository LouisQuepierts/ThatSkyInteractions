package net.quepierts.thatskyinteractions.feature.data.friendship;

import com.google.common.collect.ImmutableMap;
import io.netty.buffer.ByteBuf;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.quepierts.thatskyinteractions.core.model.friendship.FriendshipTreeDefinition;
import net.quepierts.thatskyinteractions.feature.data.DataSyncManager;
import net.quepierts.thatskyinteractions.feature.data.DataSyncSystem;
import org.jspecify.annotations.NonNull;

import java.util.Map;

@Slf4j
public final class FriendshipTreeManager extends DataSyncManager<FriendshipTreeDefinition> {

    public static final String FOLDER = "friendship/tree";

    public static final StreamCodec<ByteBuf, Map<Identifier, FriendshipTreeDefinition>> STREAM_CODEC
            = createStreamCodec(FriendshipTreeParser.TREE_STREAM_CODEC);

    private static final FriendshipTreeManager INSTANCE
            = DataSyncSystem.register(FriendshipTreeManager::new);

    private Map<Identifier, FriendshipTree> trees = Map.of();

    public static FriendshipTreeManager getInstance() {
        return INSTANCE;
    }

    FriendshipTreeManager() {
        super(
                FriendshipTreeParser.TREE_CODEC,
                FOLDER
        );
    }

    @Override
    protected void apply(@NonNull final Map<Identifier, FriendshipTreeDefinition> preparations) {
        final var builder = ImmutableMap.<Identifier, FriendshipTree>builder();
        for (final var entry : preparations.entrySet()) {
            final var tree = FriendshipTree.of(entry.getValue());
            builder.put(entry.getKey(), tree);
        }
        this.trees = builder.build();

        log.info("Loaded {} friendship trees", this.trees.size());
    }

    @Override
    protected @NonNull StreamCodec<ByteBuf, Map<Identifier, FriendshipTreeDefinition>> getStreamCodec() {
        return STREAM_CODEC;
    }
}
