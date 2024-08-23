package net.quepierts.thatskyinteractions.data.astrolabe.node;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;

public class FriendNode extends AstrolabeNode {
    protected FriendNode(int x, int y) {
        super(x, y);
    }

    public FriendNode(FriendlyByteBuf byteBuf) {
        super(byteBuf);
    }

    public FriendNode(JsonObject jsonObject) {
        super(jsonObject);
    }
}
