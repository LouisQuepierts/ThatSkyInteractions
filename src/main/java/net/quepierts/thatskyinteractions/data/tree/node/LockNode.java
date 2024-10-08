package net.quepierts.thatskyinteractions.data.tree.node;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.quepierts.thatskyinteractions.client.gui.animate.ScreenAnimator;
import net.quepierts.thatskyinteractions.client.gui.component.tree.LockButton;
import net.quepierts.thatskyinteractions.client.gui.component.tree.TreeNodeButton;
import net.quepierts.thatskyinteractions.data.tree.NodeState;
import org.jetbrains.annotations.NotNull;

public class LockNode extends InteractTreeNode {
    public static final String TYPE = "lock";
    public LockNode(JsonObject json) {
        super(json);
    }

    public LockNode(FriendlyByteBuf friendlyByteBuf) {
        super(friendlyByteBuf);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public @NotNull TreeNodeButton asButton(ScreenAnimator animator, NodeState state) {
        return new LockButton(this.id, this.x, this.y, this.price, Component.empty(), animator, state);
    }

    @Override
    protected String type() {
        return TYPE;
    }

    @Override
    public Item getCurrency() {
        return Items.RED_CANDLE;
    }
}
