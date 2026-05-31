package net.quepierts.thatskyinteractions.feature.client.input;

import lombok.experimental.UtilityClass;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.feature.client.gui.ScreenLoader;
import net.quepierts.thatskyinteractions.feature.client.gui.screen.ExpressionsScreen;
import net.quepierts.thatskyinteractions.feature.client.gui.screen.FriendshipScreen;

@UtilityClass
@EventBusSubscriber(value = Dist.CLIENT, modid = ThatSkyInteractions.MODID)
public class ClientScreenHandler {

    @SubscribeEvent
    public static void onKey(final InputEvent.Key event) {
        final var minecraft = Minecraft.getInstance();

        if (TSIKeys.KEY_INTERACT.consumeClick()) {

            if (minecraft.screen == null) {
                ScreenLoader.open(ExpressionsScreen.class);
            }
        }
    }

}
