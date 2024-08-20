package net.quepierts.thatskyinteractions.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.quepierts.simpleanimator.core.SimpleAnimator;
import net.quepierts.simpleanimator.core.network.INetwork;
import net.quepierts.simpleanimator.core.network.IPacket;
import net.quepierts.simpleanimator.core.network.NetworkDirection;
import net.quepierts.simpleanimator.core.network.NetworkPackets;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.network.packet.BatchInteractTreePacket;
import net.quepierts.thatskyinteractions.network.packet.BatchRelationshipPacket;
import net.quepierts.thatskyinteractions.network.packet.InteractButtonPacket;
import net.quepierts.thatskyinteractions.network.packet.UnlockRelationshipPacket;

import java.util.Locale;
import java.util.function.Function;

public enum Packets {

    BATCH_INTERACT_TREE(BatchInteractTreePacket.class, BatchInteractTreePacket::new, NetworkDirection.PLAY_TO_CLIENT),
    BATCH_RELATIONSHIP(BatchRelationshipPacket.class, BatchRelationshipPacket::new, NetworkDirection.PLAY_TO_CLIENT),
    UNLOCK_RELATIONSHIP(UnlockRelationshipPacket.class, UnlockRelationshipPacket::decode, NetworkDirection.ALL),
    INTERACT_BUTTON(InteractButtonPacket.class, InteractButtonPacket::decode, NetworkDirection.ALL)
    ;

    public static <T extends IPacket> CustomPacketPayload.Type<T> createType(Class<T> type) {
        ResourceLocation location = ThatSkyInteractions.getLocation(type.getSimpleName().toLowerCase(Locale.ROOT));
        return new CustomPacketPayload.Type<>(location);
    }

    private final NetworkPackets.PacketType<?> packet;

    <T extends IPacket> Packets(
            Class<T> type,
            Function<FriendlyByteBuf, T> decoder,
            NetworkDirection direction
    ) {
        packet = new NetworkPackets.PacketType<>(type, decoder, T::handle, direction);
    }

    public static void onRegisterPayloadHandlers(final RegisterPayloadHandlersEvent event) {
        INetwork network = SimpleAnimator.getNetwork();

        for (Packets value : values()) {
            network.register(value.packet);
        }
    }
}
