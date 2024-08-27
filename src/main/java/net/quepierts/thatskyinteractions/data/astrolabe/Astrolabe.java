package net.quepierts.thatskyinteractions.data.astrolabe;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectLists;
import net.minecraft.network.FriendlyByteBuf;
import net.quepierts.thatskyinteractions.data.astrolabe.node.AstrolabeNode;
import org.apache.commons.lang3.stream.Streams;
import org.apache.logging.log4j.core.util.Integers;

import java.io.Reader;
import java.util.List;
import java.util.Map;

public class Astrolabe {
    private final ObjectList<AstrolabeNode> nodes;
    private final ObjectList<Connection> connections;

    public Astrolabe(List<AstrolabeNode> nodes, List<Connection> connections) {
        this.nodes = ObjectLists.unmodifiable(new ObjectArrayList<>(nodes));
        this.connections = ObjectLists.unmodifiable(new ObjectArrayList<>(connections));
    }

    public ObjectList<AstrolabeNode> getNodes() {
        return nodes;
    }

    public ObjectList<Connection> getConnections() {
        return connections;
    }

    public static Astrolabe fromStream(Reader reader) {
        JsonObject object = JsonParser.parseReader(reader).getAsJsonObject();
        return serialize(object);
    }

    private static Astrolabe serialize(JsonObject object) {
        JsonArray nodeArray = object.getAsJsonArray("nodes");
        List<AstrolabeNode> nodes = Streams.of(nodeArray.iterator())
                .map(JsonElement::getAsJsonObject)
                .map(AstrolabeNode::serialize)
                .toList();

        JsonArray connectionArray = object.getAsJsonArray("connections");
        List<Connection> connections = Streams.of(connectionArray.iterator())
                .map(JsonElement::getAsJsonObject)
                .map(Connection::serialize)
                .toList();
        return new Astrolabe(nodes, connections);
    }

    public static void toNetwork(FriendlyByteBuf byteBuf, Astrolabe astrolabe) {
        byteBuf.writeCollection(astrolabe.nodes, AstrolabeNode::toNetwork);
        byteBuf.writeCollection(astrolabe.connections, Connection::toNetwork);
    }

    public static Astrolabe fromNetwork(FriendlyByteBuf byteBuf) {
        List<AstrolabeNode> nodes = byteBuf.readList(AstrolabeNode::fromNetwork);
        List<Connection> connections = byteBuf.readList(Connection::fromNetwork);
        return new Astrolabe(nodes, connections);
    }

    public int size() {
        return this.nodes.size();
    }

    public record Connection(int a, int b) {
        public static Connection serialize(JsonObject jsonObject) {
            for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
                int src = Integers.parseInt(entry.getKey());
                int dest = Integers.parseInt(entry.getValue().getAsString());
                return new Connection(src, dest);
            }

            throw new IllegalArgumentException();
        }

        public static void toNetwork(FriendlyByteBuf byteBuf, Connection connection) {
            byteBuf.writeVarInt(connection.a);
            byteBuf.writeVarInt(connection.b);
        }

        public static Connection fromNetwork(FriendlyByteBuf byteBuf) {
            return new Connection(byteBuf.readVarInt(), byteBuf.readVarInt());
        }
    }
}
