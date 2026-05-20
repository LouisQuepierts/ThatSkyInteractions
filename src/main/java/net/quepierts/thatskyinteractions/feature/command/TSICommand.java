package net.quepierts.thatskyinteractions.feature.command;

import lombok.experimental.UtilityClass;
import net.minecraft.commands.Commands;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;

@UtilityClass
@EventBusSubscriber(modid = ThatSkyInteractions.MODID)
public class TSICommand {

    @SubscribeEvent
    public static void onRegisterCommand(final RegisterCommandsEvent event) {
        final var dispatcher = event.getDispatcher();

        dispatcher.register(
                Commands.literal("tsi")
                        .then(AnimationCommand.command())
        );
    }

}
