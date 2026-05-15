package net.quepierts.thatskyinteractions.feature.client.model;

import it.unimi.dsi.fastutil.objects.ObjectArrayFIFOQueue;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import net.minecraft.client.model.geom.ModelPart;
import net.quepierts.thatskyinteractions.feature.mixin.vanilla.client.accessor.ModelPartAccessor;
import net.quepierts.thatskyinteractions.infra.animation.core.adapter.PipelineInputProvider;
import net.quepierts.thatskyinteractions.infra.animation.core.adapter.PropertyProvider;
import net.quepierts.thatskyinteractions.infra.animation.backend.buffer.WritableBuffer;
import net.quepierts.thatskyinteractions.infra.animation.backend.channel.ChannelLayout;
import org.jspecify.annotations.NonNull;

import java.util.Map;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class MinecraftModelInputProvider implements PipelineInputProvider {

    private final PropertyProvider[] providers;

    public static @NonNull MinecraftModelInputProvider auto(
            @NonNull ModelPart              root,
            @NonNull ChannelLayout          layout
    ) {
        var providers                       = new PropertyProvider[layout.getChannelCount()];
        var queue                           = new ObjectArrayFIFOQueue<Map.Entry<String, ModelPart>>();

        getChildren(root)                   .entrySet()
                                            .forEach(queue::enqueue);

        while (!queue.isEmpty()) {
            var entry                       = queue.dequeue();
            var part                        = entry.getValue();
            getChildren(entry.getValue())   .entrySet()
                                            .forEach(queue::enqueue);

            var position                    = entry.getKey() + ".position";
            var rotation                    = entry.getKey() + ".rotation";

            var positionId                  = layout.id(position);
            var rotationId                  = layout.id(rotation);

            if (positionId != -1)           providers[positionId] = PositionProvider.of(part, position);
            if (rotationId != -1)       providers[rotationId] = RotationProvider.of(part, rotation);
        }

        return new MinecraftModelInputProvider(providers);
    }

    @Override
    public void fill(final int channel, final int offset, final WritableBuffer out) {
        final var provider = this.providers[channel];

        if (provider != null) {
            provider.write(offset, out);
        }
    }

    private static Map<String, ModelPart> getChildren(ModelPart thiz) {
        return ((ModelPartAccessor) (Object) thiz).getChildren();
    }
}
