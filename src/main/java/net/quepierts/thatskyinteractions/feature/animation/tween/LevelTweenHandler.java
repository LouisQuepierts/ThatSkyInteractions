package net.quepierts.thatskyinteractions.feature.animation.tween;

import lombok.experimental.UtilityClass;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;

@UtilityClass
@EventBusSubscriber(modid = ThatSkyInteractions.MODID)
public class LevelTweenHandler {

    @SubscribeEvent
    public static void beforeLevelTick(final LevelTickEvent.Pre event) {
        final var level         = event.getLevel();
        final var attachment    = PhysicalTweenAttachment.getAttachment(level);

        attachment.tick(0.05f);
    }

}
