package net.quepierts.thatskyinteractions.data.tree.node;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.quepierts.thatskyinteractions.client.gui.animate.ScreenAnimator;
import net.quepierts.thatskyinteractions.client.gui.component.tree.BlockButton;
import net.quepierts.thatskyinteractions.client.gui.component.tree.TreeNodeButton;
import net.quepierts.thatskyinteractions.data.tree.NodeState;
import org.jetbrains.annotations.NotNull;

public class BlockNode extends InteractTreeNode {
    public static final String TYPE = "block";
    protected BlockNode(JsonObject json) {
        super(json);
    }

    public BlockNode(FriendlyByteBuf friendlyByteBuf) {
        super(friendlyByteBuf);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public @NotNull TreeNodeButton asButton(ScreenAnimator animator, NodeState state) {
        return new BlockButton(this.id, this.x, this.y, animator);
    }

    @Override
    protected String type() {
        return TYPE;
    }
}
