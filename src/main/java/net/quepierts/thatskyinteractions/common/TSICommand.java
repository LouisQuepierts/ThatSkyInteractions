package net.quepierts.thatskyinteractions.common;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.quepierts.simpleanimator.core.SimpleAnimator;
import net.quepierts.simpleanimator.core.network.INetwork;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.common.data.PlayerPair;
import net.quepierts.thatskyinteractions.common.data.RelationshipSavedData;
import net.quepierts.thatskyinteractions.common.data.TSIUserData;
import net.quepierts.thatskyinteractions.common.data.tree.InteractTreeInstance;
import net.quepierts.thatskyinteractions.common.network.packet.ResetPickUpPacket;
import net.quepierts.thatskyinteractions.common.network.packet.UnlockRelationshipPacket;

@EventBusSubscriber(modid = ThatSkyInteractions.MODID, bus = EventBusSubscriber.Bus.GAME)
public class TSICommand {
    private static final SuggestionProvider<CommandSourceStack> SUGGESTION_NODE = (context, builder) -> SharedSuggestionProvider.suggest(
            ThatSkyInteractions.getInstance().getProxy().getInteractTreeManager().get(RelationshipSavedData.FRIEND_INTERACT_TREE).getNodes().keySet(),
            builder
    );

    @SubscribeEvent
    public static void register(RegisterCommandsEvent event) {
        event.getDispatcher().register(
                Commands.literal("thatskyinteractions")
                        .then(Commands.literal("unclaim")
                                .then(Commands.literal("static").executes(context -> unclaim(context.getSource(), true)))
                                .then(Commands.literal("daily").executes(context -> unclaim(context.getSource(), false)))
                        )
                        .then(Commands.literal("tree")
                                .then(Commands.literal("unlock")
                                        .then(Commands.argument("player", EntityArgument.player())
                                                .then(Commands.literal("all").executes(context -> unlockTree(context, "all")))
                                                .then(Commands.argument("node", StringArgumentType.string()).suggests(SUGGESTION_NODE).executes(context -> unlockTree(context, context.getArgument("node", String.class))))))
                        )
        );
    }

    private static int unlockTree(CommandContext<CommandSourceStack> context, String node) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        if (source.isPlayer()) {
            ServerPlayer sender = source.getPlayer();
            ServerPlayer player = EntityArgument.getPlayer(context, "player");

            final RelationshipSavedData data = RelationshipSavedData.getRelationTree(source.getLevel());
            PlayerPair pair = new PlayerPair(sender.getUUID(), player.getUUID());
            InteractTreeInstance instance = data.getRelationTree(pair);

            if (node.equals("all")) {
                instance.unlockAll();
            } else {
                if (!instance.unlock(node, true)) {
                    source.sendFailure(Component.literal("unlock failed"));
                    return 0;
                }
            }

            INetwork network = SimpleAnimator.getNetwork();
            network.sendToPlayer(new UnlockRelationshipPacket.Forced(sender.getUUID(), pair, node), player);
            network.sendToPlayer(new UnlockRelationshipPacket.Forced(player.getUUID(), pair, node), sender);
            source.sendSuccess(() -> Component.literal("unlock succeed"), false);
            return 1;
        }
        return 0;
    }

    @SuppressWarnings("all")
    private static int unclaim(CommandSourceStack source, boolean isStatic) {
        if (!source.isPlayer()) {
            return 0;
        }

        ServerPlayer player = source.getPlayer();

        TSIUserData data = ThatSkyInteractions.getInstance().getProxy().getUserDataManager().getUserData(player.getUUID());
        if (data != null) {
            if (isStatic) {
                data.resetStaticPickUp();
            } else {
                data.resetDailyPickUp();
            }

            SimpleAnimator.getNetwork().sendToPlayer(new ResetPickUpPacket(isStatic), player);
            source.sendSuccess(() -> Component.literal("Reseted pick up data"), false);
            return 1;
        }

        return 0;
    }
}
