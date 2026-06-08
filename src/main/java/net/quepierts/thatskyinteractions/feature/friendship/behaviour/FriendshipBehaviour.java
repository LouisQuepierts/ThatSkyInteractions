package net.quepierts.thatskyinteractions.feature.friendship.behaviour;

import lombok.experimental.UtilityClass;
import net.minecraft.data.AtlasIds;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.contents.objects.AtlasSprite;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.core.friendship.model.FriendshipTreeNode;
import net.quepierts.thatskyinteractions.core.friendship.model.NodeState;
import net.quepierts.thatskyinteractions.core.model.Currency;
import net.quepierts.thatskyinteractions.feature.friendship.PlayerFriendshipAttachment;
import org.jspecify.annotations.NonNull;

public interface FriendshipBehaviour {

    @NonNull Identifier DEFAULT_ICON    = ThatSkyInteractions.location("textures/icons/none.png");

    @NonNull AtlasSprite SPRITE_CANDLE  = new AtlasSprite(
                                            AtlasIds.ITEMS,
                                            Identifier.withDefaultNamespace("item/candle")
                                        );

    @NonNull AtlasSprite SPRITE_ACS     = new AtlasSprite(
                                            AtlasIds.ITEMS,
                                            Identifier.withDefaultNamespace("item/red_candle")
                                        );


    int NORMAL_TEXT_COLOR               = 0xfff4f5e3;
    int HIGHLIGHT_TEXT_COLOR            = 0xfff67e1e;

    void execute(
            final @NonNull  Player                      requester,
            final @NonNull  Player                      receiver,
            final @NonNull  FriendshipTreeNode          node
    );

    default @NonNull Identifier getIcon(
            final @NonNull  PlayerFriendshipAttachment  attachment,
            final @NonNull  FriendshipTreeNode          node,
            final @NonNull  NodeState                   state
    ) {
        return DEFAULT_ICON;
    }

    default @NonNull Component getUnlockMessage(
            final @NonNull  FriendshipTreeNode          node
    ) {
        return Component.translatable(
                "gui.thatskyinteractions.message.unlock.default.request",
                Component.object(node.getCost().currency() == Currency.WHITE_CANDLE ? SPRITE_CANDLE : SPRITE_ACS)
                        .withStyle(Styles.SHADOWLESS),
                Component.translatable("node.thatskyinteractions." + node.getId())
                        .withColor(FriendshipBehaviour.HIGHLIGHT_TEXT_COLOR)
                        .withStyle(Styles.BOLD)
        ).withColor(FriendshipBehaviour.NORMAL_TEXT_COLOR);
    }

    @UtilityClass
    class Styles {
        public static final Style BOLD          = Style.EMPTY.withBold(true);
        public static final Style SHADOWLESS    = Style.EMPTY.withoutShadow();
    }

}
