package net.quepierts.thatskyinteractions.feature.animation.tween;

import lombok.experimental.UtilityClass;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.feature.registry.AttachmentTypes;

@UtilityClass
@EventBusSubscriber(modid = ThatSkyInteractions.MODID)
public class PhysicalTweenHandler {

    @SubscribeEvent
    public static void beforeLevelTick(final LevelTickEvent.Pre event) {
        final var level         = event.getLevel();
        final var attachment    = PhysicalTweenAttachment.getAttachment(level);

        attachment.tick(0.05f);
    }

    @SubscribeEvent
    public static void beforePlayerTick(final PlayerTickEvent.Pre event) {
        final var player        = event.getEntity();
        final var attachment    = player.getExistingDataOrNull(AttachmentTypes.PHYSICAL_TWEEN);

        if (attachment != null) {
            attachment.tick(0.05f);
        }
    }

}
