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
import net.quepierts.thatskyinteractions.common.data.attachment.UserDataAttachment;
import net.quepierts.thatskyinteractions.common.data.attachment.component.AstrolabeComponent;
import net.quepierts.thatskyinteractions.common.data.attachment.component.PickupComponent;
import net.quepierts.thatskyinteractions.common.data.attachment.component.RelationshipComponent;
import net.quepierts.thatskyinteractions.common.data.manager.InteractTreeManager;
import net.quepierts.thatskyinteractions.common.data.tree.InteractTreeInstance;
import net.quepierts.thatskyinteractions.common.network.packet.ResetPickUpPacket;
import net.quepierts.thatskyinteractions.common.network.packet.UnlockRelationshipPacket;
import net.quepierts.thatskyinteractions.common.network.packet.astrolabe.AstrolabeOperationPacket;
import net.quepierts.thatskyinteractions.common.registry.TriggerTypes;

import java.util.UUID;

@EventBusSubscriber(modid = ThatSkyInteractions.MODID, bus = EventBusSubscriber.Bus.GAME)
public class TSICommand {
    private static final SuggestionProvider<CommandSourceStack> SUGGESTION_NODE = (context, builder) -> SharedSuggestionProvider.suggest(
            InteractTreeManager.INSTANCE.get(RelationshipComponent.FRIEND_INTERACT_TREE).getNodes().keySet(),
            builder
    );

    @SubscribeEvent
    public static void register(RegisterCommandsEvent event) {
        event.getDispatcher().register(
                Commands.literal("thatskyinteractions")
                        .then(Commands.literal("reset")
                                .then(Commands.literal("permanent").executes(context -> unclaim(context.getSource(), false)))
                                .then(Commands.literal("daily").executes(context -> unclaim(context.getSource(), true)))
                        )
                        .then(Commands.literal("tree")
                                .then(Commands.literal("unlock")
                                        .then(Commands.argument("player", EntityArgument.player())
                                                .then(Commands.literal("all").executes(context -> unlockTree(context, "all")))
                                                .then(Commands.argument("node", StringArgumentType.string()).suggests(SUGGESTION_NODE).executes(context -> unlockTree(context, context.getArgument("node", String.class))))))
                                .then(Commands.literal("reset")
                                        .then(Commands.argument("player", EntityArgument.player()).executes(TSICommand::resetTree)))
                        )
                        .then(Commands.literal("astrolabe")
                                .then(Commands.literal("refresh").executes(context -> refreshAstrolabe(context.getSource()))))
        );
    }

    private static int refreshAstrolabe(CommandSourceStack source) {
        if (!source.isPlayer()) {
            source.sendFailure(Component.literal("command must execute by player"));
            return 0;
        }

        ServerPlayer player = source.getPlayer();
        AstrolabeComponent astrolabe = UserDataAttachment.getAttachment(player).getAstrolabe();
        astrolabe.getAstrolabes().update();

        SimpleAnimator.getNetwork().sendToPlayer(new AstrolabeOperationPacket.Refresh(), player);
        source.sendSuccess(() -> Component.literal("astrolabe refreshed"), true);
        return 1;
    }

    private static int unlockTree(CommandContext<CommandSourceStack> context, String node) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        if (source.isPlayer()) {
            ServerPlayer sender = source.getPlayer();
            ServerPlayer player = EntityArgument.getPlayer(context, "player");

            if (sender.getUUID().equals(player.getUUID())) {
                source.sendFailure(Component.literal("you cannot unlock with yourself"));
                return 0;
            }

            PlayerPair pair = new PlayerPair(sender.getUUID(), player.getUUID());

            InteractTreeInstance instance1 = UserDataAttachment.getAttachment(sender).getRelationship().get(player.getUUID());
            InteractTreeInstance instance2 = UserDataAttachment.getAttachment(player).getRelationship().get(sender.getUUID());

            if (node.equals("all")) {
                instance1.unlockAll();
                instance2.unlockAll();
                TriggerTypes.COMPLETED_RELATIONSHIP.get().trigger(sender, player.getUUID());
                TriggerTypes.COMPLETED_RELATIONSHIP.get().trigger(player, sender.getUUID());
            } else {
                if (!InteractTreeInstance.unlock(instance1, instance2, node, true)) {
                    source.sendFailure(Component.literal("unlock failed"));
                    return 0;
                }

                TriggerTypes.UNLOCK_RELATIONSHIP.get().trigger(sender, node);
                TriggerTypes.UNLOCK_RELATIONSHIP.get().trigger(player, node);
                TriggerTypes.COMPLETED_RELATIONSHIP.get().trigger(sender, player.getUUID());
                TriggerTypes.COMPLETED_RELATIONSHIP.get().trigger(player, sender.getUUID());
            }

            INetwork network = SimpleAnimator.getNetwork();
            network.sendToPlayer(new UnlockRelationshipPacket.Forced(sender.getUUID(), pair, node), player);
            network.sendToPlayer(new UnlockRelationshipPacket.Forced(player.getUUID(), pair, node), sender);
            source.sendSuccess(() -> Component.literal("unlock succeed"), false);
            return 1;
        }

        source.sendFailure(Component.literal("command must execute by player"));
        return 0;
    }

    private static int resetTree(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        if (source.isPlayer()) {
            ServerPlayer sender = source.getPlayer();
            ServerPlayer player = EntityArgument.getPlayer(context, "player");

            UUID targetUuid = player.getUUID();
            if (sender.getUUID().equals(targetUuid)) {
                source.sendFailure(Component.literal("you cannot reset with yourself"));
                return 0;
            }

            PlayerPair pair = new PlayerPair(sender.getUUID(), targetUuid);

            RelationshipComponent relationship = UserDataAttachment.getAttachment(sender).getRelationship();

            if (!relationship.isFriend(targetUuid)) {
                source.sendFailure(Component.literal("this player is not your friend!"));
                return 0;
            }

            relationship.get(targetUuid).reset();
            UserDataAttachment.getAttachment(player).getRelationship().get(sender.getUUID()).reset();

            INetwork network = SimpleAnimator.getNetwork();
            network.sendToPlayer(new UnlockRelationshipPacket.Reset(sender.getUUID(), pair), player);
            network.sendToPlayer(new UnlockRelationshipPacket.Reset(player.getUUID(), pair), sender);
            source.sendSuccess(() -> Component.literal("reset succeed"), false);
            return 1;
        }

        source.sendFailure(Component.literal("command must execute by player"));
        return 0;
    }

    @SuppressWarnings("all")
    private static int unclaim(CommandSourceStack source, boolean isRefreshable) {
        if (!source.isPlayer()) {
            source.sendFailure(Component.literal("command must execute by player"));
            return 0;
        }

        ServerPlayer player = source.getPlayer();
        UserDataAttachment attachment = UserDataAttachment.getAttachment(player);
        PickupComponent pickupComponent = attachment.getPickup();

        pickupComponent.unclaim(isRefreshable);

        SimpleAnimator.getNetwork().sendToPlayer(new ResetPickUpPacket(isRefreshable), player);
        source.sendSuccess(() -> Component.literal("Reseted pick up data"), false);
        return 0;
    }
}
