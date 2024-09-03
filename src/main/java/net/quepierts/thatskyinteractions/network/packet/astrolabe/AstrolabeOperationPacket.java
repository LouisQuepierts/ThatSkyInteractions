package net.quepierts.thatskyinteractions.network.packet.astrolabe;

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
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.client.data.ClientTSIDataCache;
import net.quepierts.thatskyinteractions.data.TSIUserData;
import net.quepierts.thatskyinteractions.data.TSIUserDataStorage;
import net.quepierts.thatskyinteractions.data.astrolabe.FriendAstrolabeInstance;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public abstract class AstrolabeOperationPacket extends BiPacket {
    public static final Type<AstrolabeOperationPacket> TYPE = NetworkPackets.createType(AstrolabeOperationPacket.class);
    private static final byte IGNITE = 0;
    private static final byte GAIN = 1;
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

            TSIUserDataStorage manager = ThatSkyInteractions.getInstance().getProxy().getUserDataManager();
            TSIUserData data = manager.getUserData(sender);
            if (!data.isFriend(this.target)) {
                return;
            }

            FriendAstrolabeInstance.NodeData nodeData = data.getNodeData(this.target);
            if (nodeData != null && nodeData.hasFlag(FriendAstrolabeInstance.Flag.SENT)) {
                return;
            }

            manager.litLight(sender, target);

            ServerPlayer other = server.getPlayerList().getPlayer(this.target);
            if (other == null) {
                return;
            }
            SimpleAnimator.getNetwork().sendToPlayer(new AstrolabeOperationPacket.Ignite(sender), other);
        }

        @OnlyIn(Dist.CLIENT)
        @Override
        protected void sync() {
            ClientTSIDataCache cache = ThatSkyInteractions.getInstance().getClient().getCache();
            cache.awardLight(this.target);
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

            UUID sender = serverPlayer.getUUID();

            TSIUserDataStorage manager = ThatSkyInteractions.getInstance().getProxy().getUserDataManager();
            TSIUserData data = manager.getUserData(sender);
            if (!data.isFriend(this.target)) {
                return;
            }

            FriendAstrolabeInstance.NodeData nodeData = data.getNodeData(this.target);
            if (nodeData != null && !nodeData.hasFlag(FriendAstrolabeInstance.Flag.RECEIVED)) {
                manager.gainLight(sender, this.target);
                serverPlayer.addItem(new ItemStack(Items.CANDLE));
            }
        }

        @Override
        protected void sync() {

        }
    }
}
