package net.quepierts.thatskyinteractions.network;

import net.minecraft.network.FriendlyByteBuf;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.quepierts.simpleanimator.core.SimpleAnimator;
import net.quepierts.simpleanimator.core.network.INetwork;
import net.quepierts.simpleanimator.core.network.IPacket;
import net.quepierts.simpleanimator.core.network.NetworkDirection;
import net.quepierts.simpleanimator.core.network.NetworkPackets;
import net.quepierts.thatskyinteractions.network.packet.*;
import net.quepierts.thatskyinteractions.network.packet.astrolabe.AstrolabeOperationPacket;
import net.quepierts.thatskyinteractions.network.packet.astrolabe.AstrolabeSyncPacket;
import net.quepierts.thatskyinteractions.network.packet.block.UpdateBlockEntityDataPacket;

import java.util.function.Function;

@SuppressWarnings("all")
public enum Packets {
    BATCH_INTERACT_TREE(BatchInteractTreePacket.class, BatchInteractTreePacket::new, NetworkDirection.PLAY_TO_CLIENT),
    BATCH_ASTROLABE(BatchAstrolabePacket.class, BatchAstrolabePacket::new, NetworkDirection.PLAY_TO_CLIENT),
    BATCH_RELATIONSHIP(BatchRelationshipPacket.class, BatchRelationshipPacket::new, NetworkDirection.PLAY_TO_CLIENT),
    USERDATA_SYNC(UserDataSync.class, UserDataSync::new, NetworkDirection.PLAY_TO_CLIENT),
    ASTROLABE_SYNC(AstrolabeSyncPacket.class, AstrolabeSyncPacket::decode, NetworkDirection.PLAY_TO_CLIENT),
    ASTROLABE_IGNITE(AstrolabeOperationPacket.class, AstrolabeOperationPacket::decode, NetworkDirection.ALL),
    UNLOCK_RELATIONSHIP(UnlockRelationshipPacket.class, UnlockRelationshipPacket::decode, NetworkDirection.ALL),
    INTERACT_BUTTON(InteractButtonPacket.class, InteractButtonPacket::decode, NetworkDirection.ALL),
    ASTROLABE_MODIFY(UserDataModifyPacket.class, UserDataModifyPacket::decode, NetworkDirection.PLAY_TO_SERVER),
    PICKUP_WING_OF_LIGHT(PickupWingOfLightPacket.class, PickupWingOfLightPacket::new, NetworkDirection.PLAY_TO_SERVER),
    UPDATE_BLOCK_ENTITY(UpdateBlockEntityDataPacket.class, UpdateBlockEntityDataPacket::new, NetworkDirection.PLAY_TO_SERVER)
    ;

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
