package net.quepierts.thatskyinteractions.data.astrolabe.node;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;

public class FriendNode extends AstrolabeNode {
    public FriendNode(int x, int y, DescriptionPosition namePosition) {
        super(x, y, namePosition);
    }

    public FriendNode(FriendlyByteBuf byteBuf) {
        super(byteBuf);
    }

    public FriendNode(JsonObject jsonObject) {
        super(jsonObject);
    }
}
