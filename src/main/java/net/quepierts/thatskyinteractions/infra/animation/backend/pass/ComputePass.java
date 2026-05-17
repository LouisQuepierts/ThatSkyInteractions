package net.quepierts.thatskyinteractions.infra.animation.backend.pass;

import lombok.RequiredArgsConstructor;
import net.quepierts.thatskyinteractions.infra.animation.backend.buffer.AnimationBuffer;
import net.quepierts.thatskyinteractions.infra.animation.backend.pipeline.AnimationContext;
import net.quepierts.thatskyinteractions.infra.animation.backend.pipeline.AnimationFrameBuffer;
import org.jetbrains.annotations.NotNull;

public final class ComputePass extends AnimationPass {

    private final Operation[] operations;

    public ComputePass(
            final String name,
            final Operation[] operations
    ) {
        super(name);
        this.operations = operations;
    }

    @Override
    public void execute(@NotNull AnimationContext context) {

        for (int i = 0; i < operations.length; i++) {
            if (!context.getOperationMask(i)) {
                continue;
            }

            var operation = operations[i];

            switch (operation.type()) {
                case SAMPLE: {
                    var sampler     = context.getSampler(operation.src0());
                    var buffer      = context.getFrameBuffer(operation.dst());

                    sampler         .sample(context, buffer);
                    break;
                }
                case BLEND_P: {
                    var src0        = context.getFrameBuffer(operation.src0());
                    var src1        = context.getFrameBuffer(operation.src1());
                    var dst         = context.getFrameBuffer(operation.dst());

                    AnimationFrameBuffer.blend(src0, src1, dst, operation.param0());
                    break;
                }
                case BLEND_A: {
                    var src0        = context.getFrameBuffer(operation.src0());
                    var src1        = context.getFrameBuffer(operation.src1());
                    var dst         = context.getFrameBuffer(operation.dst());
                    var weight      = context
                                    .getUniform()
                                    .readFloat(operation.arg0());

                    AnimationFrameBuffer.blend(src0, src1, dst, weight);
                    break;
                }
                case ASSIGN: {
                    var src         = context.getFrameBuffer(operation.src0());
                    var dst         = context.getFrameBuffer(operation.dst());

                    AnimationFrameBuffer.memcpy(dst, src);
                    break;
                }
            }

        }

    }
}
