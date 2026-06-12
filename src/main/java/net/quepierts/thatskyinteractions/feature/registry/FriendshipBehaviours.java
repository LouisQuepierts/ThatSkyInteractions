package net.quepierts.thatskyinteractions.feature.registry;

import lombok.experimental.UtilityClass;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.feature.friendship.behaviour.*;
import net.quepierts.thatskyinteractions.feature.registry.builer.FriendshipBehaviourBuilder;
import net.quepierts.thatskyinteractions.feature.registry.entry.FriendshipBehaviourEntry;
import org.jspecify.annotations.NonNull;

@UtilityClass
public class FriendshipBehaviours {

    public static final FriendshipBehaviourEntry<InteractionBehaviour> INTERACTION
            = create(InteractionBehaviour.TYPE, InteractionBehaviour.INSTANCE);

    public static final FriendshipBehaviourEntry<FriendBehaviour> FRIEND
            = create(FriendBehaviour.TYPE, FriendBehaviour.INSTANCE);

    public static final FriendshipBehaviourEntry<BlockBehaviour> BLOCK
            = create(BlockBehaviour.TYPE, BlockBehaviour.INSTANCE);

    public static final FriendshipBehaviourEntry<LockBehaviour> LOCK
            = create(LockBehaviour.TYPE, LockBehaviour.INSTANCE);

    public static final FriendshipBehaviourEntry<HandholdBehaviour> HANDHOLD
            = create(HandholdBehaviour.TYPE, HandholdBehaviour.INSTANCE);

    public static void register() { }

    private static <T extends FriendshipBehaviour> FriendshipBehaviourEntry<T> create(
            final @NonNull String       name,
            final @NonNull T            constant
    ) {
        return ThatSkyInteractions.REGISTRUM.entry(
                name,
                callback -> new FriendshipBehaviourBuilder<>(
                        ThatSkyInteractions.REGISTRUM,
                        ThatSkyInteractions.REGISTRUM,
                        name,
                        callback,
                        () -> constant
                )
        ).register();
    }
}
