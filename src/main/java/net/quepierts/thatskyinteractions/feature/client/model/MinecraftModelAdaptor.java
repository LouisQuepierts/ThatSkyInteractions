package net.quepierts.thatskyinteractions.feature.client.model;

import it.unimi.dsi.fastutil.objects.ObjectArrayFIFOQueue;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.minecraft.client.model.geom.ModelPart;
import net.quepierts.thatskyinteractions.feature.mixin.vanilla.accessor.ModelPartAccessor;
import net.quepierts.thatskyinteractions.infra.animation.adapter.AnimationOutput;
import net.quepierts.thatskyinteractions.infra.animation.adapter.ChannelBinding;
import net.quepierts.thatskyinteractions.infra.animation.backend.channel.ChannelLayout;
import net.quepierts.thatskyinteractions.infra.animation.backend.pipeline.AnimationResultView;
import org.jspecify.annotations.NonNull;

import java.util.Map;

@Log4j2
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class MinecraftModelAdaptor implements AnimationOutput {

    private static final String ROOT = "root";

    private final ChannelBinding binding;

    public static @NonNull MinecraftModelAdaptor auto(
            @NonNull ModelPart              root,
            @NonNull ChannelLayout          layout
    ) {

        var builder                         = ChannelBinding.builder();
        var queue                           = new ObjectArrayFIFOQueue<Map.Entry<String, ModelPart>>();

        getChildren(root)                   .entrySet()
                                            .forEach(queue::enqueue);

        bind(ROOT, root, layout, builder);
        while (!queue.isEmpty()) {
            var entry                       = queue.dequeue();
            getChildren(entry.getValue())   .entrySet()
                                            .forEach(queue::enqueue);
            bind(entry.getKey(), entry.getValue(), layout, builder);
        }

        return new MinecraftModelAdaptor(builder.build());

    }

    public static @NonNull MinecraftModelAdaptor manual(
            @NonNull ModelPart              root,
            @NonNull ChannelLayout          layout,
            @NonNull String @NonNull []     parts
    ) {
        var builder     = ChannelBinding.builder();
        var lookup      = root.createPartLookup();

        for (var name   : parts) {

            if (ROOT    .equals(name)) {
                bind(ROOT, root, layout, builder);
                continue;
            }

            var part    = lookup.apply(name);
            if (part    == null) {
                log     .warn("Part not found: {}", name);
                continue;
            }

            bind(name, part, layout, builder);
        }

        var binding     = builder.build();
        return          new MinecraftModelAdaptor(binding);
    }

    private static void bind(
            @NonNull String                 name,
            @NonNull ModelPart              part,
            @NonNull ChannelLayout          layout,
            ChannelBinding.@NonNull Builder builder
    ) {
        builder.bind(layout.id(name + ".position"), PositionAccessor.of(part));
        builder.bind(layout.id(name + ".rotation"), RotationAccessor.of(part));
        builder.bind(layout.id(name + ".scale"),    ScaleAccessor.of(part));
    }

    private static Map<String, ModelPart> getChildren(ModelPart thiz) {
        return ((ModelPartAccessor) (Object) thiz).getChildren();
    }

    @Override
    public void accept(@NonNull AnimationResultView buffer) {
        this.binding.apply(buffer);
    }
}
