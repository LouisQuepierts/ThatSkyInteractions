package net.quepierts.thatskyinteractions.feature.animation.fk;

import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.core.animation.DefaultMinecraftAnimationPipeline;
import net.quepierts.thatskyinteractions.core.animation.DefaultMinecraftFSM;
import net.quepierts.thatskyinteractions.core.animation.DefaultMinecraftSkeletonPipeline;
import net.quepierts.thatskyinteractions.feature.animation.AnimationResolveContext;
import net.quepierts.thatskyinteractions.feature.animation.HumanoidAnimationState;
import net.quepierts.thatskyinteractions.feature.animation.event.RegisterPlayerAnimationTypeEvent;
import net.quepierts.thatskyinteractions.feature.animation.humanoid.TemplateAnimation;
import net.quepierts.veynir.backend.execution.ExecutionState;
import net.quepierts.veynir.backend.pipeline.AnimationPipeline;
import net.quepierts.veynir.backend.sampler.AnimationSampler;
import net.quepierts.veynir.backend.skeleton.pipeline.SkeletonPipeline;
import net.quepierts.veynir.core.fsm.FSMState;
import net.quepierts.veynir.core.fsm.FiniteStateMachine;
import net.quepierts.veynir.core.model.ParentOverrideConfiguration;
import net.quepierts.veynir.core.skeleton.PoseCache;
import org.jspecify.annotations.NonNull;

import java.util.Collections;

public final class FKAnimation extends TemplateAnimation {

    public static final Identifier LEFT_ARM
            = ThatSkyInteractions.location("fk/left_arm");

    public static final Identifier RIGHT_ARM
            = ThatSkyInteractions.location("fk/right_arm");

    private final FKTargetType type;

    private FKAnimation(
            final FiniteStateMachine            fsm,
            final AnimationPipeline             apl,
            final SkeletonPipeline              spl,
            final ParentOverrideConfiguration   override,
            final AnimationSampler[]            samplers,
            final FKTargetType                  type
    ) {
        super(fsm, apl, spl, override, samplers);
        this.type = type;
    }

    @Override
    public void resolve(
            final @NonNull FSMState                 fsmState,
            final @NonNull ExecutionState           executionState,
            final @NonNull HumanoidAnimationState   animationState,
            final @NonNull PoseCache                target,
            final @NonNull AnimationResolveContext  context
    ) {
        super.resolve(fsmState, executionState, animationState, target, context);
        context.setFkConfiguration(
                this.type,
                1.0f,
                fsmState.getCurrentState() != this.sysExit
        );
    }

    @EventBusSubscriber(modid = ThatSkyInteractions.MODID)
    private static final class Handler {

        private static final ParsingContext LEFT_ARM_CONTEXT = ParsingContext.of(
                DefaultMinecraftFSM.FORWARD_KINEMATICS,
                DefaultMinecraftAnimationPipeline.HUMANOID_TIMELINE,
                DefaultMinecraftSkeletonPipeline.MODIFIED_HUMANOID,
                Collections.singletonList("main"),
                (fsm, apl, spl, override, samplers) -> new FKAnimation(fsm, apl, spl, override, samplers, FKTargetType.LEFT_ARM),
                LinkFallback.DEFAULT
        );

        private static final ParsingContext RIGHT_ARM_CONTEXT = ParsingContext.of(
                DefaultMinecraftFSM.FORWARD_KINEMATICS,
                DefaultMinecraftAnimationPipeline.HUMANOID_TIMELINE,
                DefaultMinecraftSkeletonPipeline.MODIFIED_HUMANOID,
                Collections.singletonList("main"),
                (fsm, apl, spl, override, samplers) -> new FKAnimation(fsm, apl, spl, override, samplers, FKTargetType.RIGHT_ARM),
                LinkFallback.DEFAULT
        );

        @SubscribeEvent
        public static void onRegisterAnimationType(final RegisterPlayerAnimationTypeEvent event) {
            event.register("fk.left_arm", definition -> TemplateAnimation.template(definition, LEFT_ARM_CONTEXT));
            event.register("fk.right_arm", definition -> TemplateAnimation.template(definition, RIGHT_ARM_CONTEXT));
        }

    }

}
