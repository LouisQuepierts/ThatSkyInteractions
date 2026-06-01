package net.quepierts.thatskyinteractions.feature.data.friendship;

import com.google.common.collect.ImmutableMap;
import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.core.model.friendship.FriendshipTreeDefinition;
import net.quepierts.thatskyinteractions.feature.data.DataSyncManager;
import net.quepierts.thatskyinteractions.feature.data.event.RegisterSyncManagerEvent;
import org.jspecify.annotations.NonNull;

import java.util.Map;

@Slf4j
@EventBusSubscriber(modid = ThatSkyInteractions.MODID)
public final class FriendshipTreeManager extends DataSyncManager<FriendshipTreeDefinition> {

    public static final String FOLDER = "friendship/tree";

    public static final StreamCodec<ByteBuf, Map<Identifier, FriendshipTreeDefinition>> STREAM_CODEC
            = createStreamCodec(FriendshipTreeParser.TREE_STREAM_CODEC);

    @Getter
    private static final FriendshipTreeManager instance
            = new FriendshipTreeManager();

    private Map<Identifier, FriendshipTree> trees = Map.of();

    FriendshipTreeManager() {
        super(
                FriendshipTreeParser.TREE_CODEC,
                FOLDER
        );
    }

    @SubscribeEvent
    public static void onRegisterSyncManager(final RegisterSyncManagerEvent event) {
        event.register(instance);
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

    public FriendshipTree get(@NonNull final Identifier identifier) {
        return this.trees.get(identifier);
    }

    @Override
    protected @NonNull StreamCodec<ByteBuf, Map<Identifier, FriendshipTreeDefinition>> getStreamCodec() {
        return STREAM_CODEC;
    }
}
