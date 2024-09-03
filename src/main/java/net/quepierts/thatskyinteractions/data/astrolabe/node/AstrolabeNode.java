package net.quepierts.thatskyinteractions.data.astrolabe.node;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.network.FriendlyByteBuf;

import java.util.Locale;
import java.util.function.Function;

public class AstrolabeNode {
    private static final Factory DEFAULT_FACTORY = new Factory(AstrolabeNode::new, AstrolabeNode::new);
    private static final Object2ObjectMap<String, Factory> FACTORIES = new Object2ObjectOpenHashMap<>();
    public final int x;
    public final int y;
    public final DescriptionPosition namePosition;

    protected AstrolabeNode(int x, int y, DescriptionPosition descriptionPosition) {
        this.x = x;
        this.y = y;
        this.namePosition = descriptionPosition;
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

        if (jsonObject.has("name_position")) {
            this.namePosition = DescriptionPosition.fromString(jsonObject.get("name_position").getAsString());
        } else {
            this.namePosition = DescriptionPosition.DOWN;
        }
    }

    public AstrolabeNode(FriendlyByteBuf byteBuf) {
        this.x = byteBuf.readVarInt();
        this.y = byteBuf.readVarInt();
        this.namePosition = byteBuf.readEnum(DescriptionPosition.class);
    }

    public static void toNetwork(FriendlyByteBuf byteBuf, AstrolabeNode node) {
        byteBuf.writeUtf(node.typ());
        byteBuf.writeVarInt(node.x);
        byteBuf.writeVarInt(node.y);
        byteBuf.writeEnum(node.namePosition);
    }

    protected record Factory(
            Function<JsonObject, AstrolabeNode> fromJson,
            Function<FriendlyByteBuf, AstrolabeNode> fromNetwork
    ) {}

    public static void register() {
        FACTORIES.put("", DEFAULT_FACTORY);
        FACTORIES.put("friend", new Factory(FriendNode::new, FriendNode::new));
    }

    public enum DescriptionPosition {
        UP,
        DOWN,
        LEFT,
        RIGHT;

        public static DescriptionPosition fromString(String value) {
            return REF.getOrDefault(value.toLowerCase(Locale.ROOT), DOWN);
        }

        private static final ImmutableMap<String, DescriptionPosition> REF;

        static {
            ImmutableMap.Builder<String, DescriptionPosition> builder = ImmutableMap.builder();
            for (DescriptionPosition value : DescriptionPosition.values()) {
                builder.put(value.name().toLowerCase(Locale.ROOT), value);
            }
            REF = builder.build();
        }
    }
}
