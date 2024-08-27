package net.quepierts.thatskyinteractions.network.packet;

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

public abstract class UserDataModifyPacket implements IUpdate {
    public static final Type<UserDataModifyPacket> TYPE = NetworkPackets.createType(UserDataModifyPacket.class);
    private static final byte LIKE = 0;
    private static final byte UNLIKE = 1;
    private static final byte MOVE = 2;
    private static final byte CREATE = 3;
    private static final byte NICKNAME = 4;

    protected final byte code;

    public UserDataModifyPacket(byte code) {
        this.code = code;
    }

    public static UserDataModifyPacket decode(FriendlyByteBuf byteBuf) {
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
            case NICKNAME:
                return new Nickname(byteBuf);
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

    public static final class Like extends UserDataModifyPacket {
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

    public static final class Unlike extends UserDataModifyPacket {
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

    public static final class Move extends UserDataModifyPacket {

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

    public static final class Create extends UserDataModifyPacket {
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

    public static final class Nickname extends UserDataModifyPacket {
        private final UUID friendUUID;
        private final String nickname;

        public Nickname(FriendlyByteBuf byteBuf) {
            super(NICKNAME);
            this.friendUUID = byteBuf.readUUID();
            this.nickname = byteBuf.readUtf();
        }

        public Nickname(UUID friendUUID, String nickname) {
            super(NICKNAME);
            this.friendUUID = friendUUID;
            this.nickname = nickname;
        }

        @Override
        public void write(FriendlyByteBuf friendlyByteBuf) {
            super.write(friendlyByteBuf);
            friendlyByteBuf.writeUUID(this.friendUUID);
            friendlyByteBuf.writeUtf(this.nickname);
        }

        @Override
        public void update(ServerPlayer serverPlayer) {
            TSIUserDataStorage manager = ThatSkyInteractions.getInstance().getProxy().getUserDataManager();
            TSIUserData userData = manager.getUserData(serverPlayer.getUUID());
            if (!userData.isFriend(this.friendUUID))
                return;
            userData.getNodeData(this.friendUUID).getFriendData().setNickname(this.nickname);
        }
    }
}
