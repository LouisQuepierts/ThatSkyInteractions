package net.quepierts.thatskyinteractions.feature.friendship.behaviour;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.quepierts.thatskyinteractions.core.friendship.model.FriendshipTreeNode;
import net.quepierts.thatskyinteractions.core.friendship.model.NodeState;
import net.quepierts.thatskyinteractions.core.model.Currency;
import net.quepierts.thatskyinteractions.feature.friendship.PlayerFriendshipAttachment;
import org.jspecify.annotations.NonNull;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class InteractionBehaviour implements FriendshipBehaviour {

    public static final InteractionBehaviour    INSTANCE    = new InteractionBehaviour();
    public static final String                  TYPE        = "interaction";

    @Override
    public void execute(
            final @NonNull  Player                      requester,
            final @NonNull  Player                      receiver,
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

    @Override
    public @NonNull Component getUnlockMessage(
            final @NonNull FriendshipTreeNode           node
    ) {

        // format: "id" or "namespace:id"
        // required: "id"
        final var identifier    = node.getMetadata().get("interaction");
        final var idx           = identifier.indexOf(':');
        final var name          = idx == -1 ? identifier : identifier.substring(idx + 1);

        return Component.translatable(
                "gui.thatskyinteractions.message.unlock.interaction.request",
                Component.object(node.getCost().currency() == Currency.WHITE_CANDLE ? SPRITE_CANDLE : SPRITE_ACS)
                        .withStyle(Styles.SHADOWLESS),
                Component.translatable("interaction.thatskyinteractions." + name)
                        .withColor(FriendshipBehaviour.HIGHLIGHT_TEXT_COLOR)
                        .withStyle(Styles.BOLD)
        ).withColor(FriendshipBehaviour.NORMAL_TEXT_COLOR);

    }
}
