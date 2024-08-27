package net.quepierts.thatskyinteractions.data.tree.node;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import net.quepierts.simpleanimator.core.SimpleAnimator;
import net.quepierts.simpleanimator.core.network.INetwork;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.client.gui.animate.ScreenAnimator;
import net.quepierts.thatskyinteractions.client.gui.component.tree.FriendButton;
import net.quepierts.thatskyinteractions.client.gui.component.tree.NicknameButton;
import net.quepierts.thatskyinteractions.client.gui.component.tree.TreeNodeButton;
import net.quepierts.thatskyinteractions.data.PlayerPair;
import net.quepierts.thatskyinteractions.data.TSIUserDataStorage;
import net.quepierts.thatskyinteractions.data.tree.NodeState;
import net.quepierts.thatskyinteractions.network.packet.astrolabe.AstrolabeSyncPacket;
import org.jetbrains.annotations.NotNull;

public class FriendNode extends InteractTreeNode {
    public static final String TYPE = "friend";
    public FriendNode(JsonObject json) {
        super(json);
    }

    public FriendNode(FriendlyByteBuf friendlyByteBuf) {
        super(friendlyByteBuf);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public @NotNull TreeNodeButton asButton(ScreenAnimator animator, NodeState state) {
        if (state == NodeState.UNLOCKED)
            return new NicknameButton(this.id, this.x, this.y, animator);
        return new FriendButton(this.id, this.x, this.y, animator, state);
    }

    @Override
    protected String type() {
        return TYPE;
    }

    @Override
    public void onUnlock(PlayerPair pair, boolean onServer) {
        if (!onServer)
            return;

        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server == null)
            return;
        PlayerList playerList = server.getPlayerList();
        TSIUserDataStorage userDataManager = ThatSkyInteractions.getInstance().getProxy().getUserDataManager();

        ServerPlayer leftPlayer = playerList.getPlayer(pair.getLeft());
        ServerPlayer rightPlayer = playerList.getPlayer(pair.getRight());

        if (leftPlayer == null || rightPlayer == null)
            return;
        userDataManager.getUserData(pair.getLeft()).addFriend(rightPlayer);
        userDataManager.getUserData(pair.getRight()).addFriend(leftPlayer);

        INetwork network = SimpleAnimator.getNetwork();

        network.sendToPlayer(new AstrolabeSyncPacket.AddFriend(pair.getRight()), leftPlayer);
        network.sendToPlayer(new AstrolabeSyncPacket.AddFriend(pair.getLeft()), rightPlayer);
    }
}
