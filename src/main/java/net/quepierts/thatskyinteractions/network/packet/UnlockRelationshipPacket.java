package net.quepierts.thatskyinteractions.network.packet;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import net.quepierts.simpleanimator.api.IAnimateHandler;
import net.quepierts.simpleanimator.core.SimpleAnimator;
import net.quepierts.simpleanimator.core.network.BiPacket;
import net.quepierts.simpleanimator.core.network.INetwork;
import net.quepierts.simpleanimator.core.network.NetworkPackets;
import net.quepierts.thatskyinteractions.PlayerUtils;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.client.data.ClientTSIDataCache;
import net.quepierts.thatskyinteractions.client.gui.component.w2s.UnlockRequestW2SButton;
import net.quepierts.thatskyinteractions.client.gui.layer.World2ScreenGridLayer;
import net.quepierts.thatskyinteractions.data.PlayerPair;
import net.quepierts.thatskyinteractions.data.RelationshipSavedData;
import net.quepierts.thatskyinteractions.data.tree.InteractTree;
import net.quepierts.thatskyinteractions.data.tree.InteractTreeInstance;
import net.quepierts.thatskyinteractions.data.tree.NodeState;
import net.quepierts.thatskyinteractions.data.tree.node.InteractTreeNode;
import net.quepierts.thatskyinteractions.proxy.Animations;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public abstract class UnlockRelationshipPacket extends BiPacket {
    public static final Type<UnlockRelationshipPacket> TYPE = NetworkPackets.createType(UnlockRelationshipPacket.class);
    private static final byte INVITE = 0;
    private static final byte ACCEPT = 1;
    private static final byte CANCEL = 2;
    private static final byte FINISH = 3;

    protected final UUID sender;
    protected final PlayerPair pair;
    protected final String node;
    private final byte code;

    public static UnlockRelationshipPacket decode(FriendlyByteBuf friendlyByteBuf) {
        UUID sender = friendlyByteBuf.readUUID();
        PlayerPair pair = PlayerPair.fromNetwork(friendlyByteBuf);
        String node = friendlyByteBuf.readUtf();
        byte code = friendlyByteBuf.readByte();

        switch (code) {
            case INVITE:
                return new Invite(sender, pair, node);
            case ACCEPT:
                return new Accept(sender, pair, node);
            case CANCEL:
                return new Cancel(sender, pair, node);
            case FINISH:
                return new Finish(sender, pair, node);
        }

        throw new IllegalArgumentException("Packet Code: " + code);
    }

    protected UnlockRelationshipPacket(UUID sender, PlayerPair pair, String node, byte code) {
        this.sender = sender;
        this.pair = pair;
        this.node = node;
        this.code = code;
    }

    @Override
    public final void write(FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeUUID(this.sender);
        this.pair.toNetwork(friendlyByteBuf);
        friendlyByteBuf.writeUtf(this.node);
        friendlyByteBuf.writeByte(this.code);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static class Invite extends UnlockRelationshipPacket {
        public Invite(UUID sender, PlayerPair pair, String node) {
            super(sender, pair, node, INVITE);
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

            final RelationshipSavedData data = RelationshipSavedData.getRelationTree(level);
            final InteractTree tree = data.getTree();

            if (!tree.contains(this.node))
                return;

            InteractTreeNode tNode = tree.get(this.node);

            INetwork network = SimpleAnimator.getNetwork();

            int count = sender.getInventory().countItem(tNode.getCurrency());

            if (count < tNode.getPrice())
                return;

            network.sendToPlayer(this, sender);
            network.sendToPlayer(this, player);
        }

        @OnlyIn(Dist.CLIENT)
        @Override
        protected void sync() {
            ClientTSIDataCache cache = ThatSkyInteractions.getInstance().getClient().getCache();

            Minecraft minecraft = Minecraft.getInstance();
            UUID local = minecraft.player.getUUID();

            if (local.equals(this.sender)) {
                ((IAnimateHandler) minecraft.player).simpleanimator$playAnimate(Animations.UNLOCK_INVITE, true);
            } else {
                UUID other = this.pair.getOther(local);
                Player player = minecraft.level.getPlayerByUUID(other);

                if (player == null)
                    return;

                InteractTreeInstance instance = cache.get(other);
                World2ScreenGridLayer.INSTANCE.addWorldPositionObject(other, new UnlockRequestW2SButton(
                        instance.getTree().get(this.node).asButton(null, NodeState.UNLOCKABLE),
                        this.pair,
                        this.node,
                        player
                ));
            }
        }
    }

    public static class Accept extends UnlockRelationshipPacket {
        public Accept(UUID sender, PlayerPair pair, String node) {
            super(sender, pair, node, ACCEPT);
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

            final RelationshipSavedData data = RelationshipSavedData.getRelationTree(level);
            final InteractTreeInstance instance = data.getRelationTree(pair);
            final InteractTree tree = data.getTree();

            if (!tree.contains(this.node))
                return;

            InteractTreeNode tNode = tree.get(this.node);

            INetwork network = SimpleAnimator.getNetwork();
            int count = player.getInventory().countItem(tNode.getCurrency());

            if (count < tNode.getPrice())
                return;

            if (!instance.unlock(node, true)) {
                return;
            }

            PlayerUtils.costItems(player, tNode.getCurrency(), tNode.getPrice());

            network.sendToPlayer(this, sender);
            network.sendToPlayer(this, player);
        }

        @OnlyIn(Dist.CLIENT)
        @Override
        protected void sync() {
            ClientTSIDataCache cache = ThatSkyInteractions.getInstance().getClient().getCache();

            Minecraft minecraft = Minecraft.getInstance();
            UUID local = minecraft.player.getUUID();
            UUID other = this.pair.getOther(local);
            Player player = minecraft.level.getPlayerByUUID(other);

            if (player == null)
                return;

            InteractTreeInstance instance = cache.get(other);
            instance.unlock(this.node, false);

            InteractTreeNode tNode = instance.getTree().get(this.node);

            if (local.equals(sender)) {
                ((IAnimateHandler) minecraft.player).simpleanimator$playAnimate(Animations.UNLOCK_ACCEPT, true);
            } else {
                PlayerUtils.costItems(player, tNode.getCurrency(), tNode.getPrice());
            }
        }
    }

    public static class Cancel extends UnlockRelationshipPacket {
        public Cancel(UUID sender, PlayerPair pair, String node) {
            super(sender, pair, node, CANCEL);
        }

        @Override
        protected void update(@NotNull ServerPlayer sender) {
            final MinecraftServer server = ServerLifecycleHooks.getCurrentServer();

            UUID other = this.pair.getOther(sender.getUUID());
            ServerPlayer player = server.getPlayerList().getPlayer(other);
            if (player == null)
                return;

            if (!this.pair.getOther(other).equals(sender.getUUID()))
                return;

            INetwork network = SimpleAnimator.getNetwork();
            network.sendToPlayer(this, player);
        }

        @OnlyIn(Dist.CLIENT)
        @Override
        protected void sync() {
            UUID other = this.pair.getOther(Minecraft.getInstance().player.getUUID());
            World2ScreenGridLayer.INSTANCE.remove(other);
        }
    }

    public static class Finish extends UnlockRelationshipPacket {

        public Finish(UUID sender, PlayerPair pair, String node) {
            super(sender, pair, node, FINISH);
        }

        @Override
        protected void update(@NotNull ServerPlayer sender) {
            final MinecraftServer server = ServerLifecycleHooks.getCurrentServer();

            UUID other = this.pair.getOther(sender.getUUID());
            ServerPlayer player = server.getPlayerList().getPlayer(other);
            if (player == null)
                return;

            if (!this.pair.getOther(other).equals(sender.getUUID()))
                return;

            INetwork network = SimpleAnimator.getNetwork();
            network.sendToPlayer(this, player);
        }

        @OnlyIn(Dist.CLIENT)
        @Override
        protected void sync() {
            ThatSkyInteractions.getInstance().getClient().getUnlockRelationshipHandler().finish();
        }
    }
}
