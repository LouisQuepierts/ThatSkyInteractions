package net.quepierts.thatskyinteractions.infra.animation.backend.pipeline;

import lombok.RequiredArgsConstructor;
import net.quepierts.thatskyinteractions.infra.util.LocationLookup;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@RequiredArgsConstructor
public final class PipelineCompileContext {

    public static final String ORIGINAL_SAMPLER     = "Pipeline.OriginSampler";
    public static final String OUTPUT_BUFFER        = "Pipeline.ResultBuffer";

    private final LocationLookup samplers;
    private final LocationLookup buffers;
    private final LocationLookup uniforms;

    private final List<String> errors   = new ArrayList<>();

    public int getSamplerLocation(String name) {
        if (ORIGINAL_SAMPLER.equals(name)) {
            return 0;
        }

        var index   = this.samplers.find(name);

        if (index == -1) {
            this.error("Sampler '" + name + "' not found.");
        }

        return index + 1;
    }

    public int getBufferLocation(String name) {
        if (OUTPUT_BUFFER.equals(name)) {
            return 0;
        }

        var index   = this.buffers.find(name);

        if (index == -1) {
            this.error("Buffer '" + name + "' not found.");
        }

        return index + 1;
    }

    public int getUniformLocation(String name) {
        var index   = this.uniforms.find(name);

        if (index == -1) {
            this.error("Uniform '" + name + "' not found.");
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
