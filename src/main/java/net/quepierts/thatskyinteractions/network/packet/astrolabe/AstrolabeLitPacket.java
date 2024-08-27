package net.quepierts.thatskyinteractions.network.packet.astrolabe;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.quepierts.simpleanimator.core.SimpleAnimator;
import net.quepierts.simpleanimator.core.network.BiPacket;
import net.quepierts.simpleanimator.core.network.NetworkPackets;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.client.data.ClientTSIDataCache;
import net.quepierts.thatskyinteractions.data.PlayerPair;
import net.quepierts.thatskyinteractions.data.RelationshipSavedData;
import net.quepierts.thatskyinteractions.data.TSIUserDataStorage;
import net.quepierts.thatskyinteractions.data.tree.InteractTreeInstance;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class AstrolabeLitPacket extends BiPacket {
    public static final Type<AstrolabeLitPacket> TYPE = NetworkPackets.createType(AstrolabeLitPacket.class);
    private final UUID target;

    public AstrolabeLitPacket(FriendlyByteBuf byteBuf) {
        this.target = byteBuf.readUUID();
    }

    public AstrolabeLitPacket(UUID target) {
        this.target = target;
    }

    @Override
    protected void update(@NotNull ServerPlayer serverPlayer) {
        MinecraftServer server = serverPlayer.getServer();
        if (server == null)
            return;

        final ServerLevel level = server.getLevel(Level.OVERWORLD);

        ServerPlayer other = server.getPlayerList().getPlayer(this.target);
        if (other == null)
            return;

        UUID sender = serverPlayer.getUUID();

        RelationshipSavedData savedData = RelationshipSavedData.getRelationTree(level);
        InteractTreeInstance treeInstance = savedData.getRelationTree(new PlayerPair(sender, this.target));

        if (treeInstance == null)
            return;

        TSIUserDataStorage manager = ThatSkyInteractions.getInstance().getProxy().getUserDataManager();
        manager.litLight(sender, target);

        SimpleAnimator.getNetwork().sendToPlayer(new AstrolabeLitPacket(sender), other);
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
