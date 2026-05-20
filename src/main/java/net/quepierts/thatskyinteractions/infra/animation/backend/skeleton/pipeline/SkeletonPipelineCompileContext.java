package net.quepierts.thatskyinteractions.infra.animation.backend.skeleton.pipeline;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.quepierts.thatskyinteractions.infra.animation.backend.skeleton.SkeletonLayout;
import net.quepierts.thatskyinteractions.infra.util.LocationLookup;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@RequiredArgsConstructor
public final class SkeletonPipelineCompileContext {

    public static final String INPUT_BUFFER         = SkeletonPipeline.INPUT_BUFFER;
    public static final String OUTPUT_BUFFER        = SkeletonPipeline.OUTPUT_BUFFER;

    @Getter
    private final SkeletonLayout layout;

    private final LocationLookup providers;
    private final LocationLookup buffers;
    private final LocationLookup uniforms;
    private final LocationLookup ubos;

    private final List<String> errors   = new ArrayList<>();

    public int getProviderLocation(String name) {
        final var index = this.providers.find(name);

        if (index == -1) {
            this.error("Provider '" + name + "' not found.");
        }

        return index;
    }

    public int getBufferLocation(String name) {
        if (INPUT_BUFFER.equals(name)) {
            return 0;
        } else if (OUTPUT_BUFFER.equals(name)) {
            return 1;
        }

        var index   = this.buffers.find(name);

        if (index == -1) {
            this.error("Buffer '" + name + "' not found.");
        }

        return index;
    }

    public int getUniformLocation(String name) {
        var index   = this.uniforms.find(name);

        if (index == -1) {
            this.error("Uniform '" + name + "' not found.");
        }

        return index;
    }

    public int getUboLocation(String name) {
        var index   = this.ubos.find(name);

        if (index == -1) {
            this.error("UBO '" + name + "' not found.");
        }

        return index;
    }

    public boolean hasErrors() {
        return !this.errors.isEmpty();
    }

    public void printErrors(Consumer<String> printer) {
        this.errors.forEach(printer);
    }

    private void error(String message) {
        this.errors.add(message);
    }
}
