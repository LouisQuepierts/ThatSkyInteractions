package net.quepierts.thatskyinteractions.infra.animation.backend.pass.definition;

import net.quepierts.thatskyinteractions.infra.animation.backend.pass.AnimationPass;
import net.quepierts.thatskyinteractions.infra.animation.backend.pass.Operation;
import net.quepierts.thatskyinteractions.infra.animation.backend.pass.ComputePass;
import net.quepierts.thatskyinteractions.infra.animation.backend.pipeline.PipelineCompileContext;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public final class ComputePassDefinition extends AnimationPassDefinition {

    private final List<OperationDescription> operations = new ArrayList<>();

    public ComputePassDefinition(String name) {
        super(name);
    }

    public ComputePassDefinition sample(
            String sampler,
            String buffer
    ) {
        this.operations         .add(new OperationDescription(
                Operation.Type.SAMPLE,
                buffer,
                sampler,
                null,
                null,
                null,
                0.0f,
                0.0f
        ));
        return this;
    }

    public ComputePassDefinition blend(
            String src0,
            String src1,
            String dst,
            String weight
    ) {
        this.operations         .add(new OperationDescription(
                Operation.Type.BLEND_A,
                dst,
                src0,
                src1,
                weight,
                null,
                0.0f,
                0.0f
        ));
        return this;
    }

    public ComputePassDefinition blend(
            String src0,
            String src1,
            String dst,
            float weight
    ) {
        this.operations         .add(new OperationDescription(
                Operation.Type.BLEND_P,
                dst,
                src0,
                src1,
                null,
                null,
                weight,
                0.0f
        ));

        return this;
    }

    public ComputePassDefinition assign(
            String src,
            String dst
    ) {
        this.operations         .add(new OperationDescription(
                Operation.Type.ASSIGN,
                dst,
                src,
                null,
                null,
                null,
                0.0f,
                0.0f
        ));
        return this;
    }

    @Override
    public AnimationPass compile(@NotNull PipelineCompileContext context) {
        var operations      = new Operation[this.operations.size()];

        for (int i = 0;
             i < operations.length;
             i++
        ) {
            var operation   = this.operations.get(i);

            operations[i] = switch (operation.type()) {
                case SAMPLE -> Operation.sample(
                        context.getSamplerLocation(operation.src0()),
                        context.getBufferLocation(operation.dst())
                );
                case BLEND_P -> Operation.blend(
                        context.getBufferLocation(operation.src0()),
                        context.getBufferLocation(operation.src1()),
                        context.getBufferLocation(operation.dst()),
                        operation.param0()
                );
                case BLEND_A -> Operation.blend(
                        context.getBufferLocation(operation.src0()),
                        context.getBufferLocation(operation.src1()),
                        context.getBufferLocation(operation.dst()),
                        context.getUniformLocation(operation.arg0())
                );
                case ASSIGN -> Operation.assign(
                        context.getBufferLocation(operation.src0()),
                        context.getBufferLocation(operation.dst())
                );
                case CUSTOM -> throw new UnsupportedOperationException("Custom operations are not supported yet.");
            };
        }

        return new ComputePass(operations);
    }

    private record OperationDescription(
            Operation.Type type,

            String dst,
            String src0,
            String src1,

            String arg0,
            String arg1,

            float param0,
            float param1
    ) {

    }

}
