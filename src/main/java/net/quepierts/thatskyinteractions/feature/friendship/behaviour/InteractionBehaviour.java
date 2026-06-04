package net.quepierts.thatskyinteractions.feature.friendship.behaviour;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import net.minecraft.resources.Identifier;
import net.quepierts.thatskyinteractions.core.friendship.model.FriendshipTreeNode;
import net.quepierts.thatskyinteractions.core.friendship.model.NodeState;
import net.quepierts.thatskyinteractions.feature.friendship.PlayerFriendshipAttachment;
import org.jspecify.annotations.NonNull;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class InteractionBehaviour implements FriendshipBehaviour {

    public static final InteractionBehaviour    INSTANCE    = new InteractionBehaviour();
    public static final String                  TYPE        = "interaction";

    @Override
    public void execute(
            final @NonNull  PlayerFriendshipAttachment  attachment,
            final @NonNull  FriendshipTreeNode          node
    ) {

    }

    @Override
    public @NonNull Identifier getIcon(
            final @NonNull  PlayerFriendshipAttachment  attachment,
            final @NonNull  FriendshipTreeNode          node,
            final @NonNull  NodeState                   state
    ) {
        final var metadata      = node.getMetadata();
        final var interaction   = metadata.get("interaction");

        final var raw           = Identifier.parse(interaction);
        return Identifier.fromNamespaceAndPath(
                raw.getNamespace(),
                "textures/icon/interaction/" + raw.getPath() + ".png"
        );
    }
}
