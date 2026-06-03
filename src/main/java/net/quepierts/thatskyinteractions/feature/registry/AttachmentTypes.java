package net.quepierts.thatskyinteractions.feature.registry;

import dev.anvilcraft.lib.v2.registrum.util.entry.data.AttachmentEntry;
import lombok.experimental.UtilityClass;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.feature.animation.PlayerAnimationAttachment;
import net.quepierts.thatskyinteractions.feature.interaction.PlayerInteractionData;

@UtilityClass
public class AttachmentTypes {

    public static final AttachmentEntry<PlayerAnimationAttachment> PLAYER_ANIMATION
            = ThatSkyInteractions.REGISTRUM.attachment(
                    "player_animation",
                    PlayerAnimationAttachment::new
            ).register();

    public static final AttachmentEntry<PlayerInteractionData> PLAYER_INTERACTION
            = ThatSkyInteractions.REGISTRUM.attachment(
                    "player_interaction",
                    PlayerInteractionData::new
            )
            .register();

    public static void register() { }

}
