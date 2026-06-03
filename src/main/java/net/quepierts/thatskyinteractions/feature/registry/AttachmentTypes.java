package net.quepierts.thatskyinteractions.feature.registry;

import dev.anvilcraft.lib.v2.registrum.util.entry.data.AttachmentEntry;
import lombok.experimental.UtilityClass;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.feature.animation.PlayerAnimationAttachment;
import net.quepierts.thatskyinteractions.feature.friendship.PlayerFriendshipAttachment;
import net.quepierts.thatskyinteractions.feature.interaction.PlayerInteractionAttachment;

@UtilityClass
public class AttachmentTypes {

    public static final AttachmentEntry<PlayerAnimationAttachment> PLAYER_ANIMATION
            = ThatSkyInteractions.REGISTRUM.attachment(
                    "player/animation",
                    PlayerAnimationAttachment::new
            ).register();

    public static final AttachmentEntry<PlayerInteractionAttachment> PLAYER_INTERACTION
            = ThatSkyInteractions.REGISTRUM.attachment(
                    "player/interaction",
                    PlayerInteractionAttachment::new
            )
            .register();

    public static final AttachmentEntry<PlayerFriendshipAttachment> PLAYER_FRIENDSHIP
            = ThatSkyInteractions.REGISTRUM.attachment(
                    "player/friendship",
                    PlayerFriendshipAttachment::new
            )
            .serialize(PlayerFriendshipAttachment.MAP_CODEC)
            .copyOnDeath()
            .register();

    public static void register() { }

}
