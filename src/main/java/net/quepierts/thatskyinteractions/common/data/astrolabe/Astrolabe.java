package net.quepierts.thatskyinteractions.common.data.astrolabe;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectLists;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.quepierts.thatskyinteractions.common.data.astrolabe.node.AstrolabeNode;
import org.apache.commons.lang3.stream.Streams;
import org.apache.logging.log4j.core.util.Integers;

import java.io.Reader;
import java.util.List;
import java.util.Map;

public class Astrolabe {
    public static final Codec<Astrolabe> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            AstrolabeNode.CODEC.listOf().fieldOf("nodes").forGetter(Astrolabe::getNodes),
            Connection.CODEC.listOf().fieldOf("connections").forGetter(Astrolabe::getConnections)
    ).apply(instance, Astrolabe::new));

    public static final StreamCodec<ByteBuf, Astrolabe> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.collection(ObjectArrayList::new, AstrolabeNode.STREAM_CODEC),
            Astrolabe::getNodes,
            ByteBufCodecs.collection(ObjectArrayList::new, Connection.STREAM_CODEC),
            Astrolabe::getConnections,
            Astrolabe::new
    );

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
        Astrolabe.STREAM_CODEC.encode(byteBuf, astrolabe);
    }

    public static Astrolabe fromNetwork(FriendlyByteBuf byteBuf) {
        return Astrolabe.STREAM_CODEC.decode(byteBuf);
    }

    public int size() {
        return this.nodes.size();
    }

    public record Connection(int a, int b) {
        public static final Codec<Connection> CODEC = Codec.pair(Codec.INT, Codec.INT)
                .xmap(
                        pair -> new Connection(pair.getFirst(), pair.getSecond()),
                        connection -> Pair.of(connection.a, connection.b)
                );

        public static final StreamCodec<ByteBuf, Connection> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.VAR_INT,
                Connection::a,
                ByteBufCodecs.VAR_INT,
                Connection::b,
                Connection::new
        );

        public static Connection serialize(JsonObject jsonObject) {
            for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
                int src = Integers.parseInt(entry.getKey());
                int dest = Integers.parseInt(entry.getValue().getAsString());
                return new Connection(src, dest);
            }

            throw new IllegalArgumentException();
        }
    }
}
