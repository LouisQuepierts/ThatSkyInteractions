package net.quepierts.thatskyinteractions.feature.client.model;

import it.unimi.dsi.fastutil.objects.ObjectArrayFIFOQueue;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.client.model.geom.ModelPart;
import net.quepierts.thatskyinteractions.feature.mixin.vanilla.client.accessor.ModelPartAccessor;
import net.quepierts.thatskyinteractions.infra.animation.backend.skeleton.SkeletonLayout;
import net.quepierts.thatskyinteractions.infra.animation.backend.skeleton.pipeline.*;
import net.quepierts.thatskyinteractions.infra.animation.core.adapter.ChannelBinding;
import net.quepierts.thatskyinteractions.infra.animation.backend.channel.ChannelLayout;
import net.quepierts.thatskyinteractions.infra.animation.core.adapter.SkeletonBinding;
import net.quepierts.thatskyinteractions.infra.animation.core.skeleton.PoseCache;
import org.jspecify.annotations.NonNull;

import java.util.Map;

@Slf4j
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class MinecraftModelAdaptor
        implements SkeletonOutput, SkeletonPoseProvider {

    private static final String ROOT = "root";

    private final SkeletonBinding skeleton;

    public static @NonNull MinecraftModelAdaptor auto(
            @NonNull ModelPart              root,
            @NonNull SkeletonLayout         layout
    ) {

        var builder                         = SkeletonBinding.builder();
        var queue                           = new ObjectArrayFIFOQueue<Map.Entry<String, ModelPart>>();

        getChildren(root)                   .entrySet()
                                            .forEach(queue::enqueue);

        while (!queue.isEmpty()) {
            var entry                       = queue.dequeue();
            final var name                  = entry.getKey();
            final var part                  = entry.getValue();
            getChildren(part)   .entrySet()
                    .forEach(queue::enqueue);

            final var accessor = new ModelTransformAccessor(part);
            builder.bind(
                    layout.id(name),
                    accessor,
                    accessor
            );
        }

        return new MinecraftModelAdaptor(builder.build());

    }

    public static @NonNull MinecraftModelAdaptor manual(
            @NonNull ModelPart              root,
            @NonNull String @NonNull []     parts,
            @NonNull SkeletonLayout         layout
    ) {
        var builder     = SkeletonBinding.builder();
        var lookup      = root.createPartLookup();

        for (var name   : parts) {

            if (ROOT    .equals(name)) {
                final var accessor = new ModelTransformAccessor(root);
                builder.bind(layout.id(name), accessor, accessor);
                continue;
            }

            var part    = lookup.apply(name);
            if (part    == null) {
                log     .warn("Part not found: {}", name);
                continue;
            }

            final var accessor = new ModelTransformAccessor(part);
            builder.bind(layout.id(name), accessor, accessor);

        }

        var binding     = builder.build();
        return          new MinecraftModelAdaptor(binding);
    }

    private static Map<String, ModelPart> getChildren(ModelPart thiz) {
        return ((ModelPartAccessor) (Object) thiz).getChildren();
    }

    @Override
    public void accept(@NonNull final SkeletonResultView view) {
        this.skeleton.apply(view);
    }

    public void accept(@NonNull final PoseCache cache) {
        this.skeleton.apply(cache);
    }

    @Override
    public void fetch(@NonNull final SkeletonContext context, @NonNull final SkeletonPoseBuffer target) {
        this.skeleton.fetch(target);
    }
}
