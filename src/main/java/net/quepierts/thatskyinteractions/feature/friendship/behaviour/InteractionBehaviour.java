package net.quepierts.thatskyinteractions.feature.friendship.behaviour;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.core.friendship.model.FriendshipTreeNode;
import net.quepierts.thatskyinteractions.core.friendship.model.NodeState;
import net.quepierts.thatskyinteractions.core.model.Currency;
import net.quepierts.thatskyinteractions.feature.friendship.PlayerFriendshipAttachment;
import net.quepierts.thatskyinteractions.feature.gui.packet.PlayerInteractionUiPacket;
import net.quepierts.thatskyinteractions.feature.interaction.Interaction;
import net.quepierts.thatskyinteractions.feature.interaction.PlayerInteractionManager;
import net.quepierts.thatskyinteractions.feature.interaction.PlayerInteractionSystem;
import org.jspecify.annotations.NonNull;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class InteractionBehaviour implements FriendshipBehaviour {

    public static final InteractionBehaviour    INSTANCE    = new InteractionBehaviour();
    public static final String                  TYPE        = "interaction";
    public static final Identifier              DEFAULT     = ThatSkyInteractions.location("none");

    private final Map<String, Identifier>       cache       = new HashMap<>();
    private final Map<String, Identifier>       icons       = new HashMap<>();

    @Override
    public void execute(
            final @NonNull  ServerPlayer                requester,
            final @NonNull  ServerPlayer                receiver,
            final @NonNull  FriendshipTreeNode          node
    ) {

        final var metadata      = node.getMetadata();
        final var interaction   = metadata.get("interaction");
        final var identifier    = this.cache.computeIfAbsent(interaction, Identifier::parse);
        final var level         = metadata.get("level");

        PlayerInteractionSystem.invite(
                requester,
                receiver,
                level == null ? identifier : identifier.withSuffix("_" + level)
        );

        PacketDistributor.sendToPlayer(
                receiver,
                PlayerInteractionUiPacket.invite(
                        requester,
                        this.icon(interaction)
                )
        );

    }

    @Override
    public @NonNull Identifier getIcon(
            final @NonNull  PlayerFriendshipAttachment  attachment,
            final @NonNull  FriendshipTreeNode          node,
            final @NonNull  NodeState                   state
    ) {
        final var metadata      = node.getMetadata();
        final var interaction   = metadata.get("interaction");
        final var identifier    = this.cache.computeIfAbsent(interaction, Identifier::parse);

        final var set           = PlayerInteractionManager.getInstance().getSet(identifier);

        return set == null ? DEFAULT : set.icon();
    }

    @Override
    public @NonNull Component getUnlockMessage(
            final @NonNull FriendshipTreeNode           node
    ) {

        // format: "id" or "namespace:id"
        // required: "id"
        final var metadata      = node.getMetadata();
        final var interaction   = metadata.get("interaction");
        final var identifier    = this.cache.computeIfAbsent(interaction, Identifier::parse);

        final var name          = identifier.getPath();

        return Component.translatable(
                "gui.thatskyinteractions.message.unlock.interaction.request",
                Component.object(node.getCost().currency() == Currency.WHITE_CANDLE ? SPRITE_CANDLE : SPRITE_ACS)
                        .withStyle(Styles.SHADOWLESS),
                Component.translatable("interaction.thatskyinteractions." + name)
                        .withColor(FriendshipBehaviour.HIGHLIGHT_TEXT_COLOR)
                        .withStyle(Styles.BOLD)
        ).withColor(FriendshipBehaviour.NORMAL_TEXT_COLOR);

    }

    private Identifier icon(String interaction) {
        return this.icons.computeIfAbsent(interaction, str -> {
            final var id        = this.cache.computeIfAbsent(str, Identifier::parse);
            return Identifier.fromNamespaceAndPath(
                    id.getNamespace(),
                    "interaction/" + id.getPath() + ".png"
            );
        });
    }
}
