package net.quepierts.thatskyinteractions.feature.bond;

import lombok.experimental.UtilityClass;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityLeaveLevelEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.feature.bond.packet.ClientboundSyncHandholdPacket;

@UtilityClass
@EventBusSubscriber(modid = ThatSkyInteractions.MODID)
public class PlayerBondHandler {

    private static final Vec3       LEFT    = new Vec3(-1.0, 0.0, -0.2);
    private static final Vec3       RIGHT   = new Vec3(1.0, 0.0, -0.2);

    @SubscribeEvent
    public static void onPlayerLeave(final EntityLeaveLevelEvent event) {
        final var entity = event.getEntity();
        if (!(entity instanceof ServerPlayer player)) {
            return;
        }

        PlayerBondSystem.unholdAll(player);
        PlayerBondSystem.unRide(player);
        PlayerBondSystem.unCarry(player);

    }

    @SubscribeEvent
    public static void beforePlayerTick(final PlayerTickEvent.Pre event) {

        final var player        = event.getEntity();

        final var attachment    = PlayerBondAttachment.getAttachment(player);
        final var handhold      = attachment.getHandhold();
        final var resolver      = attachment.getResolver();

        if (handhold.isFollowing()) {
            final var left          = handhold.getLeft();
            final var right         = handhold.getRight();

            if (left != null) {
                resolver.follow(left, player, true);
            } else if (right != null) {
                resolver.follow(right, player, false);
            }
        }

    }

    @SubscribeEvent
    public static void onPlayerTick(final PlayerTickEvent.Post event) {

        final var player        = event.getEntity();

        final var attachment    = PlayerBondAttachment.getAttachment(player);
        final var handhold      = attachment.getHandhold();
        final var carry         = attachment.getCarry();
        final var resolver      = attachment.getResolver();

        if (handhold.isFollowing()) {
            final var leader    = handhold.getLeader();

            if (leader != null) {
                resolver.clampRotation(leader, player, 1.0f);
            }
        } else if (carry.isBeingCarried()) {
            final var carrier   = carry.getCarrier();

            if (carrier != null) {
                resolver.clampRotation(carrier, player, 1.0f);
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
