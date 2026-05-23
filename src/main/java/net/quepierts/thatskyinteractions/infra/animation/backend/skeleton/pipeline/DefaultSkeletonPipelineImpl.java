package net.quepierts.thatskyinteractions.infra.animation.backend.skeleton.pipeline;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.quepierts.thatskyinteractions.infra.animation.backend.buffer.AnimationBuffer;
import net.quepierts.thatskyinteractions.infra.animation.backend.execution.ExecutionReflection;
import net.quepierts.thatskyinteractions.infra.animation.backend.pipeline.AnimationResultView;
import net.quepierts.thatskyinteractions.infra.animation.backend.skeleton.SkeletonLayout;
import net.quepierts.thatskyinteractions.infra.animation.backend.skeleton.pass.SkeletonPass;
import net.quepierts.thatskyinteractions.infra.animation.backend.skeleton.pass.definition.SkeletonPassDefinition;
import net.quepierts.thatskyinteractions.infra.animation.backend.uniform.UboDefinition;
import net.quepierts.thatskyinteractions.infra.animation.backend.uniform.UniformBuffer;
import net.quepierts.thatskyinteractions.infra.animation.backend.uniform.UniformReader;
import net.quepierts.thatskyinteractions.infra.animation.backend.uniform.UniformType;
import net.quepierts.thatskyinteractions.infra.animation.core.SkeletonState;
import net.quepierts.thatskyinteractions.infra.animation.core.adapter.AnimationOutput;
import net.quepierts.thatskyinteractions.infra.util.LocationLookup;
import org.joml.Quaternionf;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public final class DefaultSkeletonPipelineImpl implements SkeletonPipeline {

    @Getter
    private final SkeletonLayout            layout;

    private final SkeletonPass[]            passes;

    private final SkeletonPoseProvider[]    providers;

    private final SkeletonResultView        result;
    private final SkeletonPoseBuffer[]      buffers;

    private final UniformBuffer[]           ubos;

    private final SkeletonOutput[]          targets;

    @Getter
    private final UniformBuffer             uniform;

    @Getter
    private final AnimationOutput           adapter;

    private final Reflection                reflection;

    private final Context                   context = new Context(this);

    private DefaultSkeletonPipelineImpl(
            final SkeletonLayout    layout,
            final SkeletonPass[]    passes,
            final UboDefinition     definition,
            final Reflection        reflection
    ) {
        this.layout                 = layout;
        this.passes                 = passes;
        this.providers              = new SkeletonPoseProvider[reflection.providers.size()];

        this.reflection             = reflection;
        final var bufferAmount      = reflection.providers.size();
        final var boneAmount        = layout.size();
        final var bufferSize        = boneAmount * SkeletonLayout.BONE_SIZE;
        final var baseBuffer        = new AnimationBuffer(bufferAmount * bufferSize);
        this.buffers                = new SkeletonPoseBuffer[bufferAmount];

        for (int i = 0; i < bufferAmount; i++) {
            this.buffers[i] = new SkeletonPoseBuffer(baseBuffer, layout, i * bufferSize);
        }

        this.result                 = this.buffers[1];

        this.ubos                   = new UniformBuffer[reflection.ubos.size()];

        this.uniform               = new UniformBuffer(definition);

        this.targets               = new SkeletonOutput[1];
        this.adapter               = new FlatAdapter(this.buffers[0], boneAmount);
    }

    @Override
    public void submit(@NonNull SkeletonState state) {
        var context     = this.context;
        context.state   = state;

        var passes  = this.passes;
        var pid     = 0;

        try {
            for (; pid < passes.length; pid++) {
                final var pass = passes[pid];
                pass.execute(this.context);
            }
        } catch (Exception e) {
            final var pass = passes[pid];
            final var name = pass.getName();

            // print name, pid, and exception
            log.error("Pass '{}' (PID: {}) failed:", name, pid, e);
        }

        context.state   = null;
        this.targets[0] .accept(this.result);
    }

    @Override
    public void bindProvider(
            final String name,
            final SkeletonPoseProvider poseProvider
    ) {
        var location            = this.reflection.providers.find(name);
        if (location == -1) {
            log.error("Provider '{}' not found.", name);
            return;
        }

        this.providers[location] = poseProvider;
    }

    @Override
    public void bindProvider(
            final int location,
            final SkeletonPoseProvider poseProvider
    ) {
        this.providers[location] = poseProvider;
    }

    @Override
    public void bindUbo(
            final String name,
            final UniformBuffer buffer
    ) {
        var location            = this.reflection.ubos.find(name);
        if (location == -1) {
            log.error("UBO '{}' not found.", name);
            return;
        }

        this.bindUbo(location, buffer);
    }

    @Override
    public void bindUbo(
            final int location,
            final UniformBuffer buffer
    ) {
        this.ubos[location]         = buffer;
    }

    @Override
    public void bindTarget(
            final String name,
            final SkeletonOutput target
    ) {
        // todo: MRT
        this.targets[0]             = target;
    }

    @Override
    public void bindTarget(
            final int location,
            final SkeletonOutput target
    ) {
        // todo: MRT
        this.targets[0] = target;
    }

    @Override
    public ExecutionReflection getReflection() {
        return this.reflection;
    }

    public static Compiler compiler() {
        return new Compiler();
    }

    public static final class Compiler {

        private SkeletonLayout layout;
        private final List<SkeletonPassDefinition>      passes      = new ArrayList<>();
        private final UboDefinition.Builder             uniforms    = UboDefinition.builder();
        private final List<String>                      inputs      = new ArrayList<>();
        private final List<String>                      buffers     = new ArrayList<>();
        private final List<String>                      ubos        = new ArrayList<>();

        private Compiler() {
            this.buffers.add(INPUT_BUFFER);
            this.buffers.add(OUTPUT_BUFFER);
        }

        public Compiler withLayout(@NonNull SkeletonLayout layout) {
            this.layout = layout;
            return this;
        }

        public Compiler withPass(@NonNull SkeletonPassDefinition pass) {
            this.passes.add(pass);
            return this;
        }

        public Compiler withProvider(@NonNull String name) {
            this.inputs.add(name);
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

        public Compiler withUniform(String name) {
            this.ubos.add(name);
            return this;
        }

        public SkeletonPipeline compile() {

            if (this.layout == null) {
                throw new IllegalStateException("Layout is not set");
            }

            if (this.passes.isEmpty()) {
                throw new IllegalStateException("No passes are added.");
            }

            var uniform         = this.uniforms.build();

            var providerName    = LocationLookup.of(this.inputs);
            var bufferName      = LocationLookup.of(this.buffers);
            var uboNames        = LocationLookup.of(this.ubos);

            var oids            = new ArrayList<String>();

            var context         = new SkeletonPipelineCompileContext(
                                this.layout,
                                providerName,
                                bufferName,
                                uniform.getLookup(),
                                uboNames,
                                oids
            );

            var passes          = new SkeletonPass[this.passes.size()];
            for (var i = 0; i < passes.length; i++) {
                context         .oidObject(passes[i].getName());
                passes[i]       = this.passes.get(i).compile(context);
            }

            if (context.hasErrors()) {
                log.error("Pipeline compile failed.");
                context.printErrors(log::error);
                throw new IllegalStateException("Pipeline compile failed.");
            }

            var reflection = new Reflection(
                    bufferName,
                    providerName,
                    uboNames,
                    uniform.getLookup(),
                    LocationLookup.of(oids)
            );

            return new DefaultSkeletonPipelineImpl(
                    this.layout,
                    passes,
                    uniform,
                    reflection
            );
        }
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class FlatAdapter implements AnimationOutput {

        private final SkeletonPoseBuffer buffer;
        private final int               channels;

        private int ptr;

        @Override
        public void accept(@NonNull final AnimationResultView buffer) {
            this.ptr = 0;
            for (var i = 0; i < this.channels; i++) {
                buffer.read(i * 3, this::position);
                buffer.read(i * 3 + 1, this::rotation);
                buffer.read(i * 3 + 2, this::scale);
            }
        }

        private void position(float x, float y, float z, float w) {
            if (Float.isNaN(x)) {
                this.buffer.write(this.ptr, 0f, 0f, 0f);
            } else {
                this.buffer.write(this.ptr, x, y, z);
            }
            this.ptr += 4;
        }

        // input: euler
        // output: quaternion
        private void rotation(float x, float y, float z, float w) {
            if (Float.isNaN(x)) {
                this.buffer.write(this.ptr, 0f, 0f, 0f, 1f);
            } else {
                // parse euler (x0, y0, z0) -> quaternion
                var q = new Quaternionf();
                q.rotateZYX(z, y, x);
                this.buffer.write(this.ptr, q.x(), q.y(), q.z(), q.w());
            }
            this.ptr += 4;
        }

        private void scale(float x, float y, float z, float w) {
            if (Float.isNaN(x)) {
                this.buffer.write(this.ptr, 1f, 1f, 1f);
            } else {
                this.buffer.write(this.ptr, x, y, z);
            }
            this.ptr += 4;
        }
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class Context implements SkeletonContext {

        private final DefaultSkeletonPipelineImpl   pipeline;
        private SkeletonState                       state;

        @Override
        public @NonNull SkeletonLayout getLayout() {
            return this.pipeline.layout;
        }

        @Override
        public @NonNull SkeletonState getState() {
            return this.state;
        }

        @Override
        public @NonNull SkeletonPoseProvider getProvider(final int location) {
            return this.pipeline.providers[location];
        }

        @Override
        public @NonNull SkeletonPoseBuffer getPoseBuffer(final int location) {
            return this.pipeline.buffers[location];
        }

        @Override
        public @NonNull UniformReader getUniform() {
            return this.pipeline.uniform;
        }

        @Override
        public @NonNull UniformReader getUniformBuffer(final int location) {
            return this.pipeline.ubos[location];
        }
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class Reflection implements ExecutionReflection {

        private final LocationLookup buffers;
        private final LocationLookup providers;
        private final LocationLookup uniform;
        private final LocationLookup ubos;

        private final LocationLookup oid;

        @Override
        public int oid(final @NonNull String semantic) {
            return this.oid.find(semantic);
        }

        @Override
        public int location(final @NonNull String semantic) {
            final var args      = semantic.split("\\.");
            final var namespace = args[0];

            return switch (namespace) {
                case "buffer"   -> this.buffers.find(args[1]);
                case "provider" -> this.providers.find(args[1]);
                case "uniform"  -> this.uniform.find(args[1]);
                case "ubo"      -> this.ubos.find(args[1]);
                default         -> -1;
            };
        }
    }

}
