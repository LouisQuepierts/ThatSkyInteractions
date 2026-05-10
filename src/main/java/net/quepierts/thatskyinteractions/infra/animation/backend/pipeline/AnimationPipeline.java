package net.quepierts.thatskyinteractions.infra.animation.backend.pipeline;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.quepierts.thatskyinteractions.infra.animation.adapter.AnimationOutput;
import net.quepierts.thatskyinteractions.infra.animation.backend.buffer.AnimationBuffer;
import net.quepierts.thatskyinteractions.infra.animation.backend.channel.ChannelFormat;
import net.quepierts.thatskyinteractions.infra.animation.backend.channel.ChannelLayout;
import net.quepierts.thatskyinteractions.infra.animation.backend.channel.DefaultChannelFormats;
import net.quepierts.thatskyinteractions.infra.animation.backend.pass.AnimationPass;
import net.quepierts.thatskyinteractions.infra.animation.backend.pass.definition.AnimationPassDefinition;
import net.quepierts.thatskyinteractions.infra.animation.backend.sampler.AnimationSampler;
import net.quepierts.thatskyinteractions.infra.animation.backend.uniform.UboDefinition;
import net.quepierts.thatskyinteractions.infra.animation.backend.uniform.UniformBuffer;
import net.quepierts.thatskyinteractions.infra.animation.backend.uniform.UniformReader;
import net.quepierts.thatskyinteractions.infra.animation.backend.uniform.UniformType;
import net.quepierts.thatskyinteractions.infra.animation.runtime.AnimationState;
import net.quepierts.thatskyinteractions.infra.util.LocationLookup;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public final class AnimationPipeline {

    @Getter
    private final ChannelFormat             channelFormat;

    private final AnimationPass[]           passes;

    @Getter
    private final LocationLookup            bufferLookup;
    private final AnimationResultView       result;
    private final AnimationFrameBuffer[]    buffers;

    @Getter
    private final LocationLookup            samplerLookup;
    private final AnimationSampler<?>[]     samplers;

    @Getter
    private final LocationLookup            uboLookup;
    private final UniformBuffer[]           ubos;

    @Getter
    private final UniformBuffer             uniform;

    private final Context                   context = new Context(this);

    public void submit(
            AnimationState      state,
            AnimationOutput     output
    ) {
        var context     = this.context;
        context.state   = state;

        for (var pass : this.passes) {
            pass.execute(context);
        }

        output.accept(this.result);

        context.state   = null;
    }

    public void bindSource(
            String                  name,
            AnimationSampler<?>     sampler
    ) {
        var location            = this.samplerLookup.find(name);
        if (location == -1) {
            log.error("Sampler '{}' not found.", name);
            return;
        }

        this.samplers[location] = sampler;
    }

    public void bindUbo(
            String              name,
            UniformBuffer       buffer
    ) {
        var location            = this.uboLookup.find(name);
        if (location == -1) {
            log.error("UBO '{}' not found.", name);
            return;
        }

        this.ubos[location]     = buffer;
    }

    private AnimationContext context(AnimationState state) {
        this.context.state      = state;
        return this.context;
    }

    public static Compiler compiler() {
        return new Compiler();
    }

    public static final class Compiler {

        private ChannelLayout                           layout;
        private ChannelFormat                           format      = DefaultChannelFormats.EMPTY;
        private final List<AnimationPassDefinition>     passes      = new ArrayList<>();
        private final List<String>                      samplers    = new ArrayList<>();
        private final List<String>                      buffers     = new ArrayList<>();
        private final UboDefinition.Builder             uniforms    = UboDefinition.builder();
        private final Map<String, UboDefinition>        ubo         = new Object2ObjectArrayMap<>();

        private Compiler() {
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

            var channelCount    = this.layout.getChannelCount();
            var bufferSize      = channelCount << 2;

            var uniform         = this.uniforms.build();
            var uniformBuffer   = new UniformBuffer(uniform);

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

            int bufferAmount    = this.buffers.size();
            var buffer          = new AnimationBuffer(bufferSize * bufferAmount);
            var buffers         = new AnimationFrameBuffer[bufferAmount];

            for (int i = 0; i < bufferAmount; i++) {
                buffers[i]      = new AnimationFrameBuffer(
                                    buffer,
                                    bufferSize * i,
                                    bufferSize
                                );
            }

            if (context.hasErrors()) {
                context.printErrors(log::error);
                throw new IllegalStateException("Pipeline compile failed.");
            }

            var result          = new AnimationResultBuffer(buffer, bufferSize);

            return new AnimationPipeline(
                    this.format,
                    passes,
                    bufferNames,
                    result,
                    buffers,
                    samplerNames,
                    new AnimationSampler[this.samplers.size()],
                    uboNames,
                    new UniformBuffer[this.ubo.size()],
                    uniformBuffer
            );
        }
    }

    @RequiredArgsConstructor
    private static final class Context implements AnimationContext {

        private final   AnimationPipeline   pipeline;
        private         AnimationState      state;

        @Override
        public float getProgress() {
            return this.state.getProgress();
        }

        @Override
        public ChannelFormat getChannelFormat() {
            return this.pipeline.getChannelFormat();
        }

        @Override
        public AnimationState getAnimationState() {
            return this.state;
        }

        @Override
        public AnimationSampler<?> getSampler(int location) {
            return this.pipeline.samplers[location];
        }

        @Override
        public AnimationFrameBuffer getFrameBuffer(int location) {
            return this.pipeline.buffers[location];
        }

        @Override
        public AnimationBuffer getParameterBuffer() {
            return this.state.getParameterBuffer();
        }

        @Override
        public UniformReader getUniform() {
            return this.pipeline.getUniform();
        }

        @Override
        public boolean getOperationMask(int index) {
            return true; // todo
        }

        @Override
        public boolean getSamplerMask(int channel) {
            return channel != -1; // todu
        }
    }

}
