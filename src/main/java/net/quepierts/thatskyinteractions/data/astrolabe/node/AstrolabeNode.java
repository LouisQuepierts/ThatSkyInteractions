package net.quepierts.thatskyinteractions.data.astrolabe.node;

import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.network.FriendlyByteBuf;

import java.util.function.Function;

public class AstrolabeNode {
    private static final Factory DEFAULT_FACTORY = new Factory(AstrolabeNode::new, AstrolabeNode::new);
    private static final Object2ObjectMap<String, Factory> FACTORIES = new Object2ObjectOpenHashMap<>();
    public final int x;
    public final int y;

    protected AstrolabeNode(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public static AstrolabeNode serialize(JsonObject obj) {
        String type = obj.get("type").getAsString();
        return FACTORIES.getOrDefault(type, DEFAULT_FACTORY).fromJson.apply(obj);
    }

    public static AstrolabeNode fromNetwork(FriendlyByteBuf byteBuf) {
        String type = byteBuf.readUtf();
        return FACTORIES.getOrDefault(type, DEFAULT_FACTORY).fromNetwork.apply(byteBuf);
    }

    protected String typ() {
        return "";
    }

    public AstrolabeNode(JsonObject jsonObject) {
        this.x = jsonObject.get("x").getAsInt();
        this.y = jsonObject.get("y").getAsInt();
    }

    public AstrolabeNode(FriendlyByteBuf byteBuf) {
        this.x = byteBuf.readVarInt();
        this.y = byteBuf.readVarInt();
    }

    public static void toNetwork(FriendlyByteBuf byteBuf, AstrolabeNode node) {
        byteBuf.writeUtf(node.typ());
        byteBuf.writeVarInt(node.x);
        byteBuf.writeVarInt(node.y);
    }

    protected record Factory(
            Function<JsonObject, AstrolabeNode> fromJson,
            Function<FriendlyByteBuf, AstrolabeNode> fromNetwork
    ) {}

    public static void register() {
        FACTORIES.put("", DEFAULT_FACTORY);
        FACTORIES.put("friend", new Factory(FriendNode::new, FriendNode::new));
    }
}
