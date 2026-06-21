package net.quepierts.thatskyinteractions.feature.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.objects.ObjectArrayPriorityQueue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.util.StringRepresentable;
import org.jspecify.annotations.NonNull;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;

@Slf4j
public record Order(
        int                     priority,
        Optional<Identifier>    target,
        Relation                relation

) {

    public static final Order DEFAULT = new Order(0, Optional.empty(), Relation.WHATEVER);

    public static final Codec<Order> CODEC
            = RecordCodecBuilder.<Order>create(instance -> instance.group(
                    Codec.INT.fieldOf("priority").forGetter(Order::priority),
                    Identifier.CODEC.optionalFieldOf("target").forGetter(Order::target),
                    Relation.CODEC.optionalFieldOf("relation", Relation.WHATEVER).forGetter(Order::relation)
            ).apply(instance, Order::new)).withAlternative(
                    Codec.INT.xmap(
                            priority -> new Order(priority, Optional.empty(), Relation.WHATEVER),
                            order -> order.priority
                    )
            );

    public static final StreamCodec<ByteBuf, Order> STREAM_CODEC
            = StreamCodec.composite(
                    ByteBufCodecs.INT,
                    Order::priority,
                    ByteBufCodecs.optional(Identifier.STREAM_CODEC),
                    Order::target,
                    Relation.STREAM_CODEC,
                    Order::relation,
                    Order::new
            );

    public static <T> void sort(
            final @NonNull Map<Identifier, T>           preparations,
            final @NonNull Function<T, Order>           mapper,
            final @NonNull BiConsumer<Identifier, T>    consumer
    ) {

        @RequiredArgsConstructor
        class Node implements Comparable<Node> {
            final   Identifier          key;
            final   T                   value;
            final   Order               order;

            final   List<Identifier>    outEdges    = new ArrayList<>();
                    int                 inDegree    = 0;

            @Override
            public int compareTo(@NonNull final Node o) {
                return Integer.compare(order.priority, o.order.priority);
            }
        }

        final var nodes                 = new HashMap<Identifier, Node>(preparations.size());

        for (final var entry : preparations.entrySet()) {
            final var key               = entry.getKey();
            final var value             = entry.getValue();
            final var order             = mapper.apply(value);

            nodes.put(key, new Node(key, value, order));
        }

        for (final var node : nodes.values()) {
            final var order             = node.order;

            if (order.target().isPresent()) {
                final var key   = order.target().get();
                switch (order.relation()) {
                    case BEFORE: {
                        final var target = nodes.get(key);

                        if (target == null) {
                            // log and silently ignore
                            log.warn("Target of order {} is not found", key);
                            continue;
                        }

                        node            .outEdges
                                        .add(target.key);
                        target          .inDegree++;

                        break;
                    }
                    case AFTER: {
                        final var target = nodes.get(key);

                        if (target == null) {
                            // log and silently ignore
                            log.warn("Target of order {} is not found", key);
                            continue;
                        }

                        target          .outEdges
                                        .add(node.key);
                        node            .inDegree++;

                        break;
                    }
                }
            }
        }

        final var queue     = new ObjectArrayPriorityQueue<>(Node::compareTo);
        for (final var node : nodes.values()) {
            if (node.inDegree == 0) {
                queue.enqueue(node);
            }
        }

        final var result    = new ArrayList<Node>(preparations.size());
        while (!queue.isEmpty()) {
            final var node = queue.dequeue();
            result.add(node);
            for (final var outEdge : node.outEdges) {
                final var outNode = nodes.get(outEdge);
                outNode.inDegree--;
                if (outNode.inDegree == 0) {
                    queue.enqueue(outNode);
                }
            }
        }

        if (result.size() != nodes.size()) {
            // log and silently ignore
            log.warn("Circular dependency detected in order");
        }

        for (final var node : result) {
            consumer.accept(node.key, node.value);
        }

    }

    @Getter
    @RequiredArgsConstructor
    public enum Relation implements StringRepresentable {
        WHATEVER("none"),
        BEFORE("before"),
        AFTER("after");

        private static final Relation[] VALUES = values();

        public static final Codec<Relation> CODEC
                = StringRepresentable.fromEnum(() -> VALUES);

        public static final StreamCodec<ByteBuf, Relation> STREAM_CODEC
                = ByteBufCodecs.BYTE.map(
                        Relation::fromByte,
                        Relation::toByte
                );

        public static Relation fromByte(byte b) {
            return VALUES[b];
        }

        public byte toByte() {
            return (byte) ordinal();
        }


        private final String serializedName;
    }

}
