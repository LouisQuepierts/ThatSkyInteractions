package net.quepierts.thatskyinteractions.network.packet.astrolabe;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.quepierts.simpleanimator.core.network.ISync;
import net.quepierts.simpleanimator.core.network.NetworkPackets;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.client.data.ClientTSIDataCache;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public abstract class AstrolabeSyncPacket implements ISync {
    public static final Type<AstrolabeSyncPacket> TYPE = NetworkPackets.createType(AstrolabeSyncPacket.class);
    private static final byte ADD_FRIEND = 0;

    private final byte code;

    protected AstrolabeSyncPacket(byte code) {
        this.code = code;
    }

    public static AstrolabeSyncPacket decode(FriendlyByteBuf byteBuf) {
        byte code = byteBuf.readByte();
        if (code == ADD_FRIEND) {
            return new AddFriend(byteBuf);
        }

        throw new IllegalArgumentException("Packet Code: " + code);
    }

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeByte(this.code);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static final class AddFriend extends AstrolabeSyncPacket {
        private final UUID friendUUID;
        public AddFriend(@NotNull UUID friendUUID) {
            super(ADD_FRIEND);
            this.friendUUID = friendUUID;
        }

        public AddFriend(FriendlyByteBuf byteBuf) {
            super(ADD_FRIEND);
            this.friendUUID = byteBuf.readUUID();
        }

        @Override
        public void write(FriendlyByteBuf friendlyByteBuf) {
            super.write(friendlyByteBuf);
            friendlyByteBuf.writeUUID(this.friendUUID);
        }

        @OnlyIn(Dist.CLIENT)
        @Override
        public void sync() {
            Player friend = Minecraft.getInstance().level.getPlayerByUUID(this.friendUUID);

            if (friend == null)
                return;

            ClientTSIDataCache cache = ThatSkyInteractions.getInstance().getClient().getCache();
            cache.getUserData().addFriend(friend);
        }
    }
}
