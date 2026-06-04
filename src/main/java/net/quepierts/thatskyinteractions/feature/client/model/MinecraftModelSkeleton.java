package net.quepierts.thatskyinteractions.feature.client.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayFIFOQueue;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.client.model.geom.ModelPart;
import net.quepierts.thatskyinteractions.feature.mixin.vanilla.client.accessor.ModelPartAccessor;
import net.quepierts.veynir.backend.skeleton.SkeletonLayout;
import net.quepierts.veynir.core.adapter.TransformAccessor;
import org.joml.Math;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class MinecraftModelSkeleton {

    @Getter
    private final SkeletonLayout        layout;
    private final Map<String, Bone>     byName;
    private final List<Bone>            byId;

    public Bone get(@NonNull String name) {
        return this.byName.get(name);
    }

    public Bone get(int id) {
        return this.byId.get(id);
    }

    public List<Bone> getEntries() {
        return this.byId;
    }

    public static MinecraftModelSkeleton auto(
            @NonNull ModelPart              root,
            @NonNull SkeletonLayout         layout
    ) {
        final var map       = ImmutableMap.<String, Bone>builder();
        final var list      = new ArrayList<Bone>(layout.size());

        final var queue     = new ObjectArrayFIFOQueue<Bone>();
        queue.enqueue(new Bone("root", root, layout.id("root")));

        while (!queue.isEmpty()) {
            var entry        = queue.dequeue();
            var children     = getChildren(entry.part);

            for (var child : children.entrySet()) {
                final var name = child.getKey();
                final var id = layout.id(name);

                var childEntry = new Bone(name, child.getValue(), id);

                if (id != -1) {
                    map.put(childEntry.name, childEntry);
                    list.add(childEntry);
                }

                queue.enqueue(childEntry);
            }
        }

        list.sort(Comparator.comparingInt(Bone::id));

        return new MinecraftModelSkeleton(
                layout,
                map.build(),
                ImmutableList.copyOf(list)
        );
    }

    public static MinecraftModelSkeleton manual(
            @NonNull ModelPart              root,
            @NonNull SkeletonLayout         layout,
            @NonNull String @NonNull []     names
    ) {
        final var map       = ImmutableMap.<String, Bone>builder();
        final var list      = new ArrayList<Bone>(layout.size());

        final var lookup    = root.createPartLookup();

        for (var name : names) {
            final var part = "root".equals(name) ? root : lookup.apply(name);
            final var entry = new Bone(name, part, layout.id(name));
            map.put(entry.name, entry);
            list.add(entry);
        }

        list.sort(Comparator.comparingInt(Bone::id));

        return new MinecraftModelSkeleton(
                layout,
                map.build(),
                list
        );
    }


    public record Bone(
            String      name,
            ModelPart   part,
            int         id
    ) implements TransformAccessor {

        @Override
        public void setPosition(
                final float x,
                final float y,
                final float z
        ) {
            this.part.setPos(x, y, z);
        }

        @Override
        public void setEulerAngle(
                final float x,
                final float y,
                final float z
        ) {
            this.part.setRotation(x, y, z);
        }

        @Override
        public void setQuaternion(
                final float x,
                final float y,
                final float z,
                final float w
        ) {
            float eulerX = org.joml.Math.atan2(y * z + w * x, 0.5f - x * x - y * y);
            float eulerY = org.joml.Math.safeAsin(-2.0f * (x * z - w * y));
            float eulerZ = Math.atan2(x * y + w * z, 0.5f - y * y - z * z);

            this.part.setRotation(
                    eulerX,
                    eulerY,
                    eulerZ
            );
        }

        @Override
        public void setScale(
                final float x,
                final float y,
                final float z
        ) {
            this.part.xScale = x;
            this.part.yScale = y;
            this.part.zScale = z;
        }

        public void setPosition(
                final float x,
                final float y,
                final float z,
                final float alpha
        ) {
            final var part = this.part;
            if (alpha == 1.0f) {
                part.setPos(x, y, z);
            } else if (alpha > 0.0f) {
                part.setPos(
                        part.x * (1.0f - alpha) + x * alpha,
                        part.y * (1.0f - alpha) + y * alpha,
                        part.z * (1.0f - alpha) + z * alpha
                );
            }
        }

        public void setQuaternion(
                final float x,
                final float y,
                final float z,
                final float w,
                final float alpha
        ) {
            if (alpha == 1.0f) {
                this.setQuaternion(x, y, z, w);
            } else if (alpha > 0.0f) {
                float eulerX = org.joml.Math.atan2(y * z + w * x, 0.5f - x * x - y * y);
                float eulerY = org.joml.Math.safeAsin(-2.0f * (x * z - w * y));
                float eulerZ = Math.atan2(x * y + w * z, 0.5f - y * y - z * z);

                final var part = this.part;
                part.setRotation(
                        part.xRot * (1.0f - alpha) + eulerX * alpha,
                        part.yRot * (1.0f - alpha) + eulerY * alpha,
                        part.zRot * (1.0f - alpha) + eulerZ * alpha
                );
            }
        }

        public void setScale(
                final float x,
                final float y,
                final float z,
                final float alpha
        ) {
            final var part = this.part;
            if (alpha == 1.0f) {
                part.xScale = x;
                part.yScale = y;
                part.zScale = z;
            } else if (alpha > 0.0f) {
                part.xScale = part.xScale * (1.0f - alpha) + x * alpha;
                part.yScale = part.yScale * (1.0f - alpha) + y * alpha;
                part.zScale = part.zScale * (1.0f - alpha) + z * alpha;
            }
        }

    }

    private static Map<String, ModelPart> getChildren(ModelPart thiz) {
        return ((ModelPartAccessor) (Object) thiz).getChildren();
    }
}
