package net.quepierts.thatskyinteractions.mixin.hugme;

import com.mojang.datafixers.util.Pair;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import net.quepierts.simpleanimator.core.PlayerUtils;
import nya.tuyw.hugme.command.HugCommandHandler;
import org.spongepowered.asm.mixin.*;

import java.util.Map;
import java.util.UUID;

@Pseudo
@Mixin(HugCommandHandler.class)
public class HugCommandHandlerMixin {
    @Shadow
    private static void sendRenderInfoToNearbyPlayers(ServerPlayer sender, ServerPlayer receiver) {}

    @Shadow @Final
    private static Map<Pair<UUID, UUID>, HugCommandHandler.HugRequest> hugRequests;

    @Shadow @Final
    private static Map<Pair<UUID, UUID>, Object> hugStatuses;

    /**
     * @author Louis_Quepierts
     * @reason Fixed an issue where players would accept invitations in the wrong place
     */
    @Overwrite
    private static void handleHugAcceptance(ServerPlayer sender, ServerPlayer receiver) {
        Pair<UUID, UUID> key = new Pair<>(sender.getUUID(), receiver.getUUID());
        HugCommandHandler.HugRequest request = hugRequests.get(key);
        if (request != null) {
            if (hugStatuses.containsKey(key)) {
                sender.sendSystemMessage(Component.translatable("hugme.message.hug_in_progress_sender", new Object[]{receiver.getName().getString()}).withStyle(ChatFormatting.RED));
                receiver.sendSystemMessage(Component.translatable("hugme.message.hug_in_progress_receiver", new Object[]{sender.getName().getString()}).withStyle(ChatFormatting.RED));
                return;
            }

            if (sender.serverLevel() != receiver.serverLevel()) {
                receiver.sendSystemMessage(Component.translatable("hugme.message.invalid_level", sender.getName().getString()).withStyle(ChatFormatting.RED));
                return;
            }

            if (receiver.distanceTo(sender) > 5.0F) {
                receiver.sendSystemMessage(Component.translatable("hugme.message.too_far", sender.getName().getString()).withStyle(ChatFormatting.RED));
                return;
            }

            Vec3 delta = sender.position().subtract(receiver.position()).normalize();
            Vec3 standPosition = sender.position().subtract(delta);
            if (!PlayerUtils.isPositionSave(
                    standPosition,
                    sender.level()
            )) {
                receiver.sendSystemMessage(Component.translatable("hugme.message.unsafe_position", sender.getName().getString()).withStyle(ChatFormatting.RED));
                return;
            }

            sender.sendSystemMessage(Component.translatable("hugme.message.sender_accepted", receiver.getName().getString()).withStyle(ChatFormatting.GREEN));
            receiver.sendSystemMessage(Component.translatable("hugme.message.receiver_accepted", sender.getName().getString()).withStyle(ChatFormatting.GREEN));

            sender.lookAt(EntityAnchorArgument.Anchor.EYES, new Vec3(receiver.getX(), receiver.getEyeY(), receiver.getZ()));
            receiver.teleportTo(standPosition.x(), standPosition.y(), standPosition.z());
            receiver.lookAt(EntityAnchorArgument.Anchor.EYES, new Vec3(sender.getX(), sender.getEyeY(), sender.getZ()));
            sendRenderInfoToNearbyPlayers(sender, receiver);
            hugRequests.remove(key);
        } else {
            receiver.sendSystemMessage(Component.translatable("hugme.message.invalid_request").withStyle(ChatFormatting.RED));
        }
    }
}
