package net.quepierts.thatskyinteractions.data.tree.node;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.quepierts.thatskyinteractions.client.gui.animate.ScreenAnimator;
import net.quepierts.thatskyinteractions.client.gui.component.tree.LikeButton;
import net.quepierts.thatskyinteractions.client.gui.component.tree.LockedButton;
import net.quepierts.thatskyinteractions.client.gui.component.tree.TreeNodeButton;
import net.quepierts.thatskyinteractions.data.tree.NodeState;
import org.jetbrains.annotations.NotNull;

public class LikeNode extends InteractTreeNode {
    public static final String TYPE = "like";
    public LikeNode(JsonObject json) {
        super(json);
    }

    public LikeNode(FriendlyByteBuf friendlyByteBuf) {
        super(friendlyByteBuf);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public @NotNull TreeNodeButton asButton(ScreenAnimator animator, NodeState state) {
        if (state == NodeState.LOCKED) {
            return new LockedButton(this.id, this.x, this.y, animator);
        }
        return new LikeButton(this.id, this.x, this.y, animator, state);
    }

    @Override
    protected String type() {
        return TYPE;
    }
}
