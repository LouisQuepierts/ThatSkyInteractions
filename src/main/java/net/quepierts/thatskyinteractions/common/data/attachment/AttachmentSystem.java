package net.quepierts.thatskyinteractions.common.data.attachment;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.quepierts.simpleanimator.core.SimpleAnimator;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.common.data.global.TSIGlobalData;
import net.quepierts.thatskyinteractions.common.network.packet.UserDataAttachmentSyncPacket;
import net.quepierts.thatskyinteractions.common.registry.AttachmentTypes;

@EventBusSubscriber(modid = ThatSkyInteractions.MODID, bus = EventBusSubscriber.Bus.GAME)
public class AttachmentSystem {
    @SubscribeEvent
    public static void onPlayerJoin(EntityJoinLevelEvent event) {
        if (event.getEntity() instanceof Player player) {
            UserDataAttachment.getAttachment(player);
        }
    }

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        Player player = event.getEntity();
        if (player instanceof ServerPlayer serverPlayer) {
            MinecraftServer server = serverPlayer.getServer();

            if (server != null) {
                TSIGlobalData data = TSIGlobalData.getGlobalRelationData(server);
                data.tryAward(serverPlayer);
            }

            SimpleAnimator.getNetwork().sendToPlayer(new UserDataAttachmentSyncPacket(serverPlayer), serverPlayer);
        }
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        Player original = event.getOriginal();
        if (event.isWasDeath() && original.hasData(AttachmentTypes.USER_DATA)) {
            Player player = event.getEntity();
            UserDataAttachment current = UserDataAttachment.getAttachment(player);
            UserDataAttachment last = UserDataAttachment.getAttachment(original);

            current.setComponent(last);
        }
    }
}
