package net.quepierts.thatskyinteractions.common.network.packet;

import com.mojang.datafixers.util.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import net.quepierts.simpleanimator.api.IAnimateHandler;
import net.quepierts.simpleanimator.core.SimpleAnimator;
import net.quepierts.simpleanimator.core.network.BiPacket;
import net.quepierts.simpleanimator.core.network.INetwork;
import net.quepierts.simpleanimator.core.network.NetworkPackets;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.client.gui.component.w2s.UnlockRequestW2SButton;
import net.quepierts.thatskyinteractions.client.gui.layer.World2ScreenWidgetLayer;
import net.quepierts.thatskyinteractions.common.PlayerUtils;
import net.quepierts.thatskyinteractions.common.data.PlayerPair;
import net.quepierts.thatskyinteractions.common.data.attachment.UserDataAttachment;
import net.quepierts.thatskyinteractions.common.data.attachment.component.RelationshipComponent;
import net.quepierts.thatskyinteractions.common.data.manager.InteractTreeManager;
import net.quepierts.thatskyinteractions.common.data.tree.InteractTree;
import net.quepierts.thatskyinteractions.common.data.tree.InteractTreeInstance;
import net.quepierts.thatskyinteractions.common.data.tree.NodeState;
import net.quepierts.thatskyinteractions.common.data.tree.node.InteractTreeNode;
import net.quepierts.thatskyinteractions.common.reference.Animations;
import net.quepierts.thatskyinteractions.common.registry.TriggerTypes;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public abstract class UnlockRelationshipPacket extends BiPacket {
    public static final Type<UnlockRelationshipPacket> TYPE = NetworkPackets.createType(UnlockRelationshipPacket.class);
    private static final byte INVITE = 0;
    private static final byte ACCEPT = 1;
    private static final byte CANCEL = 2;
    private static final byte FINISH = 3;
    private static final byte FORCED = 4;
    private static final byte RESET = 5;

    protected final UUID sender;
    protected final PlayerPair pair;
    protected final String node;
    private final byte code;

    public static UnlockRelationshipPacket decode(FriendlyByteBuf friendlyByteBuf) {
        UUID sender = friendlyByteBuf.readUUID();
        PlayerPair pair = PlayerPair.fromNetwork(friendlyByteBuf);
        String node = friendlyByteBuf.readUtf();
        byte code = friendlyByteBuf.readByte();

        return switch (code) {
            case INVITE -> new Invite(sender, pair, node);
            case ACCEPT -> new Accept(sender, pair, node);
            case CANCEL -> new Cancel(sender, pair, node);
            case FINISH -> new Finish(sender, pair, node);
            case FORCED -> new Forced(sender, pair, node);
            case RESET -> new Reset(sender, pair);
            default -> throw new IllegalArgumentException("Packet Code: " + code);
        };

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

            UUID other = this.pair.getOther(sender.getUUID());
            ServerPlayer player = server.getPlayerList().getPlayer(other);
            if (player == null)
                return;

            if (!this.pair.getOther(other).equals(sender.getUUID()))
                return;

            final InteractTree tree = InteractTreeManager.INSTANCE.get(RelationshipComponent.FRIEND_INTERACT_TREE);

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
            Minecraft minecraft = Minecraft.getInstance();
            LocalPlayer localPlayer = minecraft.player;

            RelationshipComponent relationship = UserDataAttachment.getAttachment(localPlayer).getRelationship();

            UUID local = localPlayer.getUUID();

            if (local.equals(this.sender)) {
                ((IAnimateHandler) localPlayer).simpleanimator$playAnimate(Animations.UNLOCK_INVITE, true);
            } else {
                UUID other = this.pair.getOther(local);
                Player player = minecraft.level.getPlayerByUUID(other);

                if (player == null)
                    return;

                InteractTreeInstance instance = relationship.get(other);
                World2ScreenWidgetLayer.INSTANCE.addWorldPositionObject(other, new UnlockRequestW2SButton(
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

            UUID other = this.pair.getOther(sender.getUUID());
            ServerPlayer player = server.getPlayerList().getPlayer(other);
            if (player == null)
                return;

            if (!this.pair.getOther(other).equals(sender.getUUID()))
                return;

            Pair<InteractTreeInstance, InteractTreeInstance> relevantInstance = RelationshipComponent.getRelevantInstance(pair, player.serverLevel());

            if (relevantInstance == null) {
                return;
            }

            final InteractTree tree = relevantInstance.getFirst().getTree();

            if (!tree.contains(this.node)) {
                return;
            }

            InteractTreeNode tNode = tree.get(this.node);

            INetwork network = SimpleAnimator.getNetwork();
            int count = player.getInventory().countItem(tNode.getCurrency());

            if (count < tNode.getPrice())
                return;


            if (InteractTreeInstance.unlock(relevantInstance, node, true)) {
                return;
            }

            PlayerUtils.costItems(player, tNode.getCurrency(), tNode.getPrice());

            network.sendToPlayer(this, sender);
            network.sendToPlayer(this, player);

            TriggerTypes.UNLOCK_RELATIONSHIP.get().trigger(sender, node);
            TriggerTypes.UNLOCK_RELATIONSHIP.get().trigger(player, node);
            TriggerTypes.COMPLETED_RELATIONSHIP.get().trigger(sender, player.getUUID());
            TriggerTypes.COMPLETED_RELATIONSHIP.get().trigger(player, sender.getUUID());
        }

        @OnlyIn(Dist.CLIENT)
        @Override
        protected void sync() {
            Minecraft minecraft = Minecraft.getInstance();
            UUID local = minecraft.player.getUUID();

            RelationshipComponent relationship = UserDataAttachment.getAttachment(minecraft.player).getRelationship();

            UUID other = this.pair.getOther(local);
            Player player = minecraft.level.getPlayerByUUID(other);

            if (player == null)
                return;

            InteractTreeInstance instance = relationship.get(other);
            instance.unlock(this.node);

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
            World2ScreenWidgetLayer.INSTANCE.remove(other);
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

    public static class Forced extends UnlockRelationshipPacket {
        public Forced(UUID sender, PlayerPair pair, String node) {
            super(sender, pair, node, FORCED);
        }

        @Override
        protected void update(@NotNull ServerPlayer serverPlayer) {

        }

        @Override
        protected void sync() {
            Minecraft minecraft = Minecraft.getInstance();

            RelationshipComponent relationship = UserDataAttachment.getAttachment(minecraft.player).getRelationship();

            UUID local = minecraft.player.getUUID();
            UUID other = this.pair.getOther(local);

            InteractTreeInstance instance = relationship.get(other);
            if (this.node.equals("all")) {
                instance.unlockAll();
            } else {
                instance.unlock(this.node);
            }
        }
    }

    public static class Reset extends UnlockRelationshipPacket {
        public Reset(UUID sender, PlayerPair pair) {
            super(sender, pair, "", RESET);
        }

        @Override
        protected void update(@NotNull ServerPlayer serverPlayer) {

        }

        @Override
        protected void sync() {
            Minecraft minecraft = Minecraft.getInstance();

            RelationshipComponent relationship = UserDataAttachment.getAttachment(minecraft.player).getRelationship();

            UUID local = minecraft.player.getUUID();
            UUID other = this.pair.getOther(local);

            InteractTreeInstance instance = relationship.get(other);
            instance.reset();
        }
    }
}
