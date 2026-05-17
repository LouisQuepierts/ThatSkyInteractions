package net.quepierts.thatskyinteractions.infra.animation.backend.skeleton.pipeline;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import net.quepierts.thatskyinteractions.infra.animation.backend.pipeline.AnimationResultView;
import net.quepierts.thatskyinteractions.infra.animation.backend.skeleton.SkeletonLayout;
import net.quepierts.thatskyinteractions.infra.animation.backend.skeleton.pass.SkeletonPassDefinition;
import net.quepierts.thatskyinteractions.infra.animation.backend.uniform.UboDefinition;
import net.quepierts.thatskyinteractions.infra.animation.backend.uniform.UniformType;
import net.quepierts.thatskyinteractions.infra.animation.core.adapter.AnimationOutput;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class DefaultSkeletonPipelineImpl {



    public static Compiler compiler() {
        return new Compiler();
    }

    @RequiredArgsConstructor(access = lombok.AccessLevel.PRIVATE)
    public static final class Compiler {

        private SkeletonLayout layout;
        private final List<SkeletonPassDefinition>      passes      = new ArrayList<>();
        private final UboDefinition.Builder             uniforms    = UboDefinition.builder();
        private final Set<String>                       buffers     = new ObjectArraySet<>();
        private final Map<String, UboDefinition>        ubo         = new Object2ObjectArrayMap<>();

        public Compiler withLayout(@NonNull SkeletonLayout layout) {
            this.layout = layout;
            return this;
        }

        public Compiler withPass(@NonNull SkeletonPassDefinition pass) {
            this.passes.add(pass);
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

        public SkeletonPipeline compile() {

            if (this.layout == null) {
                throw new IllegalStateException("Layout is not set");
            }

            if (this.passes.isEmpty()) {
                throw new IllegalStateException("No passes are added.");
            }

            throw new UnsupportedOperationException();
        }
    }

    public static final class InputAdapter implements AnimationOutput {

        @Override
        public void accept(@NonNull final AnimationResultView buffer) {

        }
    }

}
