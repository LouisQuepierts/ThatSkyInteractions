package net.quepierts.thatskyinteractions.feature.friendship.behaviour;

import com.google.common.collect.ImmutableMap;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.neoforged.neoforge.common.NeoForge;
import net.quepierts.thatskyinteractions.core.friendship.model.FriendshipTreeNode;
import net.quepierts.thatskyinteractions.feature.friendship.event.RegisterFriendshipBehaviourEvent;
import org.jspecify.annotations.Nullable;

import java.util.Map;

@UtilityClass
public class FriendshipBehaviourFactory {

    private static final Map<String, FriendshipBehaviour> BEHAVIOURS;
    public static @Nullable FriendshipBehaviour get(
            @NonNull final FriendshipTreeNode node
    ) {
        final var type  = node.getType();
        return          BEHAVIOURS.get(type);
    }

    public static void register() { }

    static {
        final var builder = ImmutableMap.<String, FriendshipBehaviour>builder();

        builder.put(InteractionBehaviour.TYPE,  InteractionBehaviour.INSTANCE);
        builder.put(FriendBehaviour.TYPE,       FriendBehaviour.INSTANCE);
        builder.put(BlockBehaviour.TYPE,        BlockBehaviour.INSTANCE);

        NeoForge.EVENT_BUS.post(new RegisterFriendshipBehaviourEvent(builder));
        BEHAVIOURS = builder.build();
    }

}
