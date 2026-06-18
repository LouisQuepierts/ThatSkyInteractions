package net.quepierts.thatskyinteractions.feature.client.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayFIFOQueue;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.UtilityClass;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.quepierts.thatskyinteractions.feature.animation.model.ModelSkeleton;
import net.quepierts.thatskyinteractions.feature.mixin.vanilla.client.accessor.ModelPartAccessor;
import net.quepierts.veynir.backend.skeleton.SkeletonLayout;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@UtilityClass
public final class MinecraftModelSkeleton {

    public static ModelSkeleton auto(
            @NonNull ModelPart              root,
            @NonNull SkeletonLayout         layout
    ) {
        final var map       = ImmutableMap.<String, ModelSkeleton.Bone>builder();
        final var list      = new ArrayList<ModelSkeleton.Bone>(layout.size());

        final var queue     = new ObjectArrayFIFOQueue<ModelSkeleton.Bone>();
        queue.enqueue(new ModelSkeleton.Bone("root", Part.of(root), layout.id("root")));

        while (!queue.isEmpty()) {
            var entry        = queue.dequeue();
            var children     = getChildren(((Part) entry.part()).part);

            for (var child : children.entrySet()) {
                final var name = child.getKey();
                final var id = layout.id(name);

                var childEntry = new ModelSkeleton.Bone(name, Part.of(child.getValue()), id);

                if (id != -1) {
                    map.put(childEntry.name(), childEntry);
                    list.add(childEntry);
                }

                queue.enqueue(childEntry);
            }
        }

        list.sort(Comparator.comparingInt(ModelSkeleton.Bone::id));

        return new ModelSkeleton(
                layout,
                map.build(),
                ImmutableList.copyOf(list)
        );
    }

    public static ModelSkeleton manual(
            @NonNull ModelPart              root,
            @NonNull SkeletonLayout         layout,
            @NonNull String @NonNull []     names
    ) {
        final var map       = ImmutableMap.<String, ModelSkeleton.Bone>builder();
        final var list      = new ArrayList<ModelSkeleton.Bone>(layout.size());

        final var lookup    = root.createPartLookup();

        for (var name : names) {
            final var part      = "root".equals(name) ? root : lookup.apply(name);
            final var delegate  = Part.of(part);
            final var entry     = new ModelSkeleton.Bone(name, delegate, layout.id(name));
            map.put(entry.name(), entry);
            list.add(entry);
        }

        list.sort(Comparator.comparingInt(ModelSkeleton.Bone::id));

        return new ModelSkeleton(
                layout,
                map.build(),
                list
        );
    }

    private static final class Part implements ModelSkeleton.Delegate {

        private final ModelPart part;
        @Getter
        private final ModelSkeleton.Delegate initialPose;

        private static Part of(ModelPart part) {
            return new Part(part);
        }

        private Part(ModelPart part) {
            this.part = part;

            final var pose = part.getInitialPose();
            this.initialPose = new Initial(pose);
        }

        @Override
        public void setPosition(final float x, final float y, final float z) {
            part.setPos(x, y, z);
        }

        @Override
        public void setRotation(final float x, final float y, final float z) {
            part.setRotation(x, y, z);
        }

        @Override
        public void setScale(final float x, final float y, final float z) {
            part.xScale = x;
            part.yScale = y;
            part.zScale = z;
        }

        @Override
        public float x() {
            return this.part.x;
        }

        @Override
        public float y() {
            return this.part.y;
        }

        @Override
        public float z() {
            return this.part.z;
        }

        @Override
        public float xRot() {
            return this.part.xRot;
        }

        @Override
        public float yRot() {
            return this.part.yRot;
        }

        @Override
        public float zRot() {
            return this.part.zRot;
        }

        @Override
        public float xScale() {
            return this.part.xScale;
        }

        @Override
        public float yScale() {
            return this.part.yScale;
        }

        @Override
        public float zScale() {
            return this.part.zScale;
        }

        private static final class Initial implements ModelSkeleton.Delegate {
            private final PartPose pose;

            public Initial(final PartPose pose) {
                this.pose = pose;
            }

            @Override
            public void setPosition(final float x, final float y, final float z) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void setRotation(final float x, final float y, final float z) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void setScale(final float x, final float y, final float z) {
                throw new UnsupportedOperationException();
            }

            @Override
            public float x() {
                return pose.x();
            }

            @Override
            public float y() {
                return pose.y();
            }

            @Override
            public float z() {
                return pose.z();
            }

            @Override
            public float xRot() {
                return pose.xRot();
            }

            @Override
            public float yRot() {
                return pose.yRot();
            }

            @Override
            public float zRot() {
                return pose.zRot();
            }

            @Override
            public float xScale() {
                return pose.xScale();
            }

            @Override
            public float yScale() {
                return pose.yScale();
            }

            @Override
            public float zScale() {
                return pose.zScale();
            }

            @Override
            public ModelSkeleton.Delegate getInitialPose() {
                throw new UnsupportedOperationException();
            }
        }
    }

    private static Map<String, ModelPart> getChildren(ModelPart thiz) {
        return ((ModelPartAccessor) (Object) thiz).getChildren();
    }
}
