package net.quepierts.thatskyinteractions.data.tree.node;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;

public class RootNode extends InteractTreeNode {
    public static final String TYPE = "root";
    public RootNode(JsonObject json) {
        super(json);
    }

    public RootNode(FriendlyByteBuf friendlyByteBuf) {
        super(friendlyByteBuf);
    }

    @Override
    protected String type() {
        return TYPE;
    }
}
