package net.quepierts.thatskyinteractions.common.data;

import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.*;
import java.util.function.Function;

public class Codecs {
    public static <A, B> Codec<Map.Entry<A, B>> entry(Codec<A> k, Codec<B> v) {
        return RecordCodecBuilder.create(instance -> instance.group(
                k.fieldOf("k").forGetter(Map.Entry::getKey),
                v.fieldOf("v").forGetter(Map.Entry::getValue)
        ).apply(instance, Map::entry));
    }

    public static <A, B> Codec<Map<A, B>> map(Codec<A> a, Codec<B> b) {
        return entry(a, b)
                .listOf()
                .xmap(Codecs::toMap, Codecs::toList);
    }

    public static <T> Codec<Set<T>> set(Codec<T> codec) {
        return codec.listOf()
                .xmap(HashSet::new, ArrayList::new);
    }

    public static <A, B, T extends Map<A, B>> Codec<T> map(Codec<A> a, Codec<B> b, Function<Integer, T> constructor) {
        return entry(a, b)
                .listOf()
                .xmap(list -> toMap(list, constructor), Codecs::toList);
    }

    private static <A, B> Map<A, B> toMap(Collection<Map.Entry<A, B>> list) {
        HashMap<A, B> map = Maps.newHashMapWithExpectedSize(list.size());
        for (Map.Entry<A, B> entry : list) {
            map.put(entry.getKey(), entry.getValue());
        }
        return map;
    }

    private static <A, B, T extends Map<A, B>> T toMap(Collection<Map.Entry<A, B>> list, Function<Integer, T> constructor) {
        T map = constructor.apply(list.size());
        for (Map.Entry<A, B> entry : list) {
            map.put(entry.getKey(), entry.getValue());
        }
        return map;
    }

    private static <A, B> List<Map.Entry<A, B>> toList(Map<A, B> map) {
        return new ArrayList<>(map.entrySet());
    }
}
