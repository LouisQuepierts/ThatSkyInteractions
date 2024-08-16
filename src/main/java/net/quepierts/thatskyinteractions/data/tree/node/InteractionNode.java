package net.quepierts.thatskyinteractions.data.tree.node;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.quepierts.simpleanimator.core.JsonUtils;
import net.quepierts.thatskyinteractions.client.gui.animate.ScreenAnimator;
import net.quepierts.thatskyinteractions.client.gui.component.tree.InteractionButton;
import net.quepierts.thatskyinteractions.client.gui.component.tree.LockedButton;
import net.quepierts.thatskyinteractions.client.gui.component.tree.TreeNodeButton;
import net.quepierts.thatskyinteractions.data.tree.NodeState;
import org.jetbrains.annotations.NotNull;

public class InteractionNode extends InteractTreeNode {
    public static final String TYPE = "interaction";
    private final ResourceLocation interaction;
    private final int level;

    public InteractionNode(JsonObject json) {
        super(json);
        this.interaction = ResourceLocation.parse(json.get("interact").getAsString());
        this.level = JsonUtils.getInt("level", json, 1);
    }

    public InteractionNode(FriendlyByteBuf friendlyByteBuf) {
        super(friendlyByteBuf);
        this.interaction = friendlyByteBuf.readResourceLocation();
        this.level = friendlyByteBuf.readVarInt();
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public @NotNull TreeNodeButton asButton(ScreenAnimator animator, NodeState state) {
        if (state == NodeState.LOCKED) {
            return new LockedButton(this.id, this.x, this.y, animator);
        }
        return new InteractionButton(this.id, this.x, this.y, this.price, Component.empty(), animator, this.interaction, this.level, state);
    }

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) {
        super.write(friendlyByteBuf);
        friendlyByteBuf.writeResourceLocation(this.interaction);
        friendlyByteBuf.writeVarInt(this.level);
    }

    @Override
    protected String type() {
        return TYPE;
    }
}
