package net.quepierts.thatskyinteractions.feature.client;

import lombok.experimental.UtilityClass;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.feature.client.gui.ScreenLoader;
import net.quepierts.thatskyinteractions.feature.client.gui.screen.FriendshipScreen;
import net.quepierts.thatskyinteractions.feature.client.input.TSIKeys;
import net.quepierts.thatskyinteractions.feature.friendship.PlayerFriendshipAttachment;

@UtilityClass
@EventBusSubscriber(value = Dist.CLIENT, modid = ThatSkyInteractions.MODID)
public class ClientPlayerFriendshipSystem {

    @SubscribeEvent
    public static void onInteractPlayer(final PlayerInteractEvent.EntityInteract event) {

        final var player    = event.getEntity();
        if (!player.isLocalPlayer()) {
            return;
        }

        if (!player.getMainHandItem().isEmpty() || !player.getOffhandItem().isEmpty()) {
            return;
        }

        if (!TSIKeys.KEY_INTERACT.isDown()) {
            return;
        }

        final var target    = event.getTarget();
        if (!(target instanceof LivingEntity other)) {
            return;
        }

        final var uuid      = target.getUUID();
        final var data      = PlayerFriendshipAttachment.getAttachment(player);

        final var tree      = data.get(uuid, PlayerFriendshipAttachment.FRIEND);

        Minecraft.getInstance().mouseHandler.releaseMouse();
        ScreenLoader.open(FriendshipScreen.class, tree);
    }

}
