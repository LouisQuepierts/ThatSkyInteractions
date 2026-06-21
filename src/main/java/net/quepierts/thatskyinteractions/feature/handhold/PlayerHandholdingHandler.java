package net.quepierts.thatskyinteractions.feature.handhold;

import lombok.experimental.UtilityClass;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityLeaveLevelEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.feature.handhold.packet.ClientboundSyncHandholdPacket;
import org.jspecify.annotations.NonNull;

@UtilityClass
@EventBusSubscriber(modid = ThatSkyInteractions.MODID)
public class PlayerHandholdingHandler {

    private static final Vec3       LEFT    = new Vec3(-1.0, 0.0, -0.2);
    private static final Vec3       RIGHT   = new Vec3(1.0, 0.0, -0.2);

    @SubscribeEvent
    public static void onPlayerLeave(final EntityLeaveLevelEvent event) {
        final var entity = event.getEntity();
        if (!(entity instanceof ServerPlayer player)) {
            return;
        }

        PlayerHandholdingSystem.unholdAll(player);

    }

    @SubscribeEvent
    public static void onPlayerTick(final PlayerTickEvent.Pre event) {
        final var player        = event.getEntity();

        final var attachment    = PlayerHandholdingAttachment.getAttachment(player);
        final var relation      = attachment.getRelation();

        if (relation.isFollowing()) {
            final var left          = relation.getLeft();
            final var right         = relation.getRight();
            final var resolver      = attachment.getResolver();

            if (left != null) {
                resolver.follow(left, player, true);
            } else if (right != null) {
                resolver.follow(right, player, false);
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerStartTracking(final PlayerEvent.StartTracking event) {

        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        if (event.getTarget() instanceof ServerPlayer target) {

            PacketDistributor.sendToPlayer(
                    player,
                    ClientboundSyncHandholdPacket.of(target)
            );

        }

    }

}
