package net.quepierts.thatskyinteractions.feature.registry;

import dev.anvilcraft.lib.v2.registrum.util.entry.data.AttachmentEntry;
import lombok.experimental.UtilityClass;
import net.minecraft.world.entity.Avatar;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.feature.animation.PlayerAnimationSystem;
import net.quepierts.thatskyinteractions.feature.animation.tween.PhysicalTweenAttachment;
import net.quepierts.thatskyinteractions.feature.animation.PlayerAnimationAttachment;
import net.quepierts.thatskyinteractions.feature.expression.PlayerPreferenceAttachment;
import net.quepierts.thatskyinteractions.feature.control.PlayerNavigator;
import net.quepierts.thatskyinteractions.feature.friendship.PlayerFriendshipAttachment;
import net.quepierts.thatskyinteractions.feature.bond.PlayerBondAttachment;
import net.quepierts.thatskyinteractions.feature.expression.PlayerExpressionAttachment;
import net.quepierts.thatskyinteractions.feature.interaction.PlayerInteractionAttachment;

@UtilityClass
public class AttachmentTypes {

    public static final AttachmentEntry<PlayerAnimationAttachment> PLAYER_ANIMATION
            = ThatSkyInteractions.REGISTRUM.attachment(
            "player/animation",
            holder -> {
                if (!(holder instanceof LivingEntity entity)
                        || PlayerAnimationSystem.isNotAnimatable(entity)) {
                    throw new IllegalArgumentException("PlayerAnimation can only attach on Animatable LivingEntity!");
                }
                return new PlayerAnimationAttachment(entity);
            }
    ).register();

    public static final AttachmentEntry<PlayerInteractionAttachment> PLAYER_INTERACTION
            = ThatSkyInteractions.REGISTRUM.attachment(
                    "player/interaction",
                    PlayerInteractionAttachment::new
            )
            .register();

    public static final AttachmentEntry<PlayerNavigator> PLAYER_NAVIGATOR
            = ThatSkyInteractions.REGISTRUM.attachment(
                    "player/navigator",
                    holder -> {
                        if (!(holder instanceof Player player)) {
                            throw new IllegalArgumentException("PlayerNavigator can only attach on Player!");
                        }
                        return new PlayerNavigator(player);
                    }
            )
            .register();

    public static final AttachmentEntry<PlayerFriendshipAttachment> PLAYER_FRIENDSHIP
            = ThatSkyInteractions.REGISTRUM.attachment(
                    "player/friendship",
                    PlayerFriendshipAttachment::new
            )
            .serialize(PlayerFriendshipAttachment.SERIALIZER)
            .sync(PlayerFriendshipAttachment.STREAM_CODEC)
            .copyOnDeath()
            .register();

    public static final AttachmentEntry<PlayerExpressionAttachment> PLAYER_EXPRESSION
            = ThatSkyInteractions.REGISTRUM.attachment(
                    "player/expression",
                    PlayerExpressionAttachment::new
            )
            .register();

    public static final AttachmentEntry<PlayerBondAttachment> PLAYER_BOUND
            = ThatSkyInteractions.REGISTRUM.attachment(
                    "player/bond",
                    PlayerBondAttachment::new
            )
            .register();

    public static final AttachmentEntry<PlayerPreferenceAttachment> PLAYER_PREFERENCE
            = ThatSkyInteractions.REGISTRUM.attachment(
                    "player/preference",
                    PlayerPreferenceAttachment::new
            )
            .serialize(PlayerPreferenceAttachment.MAP_CODEC, PlayerPreferenceAttachment::shouldSerialize)
            .sync(PlayerPreferenceAttachment.STREAM_CODEC)
            .copyOnDeath()
            .register();

    public static final AttachmentEntry<PhysicalTweenAttachment> PHYSICAL_TWEEN
            = ThatSkyInteractions.REGISTRUM.attachment(
                    "tween/physical",
                    PhysicalTweenAttachment::new
            )
            .register();

    public static void register() { }

}
