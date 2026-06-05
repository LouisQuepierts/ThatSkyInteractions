package net.quepierts.thatskyinteractions.feature.command;

import lombok.experimental.UtilityClass;
import net.minecraft.commands.Commands;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;

@UtilityClass
@EventBusSubscriber(modid = ThatSkyInteractions.MODID)
public class TsiCommand {

    @SubscribeEvent
    public static void onRegisterCommand(final RegisterCommandsEvent event) {
        final var dispatcher = event.getDispatcher();

        final var root = dispatcher.register(
                Commands.literal(ThatSkyInteractions.MODID).requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                        .then(FriendshipCommand.command())
        );

        dispatcher.register(
                Commands.literal("tsi").redirect(root)
        );
    }

}
