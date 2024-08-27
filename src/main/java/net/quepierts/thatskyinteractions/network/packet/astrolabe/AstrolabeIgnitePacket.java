package net.quepierts.thatskyinteractions.network.packet.astrolabe;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
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

public class AstrolabeIgnitePacket extends BiPacket {
    public static final Type<AstrolabeIgnitePacket> TYPE = NetworkPackets.createType(AstrolabeIgnitePacket.class);
    private final UUID target;

    public AstrolabeIgnitePacket(FriendlyByteBuf byteBuf) {
        this.target = byteBuf.readUUID();
    }

    public AstrolabeIgnitePacket(UUID target) {
        this.target = target;
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

        if (data.getNodeData(this.target).hasFlag(FriendAstrolabeInstance.Flag.SENT)) {
            return;
        }

        manager.litLight(sender, target);

        ServerPlayer other = server.getPlayerList().getPlayer(this.target);
        if (other == null) {
            return;
        }
        SimpleAnimator.getNetwork().sendToPlayer(new AstrolabeIgnitePacket(sender), other);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    protected void sync() {
        ClientTSIDataCache cache = ThatSkyInteractions.getInstance().getClient().getCache();
        cache.awardLight(this.target);
    }

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeUUID(this.target);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
