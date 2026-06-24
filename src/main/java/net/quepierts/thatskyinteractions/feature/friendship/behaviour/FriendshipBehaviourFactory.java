package net.quepierts.thatskyinteractions.feature.friendship.behaviour;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.quepierts.thatskyinteractions.feature.friendship.FriendshipTreeNode;
import net.quepierts.thatskyinteractions.feature.registry.TsiRegistries;
import org.jspecify.annotations.Nullable;

@UtilityClass
public class FriendshipBehaviourFactory {

    public static @Nullable FriendshipBehaviour get(
            final @NonNull FriendshipTreeNode node
    ) {
        return node.getBehaviour();
    }

}
