package net.quepierts.thatskyinteractions.common.network.packet.astrolabe;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.quepierts.simpleanimator.core.SimpleAnimator;
import net.quepierts.simpleanimator.core.network.BiPacket;
import net.quepierts.simpleanimator.core.network.NetworkPackets;
import net.quepierts.thatskyinteractions.client.ClientHelper;
import net.quepierts.thatskyinteractions.common.data.astrolabe.FriendAstrolabeInstance;
import net.quepierts.thatskyinteractions.common.data.attachment.UserDataAttachment;
import net.quepierts.thatskyinteractions.common.data.attachment.component.AstrolabeComponent;
import net.quepierts.thatskyinteractions.common.data.global.TSIGlobalData;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public abstract class AstrolabeOperationPacket extends BiPacket {
    public static final Type<AstrolabeOperationPacket> TYPE = NetworkPackets.createType(AstrolabeOperationPacket.class);
    private static final UUID DUMMY = new UUID(42, 42);

    private static final byte IGNITE = 0;
    private static final byte GAIN = 1;
    private static final byte REFRESH = 2;
    private final byte code;
    protected final UUID target;

    public static AstrolabeOperationPacket decode(FriendlyByteBuf byteBuf) {
        byte code = byteBuf.readByte();
        UUID target = byteBuf.readUUID();
        switch (code) {
            case IGNITE -> {
                return new Ignite(target);
            }
            case GAIN -> {
                return new Gain(target);
            }
            case REFRESH -> {
                return new Refresh();
            }
        }
        throw new IllegalArgumentException("Packet Code: " + code);
    }
    public AstrolabeOperationPacket(UUID target, byte code) {
        this.target = target;
        this.code = code;
    }

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeByte(this.code);
        friendlyByteBuf.writeUUID(this.target);
    }

    @NotNull
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static class Ignite extends AstrolabeOperationPacket {
        public Ignite(UUID target) {
            super(target, IGNITE);
        }

        @Override
        protected void update(@NotNull ServerPlayer serverPlayer) {
            MinecraftServer server = serverPlayer.getServer();
            if (server == null)
                return;

            UUID sender = serverPlayer.getUUID();

            AstrolabeComponent astrolabe = UserDataAttachment.getAttachment(serverPlayer).getAstrolabe();

            if (!astrolabe.isFriend(this.target)) {
                return;
            }

            FriendAstrolabeInstance.NodeData nodeData = astrolabe.getNodeData(this.target);
            if (nodeData != null && nodeData.hasFlag(FriendAstrolabeInstance.Flag.SENT)) {
                return;
            }

            TSIGlobalData data = TSIGlobalData.getGlobalRelationData(server);
            ServerPlayer other = server.getPlayerList().getPlayer(this.target);
            if (data.lit(serverPlayer, target, server)) {
                SimpleAnimator.getNetwork().sendToPlayer(new AstrolabeOperationPacket.Ignite(sender), other);
            }
        }

        @OnlyIn(Dist.CLIENT)
        @Override
        protected void sync() {
            ClientHelper.awardLight(this.target);
        }
    }

    public static class Gain extends AstrolabeOperationPacket {
        public Gain(UUID target) {
            super(target, GAIN);
        }

        @Override
        protected void update(@NotNull ServerPlayer serverPlayer) {
            MinecraftServer server = serverPlayer.getServer();
            if (server == null)
                return;

            AstrolabeComponent astrolabe = UserDataAttachment.getAttachment(serverPlayer).getAstrolabe();

            if (!astrolabe.isFriend(this.target)) {
                return;
            }

            FriendAstrolabeInstance.NodeData nodeData = astrolabe.getNodeData(this.target);
            if (nodeData != null && nodeData.hasFlag(FriendAstrolabeInstance.Flag.RECEIVED)) {
                astrolabe.gainLight(this.target);
                serverPlayer.addItem(new ItemStack(Items.CANDLE));
            }
        }

        @Override
        protected void sync() {
        }
    }

    public static class Refresh extends AstrolabeOperationPacket {
        public Refresh() {
            super(DUMMY, REFRESH);
        }

        @Override
        protected void update(@NotNull ServerPlayer serverPlayer) {
        }

        @OnlyIn(Dist.CLIENT)
        @Override
        protected void sync() {
            LocalPlayer player = Minecraft.getInstance().player;

            if (player == null) {
                return;
            }

            UserDataAttachment attachment = UserDataAttachment.getAttachment(player);
            attachment.getAstrolabe().getAstrolabes().update();
        }
    }


}
