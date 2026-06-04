package net.quepierts.thatskyinteractions.core.animation.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.quepierts.veynir.core.util.ArrayIterator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Getter
@AllArgsConstructor(staticName = "direct")
public final class PlayerMask {

    public static final PlayerMask EMPTY    = new PlayerMask(0);
    public static final PlayerMask ALL      = new PlayerMask(0xFFFFFFFF);

    private int mask;

    public static PlayerMask of(PlayerBone... bones) {
        return new PlayerMask(toMask(new ArrayIterator<>(bones)));
    }

    public static PlayerMask of(List<PlayerBone> bones) {
        return null;
    }

    public void add(final PlayerBone bone) {
        this.mask |= bone.getBit();
    }

    public boolean contains(final PlayerBone bone) {
        return (this.mask & bone.getBit()) != 0;
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
}
