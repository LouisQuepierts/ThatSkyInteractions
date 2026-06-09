package net.quepierts.thatskyinteractions.core.animation.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.quepierts.veynir.core.util.ArrayIterator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Getter
@AllArgsConstructor(staticName = "direct")
public sealed class PlayerMask {

    private int mask;

    public static PlayerMask of(PlayerBone... bones) {
        return new PlayerMask(toMask(new ArrayIterator<>(bones)));
    }

    public static PlayerMask of(List<PlayerBone> bones) {
        return new PlayerMask(toMask(bones.iterator()));
    }

    public static PlayerMask of(PlayerMask other) {
        return new PlayerMask(other.mask);
    }

    public static PlayerMask empty() {
        return new PlayerMask(0);
    }

    public static PlayerMask all() {
        return new PlayerMask(0xFFFFFFFF);
    }

    public void add(final PlayerBone bone) {
        this.mask |= bone.getBit();
    }

    public boolean contains(final PlayerBone bone) {
        return (this.mask & bone.getBit()) != 0;
    }

    public boolean contains(final PlayerMask mask) {
        return (this.mask & mask.mask) == mask.mask;
    }

    public boolean collision(final PlayerMask mask) {
        return (this.mask & mask.mask) != 0;
    }

    public void and(final PlayerMask mask) {
        this.mask &= mask.mask;
    }

    public void or(final PlayerMask mask) {
        this.mask |= mask.mask;
    }

    public void xor(final PlayerMask mask) {
        this.mask ^= mask.mask;
    }

    public void not(final PlayerMask mask) {
        this.mask = ~this.mask & mask.mask;
    }

    public void clear() {
        this.mask = 0;
    }

    public Immutable toImmutable() {
        return new Immutable(this.mask);
    }

    public List<PlayerBone> toList() {
        return fromMask(this.mask);
    }

    private static int toMask(final Iterator<PlayerBone> iterator) {
        int mask = 0;
        while (iterator.hasNext()) {
            mask |= iterator.next().getBit();
        }
        return mask;
    }

    private static List<PlayerBone> fromMask(final int mask) {
        var list = new ArrayList<PlayerBone>(PlayerBone.size());
        for (var bone : PlayerBone.values()) {
            if (bone.in(mask)) {
                list.add(bone);
            }
        }
        return list;
    }


    public static final class Immutable extends PlayerMask {
        private Immutable(final int mask) {
            super(mask);
        }

        @Override
        public void add(final PlayerBone bone) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void and(final PlayerMask mask) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void or(final PlayerMask mask) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void xor(final PlayerMask mask) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void not(final PlayerMask mask) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void clear() {
            throw new UnsupportedOperationException();
        }
    }

}
