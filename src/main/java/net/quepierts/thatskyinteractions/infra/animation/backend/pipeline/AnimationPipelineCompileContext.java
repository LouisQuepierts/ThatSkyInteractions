package net.quepierts.thatskyinteractions.infra.animation.backend.pipeline;

import lombok.RequiredArgsConstructor;
import net.quepierts.thatskyinteractions.infra.animation.backend.Patterns;
import net.quepierts.thatskyinteractions.infra.util.LocationLookup;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@RequiredArgsConstructor
public final class AnimationPipelineCompileContext {

    public static final String ORIGINAL_SAMPLER     = "Pipeline.OriginSampler";
    public static final String OUTPUT_BUFFER        = "Pipeline.ResultBuffer";

    private final LocationLookup samplers;
    private final LocationLookup buffers;
    private final LocationLookup uniforms;
    private final LocationLookup ubos;

    private final List<String> oidObjects;

    private final List<String> errors       = new ArrayList<>();

    public int getSamplerLocation(String name) {
        if (ORIGINAL_SAMPLER.equals(name)) {
            return 0;
        }

        var index   = this.samplers.find(name);

        if (index == -1) {
            this.error("Sampler '" + name + "' not found.");
        }

        return index;
    }

    public int getBufferLocation(String name) {
        if (OUTPUT_BUFFER.equals(name)) {
            return 0;
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

    public int oidObject(final @Nullable String name) {
        if (    name != null &&
                !Patterns.PATTERN_SEMANTIC
                        .matcher(name)
                        .matches()) {
            this.error("Invalid semantic: " + name);
            return -1;
        }
        final var semantic  = name == null ?
                                "pid#" + this.oidObjects.size() :
                                name;
        final var pid       = this.oidObjects.size();
        this.oidObjects.add(semantic);

        return pid;
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
