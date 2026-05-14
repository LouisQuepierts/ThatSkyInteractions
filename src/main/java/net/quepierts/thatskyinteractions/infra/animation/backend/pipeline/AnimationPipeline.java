package net.quepierts.thatskyinteractions.infra.animation.backend.pipeline;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.quepierts.thatskyinteractions.infra.animation.adapter.AnimationOutput;
import net.quepierts.thatskyinteractions.infra.animation.adapter.PipelineInputProvider;
import net.quepierts.thatskyinteractions.infra.animation.backend.buffer.AnimationBuffer;
import net.quepierts.thatskyinteractions.infra.animation.backend.channel.ChannelFormat;
import net.quepierts.thatskyinteractions.infra.animation.backend.channel.ChannelLayout;
import net.quepierts.thatskyinteractions.infra.animation.backend.channel.DefaultChannelFormats;
import net.quepierts.thatskyinteractions.infra.animation.backend.pass.AnimationPass;
import net.quepierts.thatskyinteractions.infra.animation.backend.pass.definition.AnimationPassDefinition;
import net.quepierts.thatskyinteractions.infra.animation.backend.sampler.AnimationSampler;
import net.quepierts.thatskyinteractions.infra.animation.backend.sampler.OriginSampler;
import net.quepierts.thatskyinteractions.infra.animation.backend.uniform.*;
import net.quepierts.thatskyinteractions.infra.animation.runtime.AnimationState;
import net.quepierts.thatskyinteractions.infra.util.LocationLookup;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.*;

@Slf4j
@RequiredArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public final class AnimationPipeline {

    public static final String ORIGINAL_SAMPLER     = "Pipeline.OriginSampler";
    public static final String OUTPUT_BUFFER        = "Pipeline.ResultBuffer";

    @Getter
    private final ChannelFormat             channelFormat;

    @Getter
    private final ChannelLayout             channelLayout;

    private final AnimationPass[]           passes;

    @Getter
    private final LocationLookup            bufferLookup;
    private final AnimationResultBuffer     result;
    private final AnimationFrameBuffer[]    buffers;

    @Getter
    private final LocationLookup            samplerLookup;
    private final AnimationSampler[]        samplers;

    @Getter
    private final LocationLookup            uboLookup;
    private final UniformBuffer[]           ubos;

    @Getter
    private final UniformBuffer             uniform;

    private final Context                   context = new Context(this);

    private AnimationPipeline(
            ChannelFormat       format,
            ChannelLayout       layout,
            AnimationPass[]     passes,
            LocationLookup      bufferNames,
            LocationLookup      samplerNames,
            LocationLookup      uboNames,
            UboDefinition       uniform
    ) {
        this.channelFormat      = format;
        this.channelLayout      = layout;
        this.passes             = passes;
        this.bufferLookup       = bufferNames;
        this.samplerLookup      = samplerNames;
        this.uboLookup          = uboNames;

        var bufferAmount        = bufferNames.size();
        var bufferSize          = layout.getChannelCount() << 2;
        var buffer              = new AnimationBuffer(bufferSize * bufferAmount);
        var buffers             = new AnimationFrameBuffer[bufferAmount];

        for (int i = 0;
             i < bufferAmount;
             i++
        ) {
            buffers[i]          = new AnimationFrameBuffer(
                                    buffer,
                                    bufferSize * i,
                                    bufferSize
            );
        }

        this.result             = new AnimationResultBuffer(buffer, bufferSize);
        this.buffers            = buffers;

        this.samplers           = new AnimationSampler[samplerNames.size()];
        this.ubos               = new UniformBuffer[uboNames.size()];
        this.uniform            = new UniformBuffer(uniform);

        this.samplers[0]        = new OriginSampler();
    }

    public void submit(
            @NonNull    AnimationState          state,
            @NonNull    AnimationOutput         output
    ) {
        submit(state, null, output);
    }

    public void submit(
            @NonNull    AnimationState          state,
            @Nullable   PipelineInputProvider   input,
            @NonNull    AnimationOutput         output
    ) {
        var context     = this.context;
        context.state   = state;
        context.input   = input;

        for (var pass : this.passes) {
            pass.execute(context);
        }

        output.accept(this.result);

        context.state   = null;
        context.input   = null;
    }

    public void bindSource(
            String                  name,
            AnimationSampler        sampler
    ) {
        var location            = this.samplerLookup.find(name);
        if (location == -1) {
            log.error("Sampler '{}' not found.", name);
            return;
        }

        this.bindSource(location, sampler);
    }

    public void bindSource(
            int                     location,
            AnimationSampler        sampler
    ) {
        if (location == 0) {
            log.error("Cannot bind source to location 0.");
            return;
        }

        this.samplers[location] = sampler;
    }

    public void bindUbo(
            String                  name,
            UniformBuffer           buffer
    ) {
        var location            = this.uboLookup.find(name);
        if (location == -1) {
            log.error("UBO '{}' not found.", name);
            return;
        }

        this.bindUbo(location, buffer);
    }

    public void bindUbo(
            int                     location,
            UniformBuffer           buffer
    ) {
        this.ubos[location]     = buffer;
    }

    public static Compiler compiler() {
        return new Compiler();
    }

    public static final class Compiler {

        private ChannelLayout                           layout;
        private ChannelFormat                           format      = DefaultChannelFormats.EMPTY;
        private final List<AnimationPassDefinition>     passes      = new ArrayList<>();
        private final Set<String>                       samplers    = new ObjectArraySet<>();
        private final Set<String>                       buffers     = new ObjectArraySet<>();
        private final UboDefinition.Builder             uniforms    = UboDefinition.builder();
        private final Map<String, UboDefinition>        ubo         = new Object2ObjectArrayMap<>();

        private Compiler() {
            this.samplers.add("Pipeline.OriginSampler");
            this.buffers.add("Pipeline.OutputBuffer");
        }

        public Compiler withChannelLayout(ChannelLayout layout) {
            this.layout = layout;
            return this;
        }

        public Compiler withChannelFormat(ChannelFormat format) {
            this.format = format;
            return this;
        }

        public Compiler withPass(AnimationPassDefinition pass) {
            this.passes.add(pass);
            return this;
        }

        public Compiler withSampler(String name) {
            this.samplers.add(name);
            return this;
        }

        public Compiler withBuffer(String name) {
            this.buffers.add(name);
            return this;
        }

        public Compiler withUniform(String name, UniformType type) {
            this.uniforms.withUniform(name, type);
            return this;
        }

        public Compiler withUniform(String name, UboDefinition definition) {
            this.ubo.put(name, definition);
            return this;
        }

        public AnimationPipeline compile() {

            if (this.layout == null) {
                throw new IllegalStateException("Channel layout is not set.");
            }

            var uniform         = this.uniforms.build();

            var bufferNames     = LocationLookup.of(this.buffers);
            var samplerNames    = LocationLookup.of(this.samplers);
            var uboNames        = LocationLookup.of(this.ubo.keySet());

            var context         = new PipelineCompileContext(
                                samplerNames,
                                bufferNames,
                                uniform.getLookup()
            );

            var passes          = this.passes.stream()
                                .map(def -> def.compile(context))
                                .toArray(AnimationPass[]::new);

            if (context.hasErrors()) {
                context.printErrors(log::error);
                throw new IllegalStateException("Pipeline compile failed.");
            }

            return new AnimationPipeline(
                    this.format,
                    this.layout,
                    passes,
                    bufferNames,
                    samplerNames,
                    uboNames,
                    uniform
            );
        }
    }

    @RequiredArgsConstructor
    private static final class Context implements AnimationContext {

        private final   AnimationPipeline       pipeline;
        private         AnimationState          state;
        private         PipelineInputProvider   input;

        @Override
        public float getProgress() {
            return this.state.getProgress();
        }

        @Override
        public @NonNull ChannelLayout getChannelLayout() {
            return this.pipeline.getChannelLayout();
        }

        @Override
        public @NonNull ChannelFormat getChannelFormat() {
            return this.pipeline.getChannelFormat();
        }

        @Override
        public @NonNull AnimationState getAnimationState() {
            return this.state;
        }

        @Override
        public @NonNull AnimationSampler getSampler(int location) {
            return this.pipeline.samplers[location];
        }

        @Override
        public @NonNull AnimationFrameBuffer getFrameBuffer(int location) {
            return this.pipeline.buffers[location];
        }

        @Override
        public @NonNull AnimationBuffer getParameterBuffer() {
            return this.state.getParameterBuffer();
        }

        @Override
        public @NonNull UniformReader getUniform() {
            return this.pipeline.getUniform();
        }

        @Override
        public @Nullable PipelineInputProvider getInputProvider() {
            return this.input;
        }

        @Override
        public boolean getOperationMask(int index) {
            return true; // todo
        }

        @Override
        public boolean getSamplerMask(int channel) {
            return channel != -1; // todo
        }
    }

}
