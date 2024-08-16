package net.quepierts.thatskyinteractions.network.packet;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import net.quepierts.simpleanimator.core.SimpleAnimator;
import net.quepierts.simpleanimator.core.network.BiPacket;
import net.quepierts.simpleanimator.core.network.INetwork;
import net.quepierts.simpleanimator.core.network.NetworkPackets;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.data.PlayerPair;
import net.quepierts.thatskyinteractions.data.RelationshipSavedData;
import net.quepierts.thatskyinteractions.data.tree.InteractTree;
import net.quepierts.thatskyinteractions.data.tree.InteractTreeInstance;
import net.quepierts.thatskyinteractions.data.tree.node.InteractTreeNode;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class UnlockRelationshipNodePacket extends BiPacket {
    public static final Type<UnlockRelationshipNodePacket> TYPE = NetworkPackets.createType(UnlockRelationshipNodePacket.class);

    private final PlayerPair pair;
    private final String node;
    private final boolean request;

    public UnlockRelationshipNodePacket(FriendlyByteBuf friendlyByteBuf) {
        this.pair = PlayerPair.fromNetwork(friendlyByteBuf);
        this.node = friendlyByteBuf.readUtf();
        this.request = friendlyByteBuf.readBoolean();
    }

    public UnlockRelationshipNodePacket(PlayerPair pair, String node, boolean accept) {
        this.pair = pair;
        this.node = node;
        this.request = accept;
    }

    @Override
    protected void update(@NotNull ServerPlayer sender) {
        final MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        final ServerLevel level = server.getLevel(Level.OVERWORLD);

        UUID other = this.pair.getOther(sender.getUUID());
        ServerPlayer player = server.getPlayerList().getPlayer(other);
        if (player == null)
            return;

        if (!this.pair.getOther(other).equals(sender.getUUID()))
            return;

        final RelationshipSavedData data = RelationshipSavedData.get(level);
        final InteractTreeInstance instance = data.get(pair);
        final InteractTree tree = data.getTree();

        if (!tree.contains(this.node))
            return;

        InteractTreeNode tNode = tree.get(this.node);

        INetwork network = SimpleAnimator.getNetwork();
        if (this.request) {
            int count = sender.getInventory().countItem(tNode.getCurrency());

            if (count < tNode.getPrice())
                return;
        } else {
            int count = player.getInventory().countItem(tNode.getCurrency());

            if (count < tNode.getPrice())
                return;

            if (!instance.unlock(node)) {
                return;
            }
            network.sendToPlayer(this, sender);
        }

        network.sendToPlayer(this, player);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    protected void sync() {
        UUID other = this.pair.getOther(Minecraft.getInstance().player.getUUID());
        if (request) {

        } else {
            InteractTreeInstance instance = ThatSkyInteractions.getInstance().getClient().getCache().get(other);
            instance.unlock(this.node);
        }
    }

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) {
        this.pair.toNetwork(friendlyByteBuf);
        friendlyByteBuf.writeUtf(this.node);
        friendlyByteBuf.writeBoolean(this.request);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
