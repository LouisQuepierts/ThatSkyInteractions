package net.quepierts.thatskyinteractions.network.packet.astrolabe;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.quepierts.simpleanimator.core.network.IUpdate;
import net.quepierts.simpleanimator.core.network.NetworkPackets;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.data.TSIUserData;
import net.quepierts.thatskyinteractions.data.TSIUserDataStorage;

import java.util.UUID;

public abstract class AstrolabeModifyPacket implements IUpdate {
    public static final Type<AstrolabeModifyPacket> TYPE = NetworkPackets.createType(AstrolabeModifyPacket.class);
    private static final byte LIKE = 0;
    private static final byte UNLIKE = 1;
    private static final byte MOVE = 2;
    private static final byte CREATE = 3;

    protected final byte code;

    public AstrolabeModifyPacket(byte code) {
        this.code = code;
    }

    public static AstrolabeModifyPacket decode(FriendlyByteBuf byteBuf) {
        byte code = byteBuf.readByte();

        switch (code) {
            case LIKE:
                return new Like(byteBuf);
            case UNLIKE:
                return new Unlike(byteBuf);
            case MOVE:
                return new Move(byteBuf);
            case CREATE:
                return new Create(byteBuf);
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

    public static final class Like extends AstrolabeModifyPacket {
        private final UUID friendUUID;
        public Like(FriendlyByteBuf byteBuf) {
            super(LIKE);
            this.friendUUID = byteBuf.readUUID();
        }

        public Like(UUID friendUUID) {
            super(LIKE);
            this.friendUUID = friendUUID;
        }

        @Override
        public void update(ServerPlayer serverPlayer) {
            TSIUserDataStorage manager = ThatSkyInteractions.getInstance().getProxy().getUserDataManager();
            TSIUserData userData = manager.getUserData(serverPlayer.getUUID());
            userData.likeFriend(this.friendUUID);
        }

        @Override
        public void write(FriendlyByteBuf friendlyByteBuf) {
            super.write(friendlyByteBuf);
            friendlyByteBuf.writeUUID(this.friendUUID);
        }
    }

    public static final class Unlike extends AstrolabeModifyPacket {
        private final UUID friendUUID;
        public Unlike(FriendlyByteBuf byteBuf) {
            super(UNLIKE);
            this.friendUUID = byteBuf.readUUID();
        }

        public Unlike(UUID friendUUID) {
            super(UNLIKE);
            this.friendUUID = friendUUID;
        }

        @Override
        public void update(ServerPlayer serverPlayer) {
            TSIUserDataStorage manager = ThatSkyInteractions.getInstance().getProxy().getUserDataManager();
            TSIUserData userData = manager.getUserData(serverPlayer.getUUID());
            userData.unlikeFriend(this.friendUUID);
        }

        @Override
        public void write(FriendlyByteBuf friendlyByteBuf) {
            super.write(friendlyByteBuf);
            friendlyByteBuf.writeUUID(this.friendUUID);
        }
    }

    public static final class Move extends AstrolabeModifyPacket {

        private final UUID friendUUID;
        private final ResourceLocation destLocation;
        private final int destIndex;

        public Move(UUID friendUUID, ResourceLocation destLocation, int destIndex) {
            super(MOVE);
            this.friendUUID = friendUUID;
            this.destLocation = destLocation;
            this.destIndex = destIndex;
        }

        public Move(FriendlyByteBuf byteBuf) {
            super(MOVE);

            this.friendUUID = byteBuf.readUUID();
            this.destLocation = byteBuf.readResourceLocation();
            this.destIndex = byteBuf.readVarInt();
        }
        @Override
        public void update(ServerPlayer serverPlayer) {
            TSIUserDataStorage manager = ThatSkyInteractions.getInstance().getProxy().getUserDataManager();
            TSIUserData userData = manager.getUserData(serverPlayer.getUUID());
            userData.move(this.friendUUID, this.destLocation, this.destIndex);
        }

        @Override
        public void write(FriendlyByteBuf friendlyByteBuf) {
            super.write(friendlyByteBuf);
            friendlyByteBuf.writeUUID(this.friendUUID);
            friendlyByteBuf.writeResourceLocation(this.destLocation);
            friendlyByteBuf.writeVarInt(this.destIndex);
        }
    }

    public static final class Create extends AstrolabeModifyPacket {
        private final ResourceLocation createLocation;
        public Create(FriendlyByteBuf byteBuf) {
            super(CREATE);
            this.createLocation = byteBuf.readResourceLocation();
        }

        public Create(ResourceLocation createLocation) {
            super(CREATE);
            this.createLocation = createLocation;
        }

        @Override
        public void update(ServerPlayer serverPlayer) {
            TSIUserDataStorage manager = ThatSkyInteractions.getInstance().getProxy().getUserDataManager();
            TSIUserData userData = manager.getUserData(serverPlayer.getUUID());
            userData.createAstrolabe(this.createLocation);
        }
    }
}
