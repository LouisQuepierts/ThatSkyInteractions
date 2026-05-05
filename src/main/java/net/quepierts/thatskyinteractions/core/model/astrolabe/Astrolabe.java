package net.quepierts.thatskyinteractions.core.model.astrolabe;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectLists;
import lombok.Getter;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import org.apache.logging.log4j.core.util.Integers;

import java.util.List;
import java.util.Map;

@Getter
public final class Astrolabe {

    private final ObjectList<AstrolabeNode> nodes;
    private final ObjectList<Connection> connections;

    public Astrolabe(List<AstrolabeNode> nodes, List<Connection> connections) {
        this.nodes = ObjectLists.unmodifiable(new ObjectArrayList<>(nodes));
        this.connections = ObjectLists.unmodifiable(new ObjectArrayList<>(connections));
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
